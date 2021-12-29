package com.vvt.family.web.admin.form;

import lombok.Data;
@Data
public class UserManagerForm {
	private Integer id;
	private String username;
	private String newPassword;
	private String confirmPassword;
	private String email;
	private boolean isStaff;
	private boolean isSuper;
	private boolean isEnabled;
	private int[] permissionIds;
	private int[] permissionGroupIds;
}
