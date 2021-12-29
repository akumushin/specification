package com.vvt.family.domain.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vvt.family.domain.entity.User;
import com.vvt.family.domain.filter.UserFilter;

import common.filter.CustomSpecification;
@SpringBootTest
class UserRepositotyTest {
	@Autowired UserRepositoty repositoty;
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@Transactional
	void test() {
		UserFilter filter = new UserFilter();
		filter.setId(3);
		filter.setUsername("%t%");
		//filter.setEmail("thuong");
		//filter.setIsStaff(true);
		CustomSpecification<User> specification = new CustomSpecification<User>(filter);
		repositoty.findAll(specification).forEach(item->{
			System.out.println(item);
		});;
	}

}
