package com.sparktech.restaurant.registration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Slf4j
@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final CustomerService customerService;

    @GetMapping("/registration")
    public String registration(Model model) {

        model.addAttribute("user", new Customer());

        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(
            @Valid @ModelAttribute("user") Customer user,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        try {

            customerService.register(user);

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "registrationError",
                    exception.getMessage()
            );

            return "registration";
        }

        log.info(
                "New account registered with email: {} and role: {}",
                user.getEmail(),
                user.getRole()
        );

        return "redirect:/login?registered=true";
    }
  /*  private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String registration(Model model) {

        model.addAttribute("user", new Customer());

        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(
            @Valid @ModelAttribute("user") Customer user,
            BindingResult bindingResult) {

        log.info("{} has been submitted", user);

        // Check validation errors
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        // Save user
        // Password will be encoded inside CustomerService
        customerService.register(user);

        log.info("{} has been saved", user);

        // After successful registration, go to login
        return "redirect:/login";
    } */








}
