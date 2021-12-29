package com.vvt.family.web.auth.form;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ForgetPasswordForm {
	@NotNull
	private String username;
	@NotNull
	private String email;
}
