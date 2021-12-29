package com.vvt.family.domain.repository.userToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vvt.family.domain.entity.UserToken;
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {

}
