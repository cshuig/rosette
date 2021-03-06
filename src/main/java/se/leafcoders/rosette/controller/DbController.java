package se.leafcoders.rosette.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.exception.ForbiddenException;

@Controller
public class DbController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;

	@RequestMapping(value = "db/rename/{collectionName}", method = RequestMethod.GET)
	@ResponseBody
	public String renameCollection(@PathVariable String collectionName, @RequestParam(required = true) String newName) {
		checkPermission("admin:db");
		mongoTemplate.getCollection(collectionName).rename(newName);
		return "Renamed collection '" + collectionName + "' to '" + newName + "'.\n";
	}

	@RequestMapping(value = "db/copy/{collectionName}", method = RequestMethod.GET)
	@ResponseBody
	public String copyCollection(@PathVariable String collectionName, @RequestParam(required = true) String newName) {
		checkPermission("admin:db");
		if (mongoTemplate.collectionExists(collectionName)) {
			mongoTemplate.getDb().doEval("db." + collectionName + ".copyTo('" + newName + "')");
			return "Copied collection '" + collectionName + "' to '" + newName + "'.\n";
		}
		return "Failed to copy collection '" + collectionName + "'. It doesn't exist.\n";
	}

    protected void checkPermission(String permission) {
        if (!SecurityUtils.getSubject().isPermitted(permission)) {
            throw new ForbiddenException("error.missingPermission", permission);
        }
    }

}
