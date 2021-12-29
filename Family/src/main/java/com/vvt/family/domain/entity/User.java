package com.vvt.family.domain.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import common.filter.FilterColumn;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="auth_user")
@Data
@NoArgsConstructor
public class User {
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq",allocationSize = 1)
	@Id
	private Integer id;
	@Column(length = 100,unique = true, nullable = false)
	private String username;
	@Column(length = 100, nullable = false)
	private String password;
	@Column(length = 255, nullable = false)
	private String email;
	@Column(name="is_staff", nullable =  false)
	private boolean isStaff;
	@Column(name="is_super", nullable = false)
	private boolean isSuper;
	@Column(name="is_enabled", nullable = false)
	private boolean isEnabled;
	
	@ManyToMany
	@JoinTable(name="auth_user_permission",
		joinColumns = @JoinColumn(name="user_id"),
		inverseJoinColumns = @JoinColumn(name="permission_id")
	)
	@Fetch(FetchMode.JOIN)
	private List<Permission> permissions;
	
	@ManyToMany
	@JoinTable(name="auth_user_permission_group",
		joinColumns = @JoinColumn(name="user_id"),
		inverseJoinColumns = @JoinColumn(name="permission_group_id")
	)
	private List<PermissionGroup> permissionGroups;
}
