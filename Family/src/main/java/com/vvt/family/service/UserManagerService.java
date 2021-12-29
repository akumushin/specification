package com.vvt.family.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.vvt.family.domain.entity.User;
import com.vvt.family.domain.filter.UserFilter;
import com.vvt.family.domain.repository.user.UserRepositoty;

import common.IService;
import common.PageInfo;

@Service
public class UserManagerService implements IService<User, Integer, UserFilter>{
	@Autowired UserRepositoty userRepositoty;
	
	@Override
	public void save(User user) {
		userRepositoty.save(user);
	}
	@Override
	public void delete(Integer id) {
		userRepositoty.deleteById(id);
		
	}
	@Override
	public User getOne(Integer id) {
		return userRepositoty.findById(id).orElse(null);
	}
	@Override
	public List<User> getAll() {
		return userRepositoty.findAll();
	}
	@Override
	public Page<User> getOnePage(UserFilter filter, PageInfo pageInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean existById(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
