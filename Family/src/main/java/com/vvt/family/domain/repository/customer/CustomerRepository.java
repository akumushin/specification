package com.vvt.family.domain.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vvt.family.domain.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
