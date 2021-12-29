package com.vvt.family.domain.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import common.type.Sex;
import lombok.Data;

@Table(name="customer")
@Entity
@Data
public class Customer {
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    @SequenceGenerator(name = "customer_id_seq",allocationSize = 1)
	@Id
	private Integer id;
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	@Column(name="first_name", length = 100)
	private String firstName;
	@Column(name="middle_name", length = 100)
	private String middleName;
	@Column(name="last_name", length = 100)
	private String lastName;
	@Column(name="sex", length=6)
	@Enumerated(EnumType.STRING)
	private Sex sex;
	@Column
	private LocalDate birthday;
	
}
