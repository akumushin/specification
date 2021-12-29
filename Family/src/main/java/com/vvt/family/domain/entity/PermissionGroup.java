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

import lombok.Data;

@Table(name="auth_permission_group")
@Entity
@Data
public class PermissionGroup {
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_group_id_seq")
    @SequenceGenerator(name = "permission_group_id_seq",allocationSize = 1)
	@Id
	private Integer id;
	@Column(length = 100)
	private String name;
	
	@ManyToMany
	@JoinTable(name="auth_permission_group_permission",
		joinColumns = @JoinColumn(name="permission_group_id"),
		inverseJoinColumns = @JoinColumn(name="permission_id")
	)
	private List<Permission> permissions;
}
