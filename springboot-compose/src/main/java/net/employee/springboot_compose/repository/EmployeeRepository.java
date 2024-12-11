package net.employee.springboot_compose.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.employee.springboot_compose.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

