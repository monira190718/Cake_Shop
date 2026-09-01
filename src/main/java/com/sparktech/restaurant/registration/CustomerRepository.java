package com.sparktech.restaurant.registration;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

            //valodate kori
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByRole(String role);

    List<Customer> findAllByRole(String role);

    List<Customer> findByRole(String role);
}
