package se.leafcoders.rosette.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.GroupMembership;
import se.leafcoders.rosette.model.Permission;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.security.MongoRealm;
import se.leafcoders.rosette.security.PermissionValue;
import static se.leafcoders.rosette.security.PermissionAction.*;
import static se.leafcoders.rosette.security.PermissionType.*;
import util.QueryId;

@Service
public class PermissionService extends MongoTemplateCRUD<Permission> {

	@Autowired
	private MongoRealm mongoRealm;
	@Autowired
	private UserService userService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private GroupMembershipService groupMembershipService;

	public PermissionService() {
		super(Permission.class, PERMISSIONS);
	}

	@Override
	public Permission create(Permission data, HttpServletResponse response) {
		// Clearing auth cache
		mongoRealm.clearCache(null);

		return super.create(data, response);
	}

	@Override
	public void update(String id, Permission data, HttpServletResponse response) {
		// Clearing auth cache
		mongoRealm.clearCache(null);

		super.update(id, data, response);
	}

	@Override
	public void delete(String id, HttpServletResponse response) {
		// Clearing auth cache
		mongoRealm.clearCache(null);

		super.delete(id, response);
	}

	@Override
	public void insertDependencies(Permission data) {
		if (data.getUser() != null) {
			data.setUser(userService.readAsRef(data.getUser().getId()));
		}
		if (data.getGroup() != null) {
			data.setGroup(groupService.read(data.getGroup().getId()));
		}
	}
	
	public List<String> getForUser(String userId) {
		List<String> permissions = new LinkedList<String>();
		
		// Adding permissions for everyone
		permissions.addAll(getForEveryone());

		// Adding permissions for specified user
		if (userId == null || userId.isEmpty()) {
			return permissions;
		}
		User user = mongoTemplate.findById(userId, User.class);		
		if (user != null) {
			permissions.addAll(getPermissionsForUser(user));
			permissions.addAll(getPermissionsForGroups(user));
		}
		return permissions;
	}
	
	public List<String> getForEveryone() {
		Query query = Query.query(Criteria.where("everyone").is(true));
		Permission permission = mongoTemplate.findOne(query, Permission.class);
		if (permission != null && permission.getPatterns() != null) {
			return permission.getPatterns();
		}
		return new LinkedList<String>();
	}
	
	private List<String> getPermissionsForUser(User user) {
		List<String> permissions = new ArrayList<String>();

		// Add read/update permissions for own user
		permissions.add(new PermissionValue(USERS, READ, user.getId()).toString());
		permissions.add(new PermissionValue(USERS, UPDATE, user.getId()).toString());
		
		// Adding permissions specific for this user
		Query userPermissionQuery = Query.query(Criteria.where("user.id").is(QueryId.get(user.getId())));
		List<Permission> userPermissions = mongoTemplate.find(userPermissionQuery, Permission.class);
		if (userPermissions != null) {
			for (Permission userPermission : userPermissions) {
				if (userPermission.getPatterns() != null) {
					permissions.addAll(userPermission.getPatterns());
				}
			}
		}
		return permissions;
	}

	private List<String> getPermissionsForGroups(User user) {
		List<String> permissions = new ArrayList<String>();

		// Adding permissions from group where user is member
		List<GroupMembership> groupMemberships = groupMembershipService.getForUser(user);
		if (groupMemberships != null) {
			List<String> groupIds = new ArrayList<String>();
			for (GroupMembership groupMembership : groupMemberships) {
				groupIds.add(groupMembership.getGroup().getId());
			}

			// Adding permissions specific for each group
			Query groupPermissionQuery = Query.query(Criteria.where("group.id").in(groupIds));
			List<Permission> groupPermissions = mongoTemplate.find(groupPermissionQuery, Permission.class);
			if (groupPermissions != null) {
				for (Permission groupPermission : groupPermissions) {
					if (groupPermission.getPatterns() != null) {
						permissions.addAll(groupPermission.getPatterns());
					}
				}
			}

			// Add read permission for each user in groups
			List<GroupMembership> groupMemberships2 = groupMembershipService.getForGroupIds(groupIds);
			if (groupMemberships2 != null) {
				for (GroupMembership groupMembership2 : groupMemberships2) {
					permissions.add(new PermissionValue(USERS, READ, groupMembership2.getUser().getId()).toString());
				}
			}
		}
		return permissions;
	}

}
