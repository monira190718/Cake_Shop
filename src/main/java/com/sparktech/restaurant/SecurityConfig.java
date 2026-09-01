package com.sparktech.restaurant;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final CustomAuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authenticationProvider(
                        authenticationProvider
                )

                .authorizeHttpRequests(auth -> auth



                        // Public resources

                        .requestMatchers(
                                "/login",
                                "/registration",
                                "/login.css",
                                "/registration.css",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/cake-images/**",
                                "/customize-images/**",
                                "/favicon.ico",
                                "/error"
                        )
                        .permitAll()



                        // HEAD ADMIN

                        .requestMatchers(
                                "/cakeshop/head-admin",
                                "/cakeshop/head-admin/**"
                        )
                        .hasRole("HEAD_ADMIN")



                        // ADMIN + HEAD ADMIN

                        .requestMatchers(
                                "/cakeshop/admin",
                                "/cakeshop/admin/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "HEAD_ADMIN"
                        )



                        // CUSTOMER

                        .requestMatchers(
                                "/cakeshop/dashboard",
                                "/cakeshop/dashboard/**",
                                "/cakeshop/profile",
                                "/cakeshop/profile/**"
                        )
                        .hasRole("CUSTOMER")



                        // DELIVERY MAN

                        .requestMatchers(
                                "/cakeshop/deliveryman",
                                "/cakeshop/deliveryman/**"
                        )
                        .hasRole("DELIVERYMAN")



                        // Authenticated images

                        .requestMatchers(
                                "/profile-images/**"
                        )
                        .authenticated()



                        // Others

                        .anyRequest()
                        .authenticated()


                )





                .formLogin(form -> form


                        .loginPage("/login")


                        .loginProcessingUrl("/login")


                        .usernameParameter("username")


                        .passwordParameter("password")



                        .successHandler(
                                (request, response, authentication) -> {



                                    boolean isAdmin =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(authority ->
                                                            authority
                                                                    .getAuthority()
                                                                    .equals(
                                                                            "ROLE_ADMIN"
                                                                    )
                                                    );



                                    boolean isHeadAdmin =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(authority ->
                                                            authority
                                                                    .getAuthority()
                                                                    .equals(
                                                                            "ROLE_HEAD_ADMIN"
                                                                    )
                                                    );



                                    boolean isDeliveryman =
                                            authentication
                                                    .getAuthorities()
                                                    .stream()
                                                    .anyMatch(authority ->
                                                            authority
                                                                    .getAuthority()
                                                                    .equals(
                                                                            "ROLE_DELIVERYMAN"
                                                                    )
                                                    );




                                    if(isAdmin || isHeadAdmin){


                                        response.sendRedirect(
                                                "/cakeshop/admin"
                                        );


                                    }

                                    else if(isDeliveryman){


                                        response.sendRedirect(
                                                "/cakeshop/deliveryman"
                                        );


                                    }

                                    else{


                                        response.sendRedirect(
                                                "/cakeshop/dashboard"
                                        );

                                    }


                                }
                        )

                        .failureUrl(
                                "/login?error=true"
                        )


                        .permitAll()


                )





                .logout(logout -> logout


                        .logoutUrl("/logout")


                        .invalidateHttpSession(true)


                        .clearAuthentication(true)


                        .deleteCookies(
                                "JSESSIONID"
                        )


                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )


                        .permitAll()


                );



        return http.build();

    }


}