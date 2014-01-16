package se.ryttargardskyrkan.rosette.model;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "permissions")
public class Permission {
	@Id
	private String id;
	
	@Indexed
	private Boolean everyone;
	
	@Indexed
	private String userId;

	private String userFullName;

	@Indexed
	private String groupId;

	private String groupName;

	private List<String> patterns;
	
	// Getters and setters

	public String getId() {
		return id;
	}

	public Boolean getEveryone() {
		return everyone;
	}

	public void setEveryone(Boolean everyone) {
		this.everyone = everyone;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
}