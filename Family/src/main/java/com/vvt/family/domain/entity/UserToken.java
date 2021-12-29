package com.vvt.family.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import common.type.TokenType;

import lombok.Data;

@Table(name="user_token")
@Entity
@Data
public class UserToken {
	public static long TOKEN_EXPIRED_SECONDS = 60 * 60 *24; //1 day
	@Id
	@Column(length = 255)
	private String id;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	@Column(name="type", length = 50 )
	@Enumerated(EnumType.STRING)
	private TokenType type;
	
	@Column(name="content", length = 500)
	private String content;
	@Column(name="expired_time", nullable = false)
	private LocalDateTime expiredTime;
	
	public static UserToken createUserToken(User user, TokenType type) {
		UserToken ut = new UserToken();
		ut.id = UUID.randomUUID().toString();
		ut.type = type;
		ut.user = user;
		ut.expiredTime = LocalDateTime.now().plusSeconds(TOKEN_EXPIRED_SECONDS);
		return ut;
	}
	
	public boolean isNonExpired() {
		return this.expiredTime.isAfter(LocalDateTime.now());
	}
	
}
