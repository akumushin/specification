package com.vvt.family.domain.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vvt.family.domain.entity.User;
import com.vvt.family.domain.filter.PermissionFilter;
import com.vvt.family.domain.filter.UserFilter;

import common.filter.CustomSpecification;
import common.type.EntityName;
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
		//filter.setEmail(new String[] {"thuong.test93@gmail.com", "thu", "t"});
		filter.setPermissions(new ArrayList<>());
		filter.getPermissions().add(new PermissionFilter(1, EntityName.USER));
		filter.getPermissions().add(new PermissionFilter(1, EntityName.PERMISSION));
		//filter.setIsStaff(true);
		CustomSpecification<User> specification = new CustomSpecification<User>(filter);
		specification.addSubFetch("permissionGroups", JoinType.LEFT).addSubFetch("permissionss", JoinType.LEFT);
		//specification.addSubFetch("permissions", JoinType.LEFT);
		repositoty.findAll(specification).forEach(item->{
			System.out.println(item);
		});
		
	}

}
