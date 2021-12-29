package com.vvt.family.web.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vvt.family.domain.entity.UserToken;
import com.vvt.family.web.auth.form.ResetPasswordForm;
import com.vvt.family.web.auth.service.EmailService;
import com.vvt.family.web.auth.service.LoginService;

import common.type.TokenType;
import common.utils.JsonUtils;

@RequestMapping("/token")
@Controller
public class TokenController {
	@Autowired EmailService emailService;
	@Autowired LoginService userService;
	public static final String PATH="auth/";
	public static final String SUCCESS_URL="/home";
	public static final String TOKEN_SESSION_NAME ="TOKEN";
	/**
	 * 
	 * @param token
	 * @param model
	 * @return
	 */
	@GetMapping("/token/{token:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
	public String receiveToken(@PathVariable("token") String token, Model model, RedirectAttributes redirectAttributes) {
		UserToken userToken= userService.getUserToken(token);
		if(userToken==null) {
			model.addAttribute("msg","token not found");
			return PATH+"token/Message";
		}
		if(!userToken.isNonExpired()) {
			model.addAttribute("msg", "token has been expired");
			return PATH+"token/Message";
		}
		Map	<String, Object> map = JsonUtils.toMap(userToken.getContent());
		switch(userToken.getType()) {
			case ENABLE_USER:
				userToken.getUser().setEnabled(true);
				userService.save(userToken.getUser());
				userService.deleteToken(token);
				return "redirect:/auth/register/complete";
			case CHANGE_EMAIL_COMPLETE:// token sent from new email
				if(map==null || map.get("email")== null) {
					model.addAttribute("msg", "token data error");
					break;
				}
				userToken.getUser().setEmail(map.get("email").toString());
				userService.save(userToken.getUser());
				userService.deleteToken(token);
				return "redirect:/auth/change-email/complete";
			case CHANGE_EMAIL_REQUEST:// token sent from current email
				if(map==null || map.get("email")== null) {
					model.addAttribute("msg", "token data error");
					break;
				}
				UserToken completeToten = UserToken.createUserToken(userToken.getUser(), TokenType.CHANGE_EMAIL_COMPLETE);
				completeToten.setContent(userToken.getContent());
				userService.saveToken(completeToten);
				userService.deleteToken(token);
				
				Map<String, Object> attributes = new HashMap<>();
				attributes.put("user", completeToten.getUser());
				attributes.put("token", completeToten.getId());
				emailService.sendMailWithTemplate("email/UserChangeEmailComplete", attributes, map.get("email").toString());
				return "redirect:/auth/change-email/apply";
			case RESET_PASSWORD:
				ResetPasswordForm form= new ResetPasswordForm();
				form.setToken(token);
				redirectAttributes.addFlashAttribute("form", form);
				return "redirect:/password/reset";
			default:
				
		}
		return PATH+"token/Message";
	}
}
