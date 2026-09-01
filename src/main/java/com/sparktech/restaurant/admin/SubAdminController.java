package com.sparktech.restaurant.admin;

import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cakeshop/head-admin")
@RequiredArgsConstructor
public class SubAdminController {

    private final CustomerRepository customerRepository;

    /*
     * শুধু normal ADMIN account-গুলো দেখাবে।
     */
    @GetMapping("/admins")
    public String showAdmins(
            Authentication authentication,
            Model model) {

        Customer headAdmin = getLoggedInHeadAdmin(authentication);

        model.addAttribute("headAdmin", headAdmin);
        model.addAttribute(
                "admins",
                customerRepository.findAllByRole("ADMIN")
        );

        return "manage-admins";
    }

    /*
     * Normal ADMIN-কে HEAD_ADMIN বানাবে।
     */
    @Transactional
    @PostMapping("/admins/{id}/promote")
    public String promoteToHeadAdmin(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Customer account = customerRepository.findById(id)
                .orElse(null);

        if (account == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Admin account not found."
            );
            return "redirect:/cakeshop/head-admin/admins";
        }

        if (!"ADMIN".equals(account.getRole())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Only a normal Admin can be promoted."
            );
            return "redirect:/cakeshop/head-admin/admins";
        }

        account.setRole("HEAD_ADMIN");
        customerRepository.save(account);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                account.getName()
                        + " has been promoted to Head Administrator."
        );

        return "redirect:/cakeshop/head-admin/admins";
    }

    /*
     * Normal ADMIN account database থেকে সম্পূর্ণ delete করবে।
     */
    @Transactional
    @PostMapping("/admins/{id}/delete")
    public String deleteAdmin(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Customer account = customerRepository.findById(id)
                .orElse(null);

        if (account == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Admin account not found."
            );
            return "redirect:/cakeshop/head-admin/admins";
        }

        if (!"ADMIN".equals(account.getRole())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Only a normal Admin can be deleted."
            );
            return "redirect:/cakeshop/head-admin/admins";
        }

        String deletedName = account.getName();
        customerRepository.delete(account);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                deletedName + " has been deleted permanently."
        );

        return "redirect:/cakeshop/head-admin/admins";
    }

    private Customer getLoggedInHeadAdmin(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new RuntimeException("Head Admin is not logged in.");
        }

        Customer headAdmin = customerRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Head Admin not found."));

        if (!"HEAD_ADMIN".equals(headAdmin.getRole())) {
            throw new RuntimeException(
                    "You are not authorized to manage Admin accounts."
            );
        }

        return headAdmin;
    }
}