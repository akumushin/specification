package com.vvt.family.domain.repository.permissionGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vvt.family.domain.entity.PermissionGroup;
@Repository
public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Integer> {

}
