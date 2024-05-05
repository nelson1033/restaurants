package restaurant.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurant.controller.model.RestaurantData;
import restaurant.controller.model.RestaurantData.restaurantCustomer;
import restaurant.controller.model.RestaurantData.restaurantEmployee;
import restaurant.dao.CustomerDao;
import restaurant.dao.EmployeeDao;
import restaurant.dao.RestaurantDao;
import restaurant.entity.Customer;
import restaurant.entity.Employee;
import restaurant.entity.Restaurant;

@Service
public class RestaurantService {

	@Autowired
	private RestaurantDao RestaurantDao;
	
	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CustomerDao customerDao;

	
	// This method saves or updates a pet store based on RestaurantData
	@Transactional(readOnly = false)
	public RestaurantData saveRestaurant(RestaurantData RestaurantData) {
		Long RestaurantId = RestaurantData.getRestaurantId();
		Restaurant Restaurant = findOrCreateRestaurant(RestaurantId);

		copyRestaurantFields(Restaurant, RestaurantData);
		return new RestaurantData(RestaurantDao.save(Restaurant));
	}

	// moves info from pet store object to the pet store entity
	private void copyRestaurantFields(Restaurant Restaurant, RestaurantData RestaurantData) {
		Restaurant.setRestaurantName(RestaurantData.getRestaurantName());
		Restaurant.setRestaurantAddress(RestaurantData.getRestaurantAddress());
		Restaurant.setRestaurantCity(RestaurantData.getRestaurantCity());
		Restaurant.setRestaurantState(RestaurantData.getRestaurantState());
		Restaurant.setRestaurantZip(RestaurantData.getRestaurantZip());
		Restaurant.setRestaurantPhone(RestaurantData.getRestaurantPhone());
	}

	// finds a pet store by ID or makes a new one
	private Restaurant findOrCreateRestaurant(Long RestaurantId) {
		Restaurant Restaurant;

		if (Objects.isNull(RestaurantId)) {
			Restaurant = new Restaurant();
		} else {
			Restaurant = findRestaurantById(RestaurantId);
		}

		return Restaurant;
	}

	// retrieves a pet store by ID
	private Restaurant findRestaurantById(Long RestaurantId) {
		return RestaurantDao.findById(RestaurantId).orElseThrow(
				() -> new NoSuchElementException("Pet store with Id: '" + RestaurantId + "' does not exist."));
	}
	
	// Saves new employee associated with pet_store
	@Transactional(readOnly = false)
	public restaurantEmployee saveEmployee(Long RestaurantId, restaurantEmployee RestaurantEmployee) {
		Restaurant Restaurant = findRestaurantById(RestaurantId);
		Long employeeId = RestaurantEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(RestaurantId, employeeId);

		copyEmployeeFields(employee, RestaurantEmployee);
		employee.setRestaurant(Restaurant);
		Restaurant.getEmployees().add(employee);

		return new restaurantEmployee(employeeDao.save(employee));
	}

	// copies data for employee object
	private void copyEmployeeFields(Employee employee, restaurantEmployee RestaurantEmployee) {
		employee.setEmployeeFirstName(RestaurantEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(RestaurantEmployee.getEmployeeLastName());
		employee.setEmployeePhone(RestaurantEmployee.getEmployeePhone());
		employee.setEmployeeJobTitle(RestaurantEmployee.getEmployeeJobTitle());
	}

	// finds or creates new employee
	private Employee findOrCreateEmployee(Long RestaurantId, Long employeeId) {
		Employee employee;

		if (Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(RestaurantId, employeeId);
		}

		return employee;
	}

	// retrieves employee by ID and checks to make sure info matches with pet store ID
	private Employee findEmployeeById(Long RestaurantId, Long employeeId) {
		Employee employee = employeeDao.findById(employeeId).orElseThrow(
				() -> new NoSuchElementException("Employee with Id: '" + employeeId + "' does not exist."));

		if (employee.getRestaurant().getRestaurantId() == RestaurantId) {
			return employee;
		} else {
			throw new IllegalArgumentException(
					"Employee with Id: '" + employeeId + "' already exists at store with Id: '" + RestaurantId + "'.");
		}
	}
	
	// Saves new customer associated with specific pet store
	@Transactional(readOnly = false)
	public restaurantCustomer saveCustomer(Long RestaurantId, restaurantCustomer RestaurantCustomer) {
		Restaurant Restaurant = findRestaurantById(RestaurantId);
		Long customerId = RestaurantCustomer.getCustomerId();
		Customer customer = findOrCreateCustomer(RestaurantId, customerId);

		copyCustomerFields(customer, RestaurantCustomer);
		customer.getRestaurant().add(Restaurant);
		Restaurant.getCustomers().add(customer);

		return new restaurantCustomer(customerDao.save(customer));
	}

	// copies fields to Customer object
	private void copyCustomerFields(Customer customer, restaurantCustomer RestaurantCustomer) {
		customer.setCustomerFirstName(RestaurantCustomer.getCustomerFirstName());
		customer.setCustomerLastName(RestaurantCustomer.getCustomerLastName());
		customer.setCustomerEmail(RestaurantCustomer.getCustomerEmail());
		customer.setCustomerId(RestaurantCustomer.getCustomerId());
	}

	// finds or creates a new customer
	private Customer findOrCreateCustomer(Long RestaurantId, Long customerId) {

		if (Objects.isNull(customerId)) {
			return new Customer();
		} 
			return findCustomerById(RestaurantId, customerId);

	}

	// retrieves customer ID and info from database, or throw exception if there isn't one
	private Customer findCustomerById(Long RestaurantId, Long customerId) {
		Customer customer = customerDao.findById(customerId).orElseThrow(
				() -> new NoSuchElementException("Customer with Id: '" + customerId + "' does not exist."));
		boolean foundStore = false;

		for (Restaurant Restaurant : customer.getRestaurant()) {
			if (Restaurant.getRestaurantId() == RestaurantId) {
				foundStore = true;
				break;
			}
		}

		if (!foundStore) {
			throw new IllegalArgumentException(
					"Pet store with Id: '" + RestaurantId + "' doesnt have a customer with Id: '" + customerId + "'.");
		
		} 
		return customer;	
	}

	// generates list of read only Restaurants w/o customer and employee data
	@Transactional(readOnly = true)
	public List<RestaurantData> retrieveAllRestaurants() {
		List<Restaurant> Restaurants = RestaurantDao.findAll();
		List<RestaurantData> result = new LinkedList<>();

		for (Restaurant Restaurant : Restaurants) {
			RestaurantData RestaurantData = new RestaurantData(Restaurant);

			RestaurantData.getCustomers().clear();
			RestaurantData.getEmployees().clear();

			result.add(RestaurantData);
		}

		return result;
	}

	// retrieves pet store by id and returns all information about it
	@Transactional(readOnly = true)
	public RestaurantData retrieveRestaurantById(Long RestaurantId) {
		return new RestaurantData(findRestaurantById(RestaurantId));
	}

	// deletes pet store 
	public void deleteRestaurantById(Long RestaurantId) {
		Restaurant Restaurant = findRestaurantById(RestaurantId);
		RestaurantDao.delete(Restaurant);
	}
}