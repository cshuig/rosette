package se.leafcoders.rosette.security;

public enum PermissionType {
	
	ASSETS("assets"),
	BOOKINGS("bookings"),
	EVENTS("events"),
	EVENT_TYPES("eventTypes"),
	EVENT_WEEKS("eventWeeks"),
	GROUP_MEMBERSHIPS("groupMemberships"),
	GROUPS("groups"),
	LOCATIONS("locations"),
	PERMISSIONS("permissions"),
	POSTERS("posters"),
	RESOURCE_TYPES("resourceTypes"),
	SIGNUP_USERS("signupUsers"),
	UPLOAD_FOLDERS("uploadFolders"),
	UPLOADS("uploads"),
	USERS("users");

	private final String type;

	PermissionType(String type) {
		this.type = type;
	}

	public String toString() {
		return type;
	}

}