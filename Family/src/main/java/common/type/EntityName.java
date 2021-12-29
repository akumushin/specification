package common.type;

public enum EntityName {
	USER("User"),
	PERMISSION("Permission"),
	PERMISSION_GROUP("PermissionGroup"),;
	
	private String value;
	EntityName(String string) {
		value = string;
	}
	public String getValue() {
		return value;
	}
}
