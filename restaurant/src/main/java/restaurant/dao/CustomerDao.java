package restaurant.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurant.entity.Customer;

public interface CustomerDao extends JpaRepository<Customer, Long> {

}