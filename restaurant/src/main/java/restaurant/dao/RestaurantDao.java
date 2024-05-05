package restaurant.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entity.Restaurant;

public interface RestaurantDao extends JpaRepository<Restaurant, Long> {

}