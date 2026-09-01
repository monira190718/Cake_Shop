package com.sparktech.restaurant.profile;

import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cakeshop")
public class ProfileController {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final ProfilePictureService profilePictureService;

    /*
     * Customer Profile page দেখাবে।
     */
    @GetMapping("/profile")
    public String showProfile(
            Principal principal,
            Model model
    ) {

        Customer customer =
                getLoggedInCustomer(principal);

        model.addAttribute(
                "customer",
                customer
        );

        return "profile";
    }

    /*
     * Name, phone number এবং address update করবে।
     */
    @PostMapping("/profile/update")
    public String updateProfile(
            Principal principal,

            @RequestParam String name,

            @RequestParam String phonenumber,

            @RequestParam String address,

            RedirectAttributes redirectAttributes
    ) {

        Customer customer =
                getLoggedInCustomer(principal);

        if (name == null || name.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "Full name is required."
            );

            return "redirect:/cakeshop/profile";
        }

        if (phonenumber == null
                ||
                phonenumber.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "Phone number is required."
            );

            return "redirect:/cakeshop/profile";
        }

        customer.setName(
                name.trim()
        );

        customer.setPhonenumber(
                phonenumber.trim()
        );

        customer.setAddress(
                address == null
                        ? null
                        : address.trim()
        );

        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute(
                "profileSuccess",
                "Profile updated successfully."
        );

        log.info(
                "Profile updated for customer: {}",
                customer.getEmail()
        );

        return "redirect:/cakeshop/profile";
    }

    /*
     * Customer profile picture upload করবে।
     */
    @PostMapping("/profile/picture")
    public String uploadProfilePicture(
            Principal principal,

            @RequestParam("profilePicture")
            MultipartFile profilePicture,

            RedirectAttributes redirectAttributes
    ) {

        Customer customer =
                getLoggedInCustomer(principal);

        try {

            String savedFileName =
                    profilePictureService
                            .saveProfilePicture(
                                    profilePicture
                            );

            customer.setProfilePicture(
                    savedFileName
            );

            customerRepository.save(customer);

            redirectAttributes.addFlashAttribute(
                    "pictureSuccess",
                    "Profile picture uploaded successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "pictureError",
                    exception.getMessage()
            );

        } catch (IOException exception) {

            log.error(
                    "Profile picture upload failed for: {}",
                    customer.getEmail(),
                    exception
            );

            redirectAttributes.addFlashAttribute(
                    "pictureError",
                    "Profile picture could not be uploaded."
            );
        }

        return "redirect:/cakeshop/profile";
    }

    /*
     * Customer password পরিবর্তন করবে।
     */
    @PostMapping("/profile/change-password")
    public String changePassword(
            Principal principal,

            @RequestParam String currentPassword,

            @RequestParam String newPassword,

            @RequestParam String confirmPassword,

            RedirectAttributes redirectAttributes
    ) {

        Customer customer =
                getLoggedInCustomer(principal);

        boolean passwordMatches =
                passwordEncoder.matches(
                        currentPassword,
                        customer.getPassword()
                );

        if (!passwordMatches) {

            redirectAttributes.addFlashAttribute(
                    "passwordError",
                    "Current password is incorrect."
            );

            return "redirect:/cakeshop/profile";
        }

        if (newPassword == null
                ||
                newPassword.length() < 6) {

            redirectAttributes.addFlashAttribute(
                    "passwordError",
                    "New password must be at least 6 characters."
            );

            return "redirect:/cakeshop/profile";
        }

        if (!newPassword.equals(confirmPassword)) {

            redirectAttributes.addFlashAttribute(
                    "passwordError",
                    "New password and confirm password do not match."
            );

            return "redirect:/cakeshop/profile";
        }

        customer.setPassword(
                passwordEncoder.encode(newPassword)
        );

        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute(
                "passwordSuccess",
                "Password changed successfully."
        );

        log.info(
                "Password changed for customer: {}",
                customer.getEmail()
        );

        return "redirect:/cakeshop/profile";
    }

    /*
     * বর্তমানে login করা customer বের করবে।
     */
    private Customer getLoggedInCustomer(
            Principal principal
    ) {

        if (principal == null) {

            throw new RuntimeException(
                    "Customer is not logged in."
            );
        }

        return customerRepository
                .findByEmail(
                        principal.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found."
                        )
                );
    }
}