package com.backend.schedule.repository;

import com.backend.schedule.model.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee,Long> {
    List<Employee> findByEmail(String email);
}
