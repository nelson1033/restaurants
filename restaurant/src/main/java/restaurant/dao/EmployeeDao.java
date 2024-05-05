package restaurant.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurant.entity.Employee;

public interface EmployeeDao extends JpaRepository<Employee, Long> {

}