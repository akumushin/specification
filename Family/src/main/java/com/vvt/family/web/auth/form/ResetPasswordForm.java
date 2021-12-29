package com.vvt.family.web.auth.form;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ResetPasswordForm {
	@NotNull
	private String token;
	@NotNull
	private String newPassword;
	@NotNull
	private String confirmPassword;
}
