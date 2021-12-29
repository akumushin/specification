package com.vvt.family.domain.repository.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vvt.family.domain.entity.Permission;
@Repository
public interface PermissionRepository  extends JpaRepository<Permission, Integer>{
}
