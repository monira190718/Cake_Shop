package com.sparktech.restaurant;

import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Wrong email or password"
                        )
                );

        if (!passwordEncoder.matches(
                password,
                customer.getPassword())) {

            throw new BadCredentialsException(
                    "Wrong email or password"
            );
        }

        return new UsernamePasswordAuthenticationToken(
                customer.getEmail(),
                customer.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + customer.getRole()
                        )
                )
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }

}
