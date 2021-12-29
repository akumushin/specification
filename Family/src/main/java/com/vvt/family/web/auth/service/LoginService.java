package com.vvt.family.web.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vvt.family.domain.entity.User;
import com.vvt.family.domain.entity.UserToken;
import com.vvt.family.domain.repository.user.UserRepositoty;
import com.vvt.family.domain.repository.userToken.UserTokenRepository;
import com.vvt.family.web.auth.authorization.UserAuthorization;

@Service
public class LoginService implements UserDetailsService {
	@Autowired UserRepositoty userRepositoty;
	@Autowired UserTokenRepository tokenRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optional = userRepositoty.findByUsername(username);
		if(!optional.isPresent())
			throw new UsernameNotFoundException("username not found");
		User user = optional.get();
		user.setPermissionGroups(userRepositoty.getPermissionGroupByUserId(user.getId()));
		
		return new UserAuthorization(user);
	}
	
	public User findUserByUsername(String username) {
		Optional<User> optional = userRepositoty.findByUsername(username);
		if(!optional.isPresent())
			return null;
		User user = optional.get();
		user.setPermissionGroups(userRepositoty.getPermissionGroupByUserId(user.getId()));
		
		return user;
	}
	
	public void login(UserDetails userDetails) {
		UsernamePasswordAuthenticationToken auth= new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
		auth.eraseCredentials();
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
	public void login(User user) {
		UserDetails userDetails = new UserAuthorization(user);
		login(userDetails);
	}
	
	public void save(User user) {
		userRepositoty.save(user);
	}
	
	public void saveToken(UserToken ut) {
		tokenRepository.save(ut);
	}
	
	public void deleteToken(String token) {
		tokenRepository.deleteById(token);
	}
	public UserToken getUserToken(String token) {
		return tokenRepository.findById(token).orElse(null);
	}
}
