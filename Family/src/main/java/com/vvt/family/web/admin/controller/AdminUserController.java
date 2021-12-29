package com.vvt.family.web.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.vvt.family.web.admin.form.UserManagerForm;

@Controller
public class AdminUserController {
	public String addUserInitial(Model model) {
		UserManagerForm form = new UserManagerForm();
		model.addAttribute("form", form);
		model.addAttribute(null, form);
		return "";
	}
}
