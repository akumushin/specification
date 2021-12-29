package com.vvt.family.web.auth.authorization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vvt.family.domain.entity.User;

import lombok.Data;

@Data
public class UserAuthorization implements UserDetails, CredentialsContainer{
	private static final long serialVersionUID = 1L;
	final private User user;
	private final Set<GrantedAuthority> authorities;
	public UserAuthorization(User user) {
		this.user = user;
		authorities= new HashSet<>();
		if(user.isSuper())
			authorities.add(new SimpleGrantedAuthority("ROLE_SUPER"));
		if(user.isStaff())
			authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));
		authorities.addAll(user.getPermissions());
		user.getPermissionGroups().forEach(group->{
			authorities.addAll(group.getPermissions());
		});
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

	@Override
	public void eraseCredentials() {
		user.setPassword(null);
		
	}
}
