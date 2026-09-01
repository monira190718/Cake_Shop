package com.sparktech.restaurant.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final Set<String> ALLOWED_ROLES =
            Set.of(
                    "CUSTOMER",
                    "ADMIN",
                    "HEAD_ADMIN",
                    "DELIVERYMAN"
            );

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public Customer register(Customer customer) {

        String role = customer.getRole();

        if (role == null || !ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException(
                    "Invalid account role selected."
            );
        }

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException(
                    "This email is already registered."
            );
        }

        /*
         * শুধু একজন Head Admin registration করতে পারবে।
         */
        if ("HEAD_ADMIN".equals(role)
                && customerRepository.existsByRole("HEAD_ADMIN")) {

            throw new IllegalArgumentException(
                    "A Head Admin account already exists."
            );
        }

        customer.setRole(role);

        customer.setPassword(
                passwordEncoder.encode(customer.getPassword())
        );

        return customerRepository.save(customer);
    }

   /* private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public Customer register(Customer customer) {

        customer.setPassword(
                passwordEncoder.encode(customer.getPassword())
        );

        return customerRepository.save(customer);
    }*/

   /* private final CustomerRepository customerRepository;
    public Customer save(Customer customer)
    { return customerRepository.save(customer); }  */

}
