package com.carpooling.service.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Employee, Long> {

        Employee findByUsername(final String username);
}

