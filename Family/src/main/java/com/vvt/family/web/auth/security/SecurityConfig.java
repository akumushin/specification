package com.vvt.family.web.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vvt.family.web.auth.service.LoginService;
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig  extends WebSecurityConfigurerAdapter{
	@Autowired
	LoginService loginService;
	
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
        .authorizeRequests()
        .anyRequest().permitAll() // ログインしなくてもアクセスできるページ
        //.anyRequest().authenticated() //	以前のページ以外全部はログインしなきゃいけない
        .and()
        .formLogin()
        .loginPage("/login")
        
        .loginProcessingUrl("/loginxx")
        .permitAll()	//	ログインした後redirectページ
        .and()
        .logout()// ログアウトできる
        .permitAll();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(loginService)
        .passwordEncoder(passwordEncoder());
	}
}
