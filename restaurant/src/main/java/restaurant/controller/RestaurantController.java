package restaurant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import restaurant.controller.model.RestaurantData;
import restaurant.controller.model.RestaurantData.restaurantCustomer;
import restaurant.controller.model.RestaurantData.restaurantEmployee;
import restaurant.service.RestaurantService;

@RestController
@RequestMapping("/restaurant")
@Slf4j
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    
    
    // Method for creating new restaurant
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public RestaurantData insertRestaurant(@RequestBody RestaurantData restaurantData) {
        log.info("Creating restaurant {}", restaurantData);
        return restaurantService.saveRestaurant(restaurantData);
    }
    
    
    // Method for updating restaurant information using the restaurantId to identify it
    @PutMapping("/{restaurantId}")
    public RestaurantData updateRestaurant(@PathVariable Long restaurantId, @RequestBody RestaurantData restaurantData) {
        restaurantData.setRestaurantId(restaurantId);
        log.info("Updating store with Id={}", restaurantId);
        return restaurantService.saveRestaurant(restaurantData);
    }
    
    // Method for adding new employee
    @PostMapping("/{restaurantId}/employee")
    @ResponseStatus(code = HttpStatus.CREATED)
    public restaurantEmployee insertEmployeeInRestaurant(@PathVariable Long restaurantId,
            @RequestBody restaurantEmployee employee) {
        log.info("Adding employee: '{}' to restaurant '{}'", employee, restaurantId);
        return restaurantService.saveEmployee(restaurantId, employee);
    }
    
    // Method for adding new customer
    @PostMapping("/{restaurantId}/customer")
    @ResponseStatus(code = HttpStatus.CREATED)
    public restaurantCustomer insertCustomerInRestaurant(@PathVariable Long restaurantId,
            @RequestBody restaurantCustomer customer) {
        log.info("Adding customer: '{}' to restaurant '{}'", customer, restaurantId);
        return restaurantService.saveCustomer(restaurantId, customer);
    }

    // Method for retrieving list of restaurant w/o customer or employee info
    @GetMapping
    public List<RestaurantData> getAllRestaurants() {
        log.info("Retrieving all restaurant information without customer and employee data.");
        return restaurantService.retrieveAllRestaurants();
    }
    
    // Method for pulling all info about one restaurant
    @GetMapping("/{restaurantId}")
    public RestaurantData getRestaurantById(@PathVariable Long restaurantId) {
        log.info("Retrieving information for restaurant with Id: '{}'", restaurantId);
        return restaurantService.retrieveRestaurantById(restaurantId);
    }
    
    // Method for deleting a restaurant, the employees associated with it, and also the restaurant/customer table data linking the two
    @DeleteMapping("/{restaurantId}")
    public Map<String, String> deleteRestaurantById(@PathVariable Long restaurantId) {
        log.info("Deleting restaurant with Id: '{}'", restaurantId);
        restaurantService.deleteRestaurantById(restaurantId);

        return Map.of("message", "Deletion of restaurant with Id: '" + restaurantId + "' was successful.");
    }
}
