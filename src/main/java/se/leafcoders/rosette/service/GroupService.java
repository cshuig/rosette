package se.leafcoders.rosette.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Group;
import se.leafcoders.rosette.model.reference.UserRef;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class GroupService extends MongoTemplateCRUD<Group> {

	@Autowired
	ResourceTypeService resourceTypeService;
	@Autowired
	GroupMembershipService groupMembershipService;
	
	public GroupService() {
		super(Group.class, PermissionType.GROUPS);
	}

	@Override
	public Group create(Group data, HttpServletResponse response) {
		validateUniqueId(data);
		return super.create(data, response);
	}

	@Override
	public void insertDependencies(Group data) {
	}
	
	public boolean containsUsers(String groupId, List<UserRef> users) {
		List<String> userIdsInGroup = groupMembershipService.getUserIdsInGroup(groupId);
		for (UserRef user : users) {
			if (!userIdsInGroup.contains(user.getId())) {
				return false;
			}
		}
		return true;
	}
}
