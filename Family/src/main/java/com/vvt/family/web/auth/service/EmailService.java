package com.vvt.family.web.auth.service;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
    private JavaMailSender javaMailSender;
	
	public boolean sendMailWithTemplate(String template, Map<String, Object> attributes, String toEmail) {
		// build html content
		final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		Context context = new Context(null, attributes);
		context.setVariable("baseUrl", baseUrl);
		String process= templateEngine.process(template, context);
		//send mail
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
		try {
			messageHelper.setSubject("welcom");
			messageHelper.setTo(toEmail);
			messageHelper.setText(process, true);
			javaMailSender.send(mimeMessage);
			return true;
		} catch (MessagingException e) {
			return false;
		}
	}
}
