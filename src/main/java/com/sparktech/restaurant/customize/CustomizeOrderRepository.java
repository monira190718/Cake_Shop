package com.sparktech.restaurant.customize;

import com.sparktech.restaurant.registration.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomizeOrderRepository  extends JpaRepository<CustomizeOrder, Long> {

    List<CustomizeOrder> findByCustomer(Customer customer);

    List<CustomizeOrder> findByStatusNot(String status);

    List<CustomizeOrder> findByStatusNotIn(
            List<String> statuses
    );

}
