package com.vvt.family.web.auth.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vvt.family.domain.entity.User;
import com.vvt.family.domain.entity.UserToken;
import com.vvt.family.web.auth.form.ForgetPasswordForm;
import com.vvt.family.web.auth.form.LoginForm;
import com.vvt.family.web.auth.form.ResetPasswordForm;
import com.vvt.family.web.auth.service.EmailService;
import com.vvt.family.web.auth.service.LoginService;

import common.type.TokenType;
@Controller
@RequestMapping("/")
public class LoginController {
	public static final String PATH="auth/";
	public static final String SUCCESS_URL="/home";
	@Autowired LoginService loginService;
	@Autowired HttpSession session;
	@Autowired PasswordEncoder pwdEncoder;
	@Autowired EmailService emailService;
	
	/**
	 * login page
	 * @param model
	 * @return
	 */
	@RequestMapping(value= "/login", method = RequestMethod.GET, name="login")
	public String loginPage(Model model ) {
		model.addAttribute("form", new LoginForm());
		return PATH+ "login/Login";
	}
	/**
	 * login processing
	 * @param model
	 * @param form
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/login")
	public String doLogin(Model model, @ModelAttribute("form") @Validated LoginForm form , BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return PATH+ "login/Login";
		}
		
		User user = loginService.findUserByUsername(form.getUsername());
		if(user==null) {
			bindingResult.addError(new FieldError(bindingResult.getObjectName(),"username","Username not found"));
		}else if(!user.isEnabled()) {
			bindingResult.addError(new ObjectError(bindingResult.getObjectName(), "User is disabled"));
		}else if(!pwdEncoder.matches(form.getPassword(), user.getPassword())) {
			bindingResult.addError(new FieldError(bindingResult.getObjectName(),"password","Your password is incorrect"));
		}else {
			loginService.login(user);
			//redirect
			DefaultSavedRequest savedRequest = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
			if(savedRequest!= null) {
				return "redirect:" + savedRequest.getRedirectUrl();
			}
			return "redirect:" + SUCCESS_URL;
		}
		return PATH+ "login/Login"; 
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/password/forget")
	public String forgetPasswordInitial(Model model) {
		ForgetPasswordForm form = new ForgetPasswordForm();
		model.addAttribute("form", form);
		return PATH +"forget/ForgetPassword";
	}
	
	@PostMapping("/password/forget")
	public String forgetProcessing(Model model, @ModelAttribute("form") @Validated ForgetPasswordForm form, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return PATH +"forget/ForgetPassword";
		}
		User user = loginService.findUserByUsername(form.getUsername());
		if(user==null|| !user.getEmail().equals(form.getEmail())) {
			bindingResult.addError(new ObjectError(bindingResult.getObjectName(), "user not found"));
		}
		
		UserToken userToken = UserToken.createUserToken(user, TokenType.RESET_PASSWORD);
		loginService.saveToken(userToken);
		//send mail
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("user", user);
		attributes.put("token", userToken.getId());
		emailService.sendMailWithTemplate("email/UserForgetPassword", attributes, user.getEmail());
		model.addAttribute("msg", "Your request has been send to your email. Please check email to reset password");
		return PATH+"forget/Message";
	}
	@GetMapping("/password/reset")
	public String resetPassword(Model model, @ModelAttribute("form") ResetPasswordForm form) {
		System.out.println(form);
		return PATH +"forgetResetPassword";
	}
	
	@PostMapping("/password/reset")
	public String resetPasswordProcess(Model model, @ModelAttribute("form")@Validated ResetPasswordForm form, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return PATH +"forget/ResetPassword";
		}
		UserToken userToken = loginService.getUserToken(form.getToken());
		if(userToken==null||!userToken.isNonExpired()|| userToken.getType()!=TokenType.RESET_PASSWORD) {
			model.addAttribute("msg", "Token do not exist");
			return PATH+ "forget/Message";
		}
		userToken.getUser().setPassword(pwdEncoder.encode(form.getNewPassword()));
		loginService.save(userToken.getUser());
		loginService.deleteToken(userToken.getId());
		model.addAttribute("msg", "password has been reset.");
		return PATH+ "forget/Message";
	}
}
