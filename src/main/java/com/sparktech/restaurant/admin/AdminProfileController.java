package com.sparktech.restaurant.admin;

import com.sparktech.restaurant.profile.ProfilePictureService;
import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cakeshop/admin/profile")
public class AdminProfileController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfilePictureService profilePictureService;

    /*
     * Admin এবং Head Admin-এর profile page দেখাবে।
     */
    @GetMapping
    public String showAdminProfile(
            Authentication authentication,
            Model model) {

        Customer admin = getLoggedInAdmin(authentication);

        model.addAttribute("admin", admin);

        return "admin-profile";
    }

    /*
     * Admin-এর name, email ও address update করবে।
     */
    @PostMapping("/update")
    public String updateAdminProfile(
            Authentication authentication,

            @RequestParam String name,

            @RequestParam String email,

            @RequestParam(required = false)
            String address,

            HttpServletRequest request,

            RedirectAttributes redirectAttributes) {

        Customer admin = getLoggedInAdmin(authentication);

        String updatedName =
                name == null ? "" : name.trim();

        String updatedEmail =
                email == null ? "" : email.trim();

        String updatedAddress =
                address == null ? null : address.trim();

        if (updatedName.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "Full name is required."
            );

            return "redirect:/cakeshop/admin/profile";
        }

        if (updatedEmail.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "Email address is required."
            );

            return "redirect:/cakeshop/admin/profile";
        }

        /*
         * নতুন email অন্য কোনো account ব্যবহার করছে কি না।
         */
        Optional<Customer> existingAccount =
                customerRepository.findByEmail(updatedEmail);

        if (existingAccount.isPresent()
                && !existingAccount.get()
                .getId()
                .equals(admin.getId())) {

            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "This email address is already registered."
            );

            return "redirect:/cakeshop/admin/profile";
        }

        boolean emailChanged =
                !admin.getEmail()
                        .equals(updatedEmail);

        admin.setName(updatedName);
        admin.setEmail(updatedEmail);
        admin.setAddress(updatedAddress);

        customerRepository.save(admin);

        log.info(
                "Admin profile updated: {}",
                updatedEmail
        );

        /*
         * Email পরিবর্তন হলে authentication-এ পুরোনো email থেকে যায়।
         * তাই Admin-কে logout করে আবার login করানো হবে।
         */
        if (emailChanged) {

            HttpSession session =
                    request.getSession(false);

            if (session != null) {
                session.invalidate();
            }

            SecurityContextHolder.clearContext();

            return "redirect:/login?emailChanged=true";
        }

        redirectAttributes.addFlashAttribute(
                "profileSuccess",
                "Profile updated successfully."
        );

        return "redirect:/cakeshop/admin/profile";
    }

    /*
     * Admin profile picture upload করবে।
     */
    @PostMapping("/picture")
    public String uploadAdminProfilePicture(
            Authentication authentication,

            @RequestParam("profilePicture")
            MultipartFile profilePicture,

            RedirectAttributes redirectAttributes) {

        Customer admin = getLoggedInAdmin(authentication);

        try {

            String savedFileName =
                    profilePictureService
                            .saveProfilePicture(profilePicture);

            admin.setProfilePicture(savedFileName);

            customerRepository.save(admin);

            redirectAttributes.addFlashAttribute(
                    "pictureSuccess",
                    "Profile picture uploaded successfully."
            );

            log.info(
                    "Profile picture updated for admin: {}",
                    admin.getEmail()
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "pictureError",
                    exception.getMessage()
            );

        } catch (IOException exception) {

            log.error(
                    "Admin profile picture upload failed: {}",
                    admin.getEmail(),
                    exception
            );

            redirectAttributes.addFlashAttribute(
                    "pictureError",
                    "Profile picture could not be uploaded."
            );
        }

        return "redirect:/cakeshop/admin/profile";
    }

    /*
     * Admin password পরিবর্তন করবে।
     */
    @PostMapping("/change-password")
    public String changeAdminPassword(
            Authentication authentication,

            @RequestParam String currentPassword,

            @RequestParam String newPassword,

            @RequestParam String confirmPassword,

            RedirectAttributes redirectAttributes) {

        Customer admin = getLoggedInAdmin(authentication);

        if (!passwordEncoder.matches(
                currentPassword,
                admin.getPassword())) {

            redirectAttributes.addFlashAttribute(
                    "passwordError",
                    "Current password is incorrect."
            );

            return "redirect:/cakeshop/admin/profile";
        }

        if (newPassword == null
                || newPassword.length() < 6) {

            redirectAttributes.addFlashAttribute(
                    "passwordError",
                    "New password must be at least 6 characters."
            );

            return "redirect:/cakeshop/admin/profile";
        }

        if (!newPassword.equals(confirmPassword)) {

            redirectAttributes.addFlashAttribute(
                    "passwordError",
                    "New password and confirm password do not match."
            );

            return "redirect:/cakeshop/admin/profile";
        }

        /*
         * নতুন password পুরোনো password-এর সমান হতে পারবে না।
         */
        if (passwordEncoder.matches(
                newPassword,
                admin.getPassword())) {

            redirectAttributes.addFlashAttribute(
                    "passwordError",
                    "New password must be different from current password."
            );

            return "redirect:/cakeshop/admin/profile";
        }

        admin.setPassword(
                passwordEncoder.encode(newPassword)
        );

        customerRepository.save(admin);

        redirectAttributes.addFlashAttribute(
                "passwordSuccess",
                "Password changed successfully."
        );

        log.info(
                "Password changed for admin: {}",
                admin.getEmail()
        );

        return "redirect:/cakeshop/admin/profile";
    }

    /*
     * বর্তমানে login করা Admin/Head Admin account বের করবে।
     */
    private Customer getLoggedInAdmin(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Admin is not logged in."
            );
        }

        Customer admin = customerRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin account not found."
                        ));

        boolean validAdminRole =
                "ADMIN".equals(admin.getRole())
                        ||
                        "HEAD_ADMIN".equals(admin.getRole());

        if (!validAdminRole) {

            throw new RuntimeException(
                    "You are not authorized to access the Admin profile."
            );
        }

        return admin;
    }
}