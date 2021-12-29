package com.vvt.family.domain.repository.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vvt.family.domain.entity.PermissionGroup;
import com.vvt.family.domain.entity.User;
@Repository
public interface UserRepositoty extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User>{
	Optional<User> findByUsername(String username);
	@Query("SELECT p1 FROM User u JOIN u.permissionGroups p1 JOIN FETCH p1.permissions WHERE u.id =:userId")
	List<PermissionGroup> getPermissionGroupByUserId(Integer userId);
}
