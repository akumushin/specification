package com.vvt.family.auth.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vvt.family.domain.entity.PermissionGroup;
import com.vvt.family.domain.entity.User;
import com.vvt.family.domain.repository.user.UserRepositoty;
@SpringBootTest
class UserRepositotyTest {
	@Autowired
	UserRepositoty repositoty;
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetPermissionByUserId() {
		User user = repositoty.findById(1).get();
		
		List<PermissionGroup> pg= repositoty.getPermissionGroupByUserId(1);
		pg.forEach(g->{
			System.out.println(g);
		});
		
		user.setPermissionGroups(pg);
		System.out.println(user);
	}

}
