package com.vvt.family.web.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vvt.family.domain.entity.Customer;
import com.vvt.family.domain.repository.customer.CustomerRepository;
import com.vvt.family.domain.repository.user.UserRepositoty;

@Service
public class CustomerService {
	@Autowired CustomerRepository customerRepository;
	@Autowired UserRepositoty userRepositoty;
	
	public void save(Customer customer) {
		userRepositoty.save(customer.getUser());
		customerRepository.save(customer);
	}
	
	public Customer getCustomer(Integer id) {
		return customerRepository.findById(id).orElse(null);
	}
}
