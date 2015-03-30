package se.leafcoders.rosette.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.model.resource.UserResourceType;
import se.leafcoders.rosette.service.SecurityService;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserResourceTypeController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SecurityService security;

	@RequestMapping(value = "userResourceTypes/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public UserResourceType getUserResourceType(@PathVariable String id) {
		checkPermission("read:userResourceTypes:" + id);

        UserResourceType userResourceType = mongoTemplate.findById(id, UserResourceType.class);
		if (userResourceType == null) {
			throw new NotFoundException();
		}
		return userResourceType;
	}

	@RequestMapping(value = "userResourceTypes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<UserResourceType> getUserResourceTypes(HttpServletResponse response) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "sortOrder")));

		List<UserResourceType> userResourceTypesInDatabase = mongoTemplate.find(query, UserResourceType.class);
		List<UserResourceType> userResourceTypes = new ArrayList<UserResourceType>();
		if (userResourceTypesInDatabase != null) {
			for (UserResourceType userResourceTypeInDatabase : userResourceTypesInDatabase) {
				if (isPermitted("read:userResourceTypes:" + userResourceTypeInDatabase.getId())) {
					userResourceTypes.add(userResourceTypeInDatabase);
				}
			}
		}
		return userResourceTypes;
	}

	@RequestMapping(value = "userResourceTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public UserResourceType postUserResourceType(@RequestBody UserResourceType userResourceType, HttpServletResponse response) {
		checkPermission("create:userResourceTypes");
		security.validate(userResourceType);
		
		mongoTemplate.insert(userResourceType);
		
		response.setStatus(HttpStatus.CREATED.value());
		return userResourceType;
	}

	@RequestMapping(value = "userResourceTypes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putUserResourceType(@PathVariable String id, @RequestBody UserResourceType userResourceType, HttpServletResponse response) {
		checkPermission("update:userResourceTypes:" + id);
		security.validate(userResourceType);

		Update update = new Update();
		update.set("name", userResourceType.getName());
		update.set("group", userResourceType.getGroup());
//TODO:...        update.set("sortOrder", userResourceType.getSortOrder());

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(new ObjectId(id))), update, UserResourceType.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "userResourceTypes/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
		checkPermission("delete:userResourceTypes:" + id);

        UserResourceType deletedUserResourceType = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(new ObjectId(id))), UserResourceType.class);
		if (deletedUserResourceType == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());
		}
	}
}
