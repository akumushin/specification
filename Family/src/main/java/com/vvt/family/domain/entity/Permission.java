package com.vvt.family.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import common.type.EntityName;
import common.type.Privilege;
import lombok.Data;

@Entity
@Table(name="auth_permission")
@Data
public class Permission implements GrantedAuthority{
	private static final long serialVersionUID = 1L;
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_id_seq")
    @SequenceGenerator(name = "permission_id_seq",allocationSize = 1)
	@Id
	private Integer id;
	@Column(name = "entity_name", length = 50)
	@Enumerated(EnumType.STRING)
	private EntityName entityName;
	@Column(name = "privilege", length = 50)
	@Enumerated(EnumType.STRING)
	private Privilege privilege;
	@Override
	public String getAuthority() {
		return entityName +"|"+privilege;
	}
	
}
