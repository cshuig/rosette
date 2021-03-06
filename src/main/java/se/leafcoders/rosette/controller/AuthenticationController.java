package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.leafcoders.rosette.security.MongoRealm;

@Controller
public class AuthenticationController extends AbstractController {
	@Autowired
	private MongoRealm mongoRealm;
	
	public AuthenticationController() {
		super();
	}

	@RequestMapping(value = "authentication", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getAuthentication(HttpServletResponse response) {
		String responseBody = "";
		
		if ("".equals(SecurityUtils.getSubject().getPrincipal())) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			responseBody = "Unauthorized";
		} else {
			response.setStatus(HttpStatus.OK.value()); 
		}
		return responseBody;
	}
	
	@RequestMapping(value = "authCaches", method = RequestMethod.DELETE, produces = "application/json")
	public void clearAuthCaches(HttpServletResponse response) {
		mongoRealm.clearCache(null);
		
		response.setStatus(HttpStatus.OK.value()); 
	}

}
