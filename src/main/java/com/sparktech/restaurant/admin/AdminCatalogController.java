package com.sparktech.restaurant.admin;

import com.sparktech.restaurant.cake.Cake;
import com.sparktech.restaurant.cake.CakeImageService;
import com.sparktech.restaurant.cake.CakeRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/cakeshop/admin")
@RequiredArgsConstructor
public class AdminCatalogController {

    private final CakeRepository cakeRepository;

    private final CakeImageService cakeImageService;

    /*
     * ==========================
     * ADD CAKE FORM
     * ==========================
     */
   /* @GetMapping("/cakes/add")
    public String showAddCakeForm(Model model) {

        model.addAttribute("cake", new Cake());



        return "cake-form";
    }  */

    @GetMapping("/cakes/add")
    public String showAddCakeForm(
            Model model,
            HttpServletResponse response
    ){

        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );


        model.addAttribute(
                "cake",
                new Cake()
        );


        return "cake-form";

    }
    /*
     * ==========================
     * EDIT CAKE FORM
     * ==========================
     */
    @GetMapping("/cakes/edit/{id}")
    public String showEditCakeForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Cake cake = cakeRepository
                .findById(id)
                .orElse(null);

        if (cake == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cake not found."
            );

            return redirectToManageCakes();
        }

        model.addAttribute("cake", cake);




        return "cake-form";
    }

    /*
     * ==========================
     * ADD AND EDIT SAVE
     * ==========================
     */
    @PostMapping("/cakes/save")
    public String saveCake(
            @RequestParam(required = false)
            Long id,

            @RequestParam
            String name,



            @RequestParam
            double price,

            @RequestParam
            boolean available,

            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile,

            RedirectAttributes redirectAttributes
    ) {
        try {
            validateCakeInformation(
                    name,
                    price
            );

            if (id == null &&
                    (imageFile == null || imageFile.isEmpty())) {

                throw new IllegalArgumentException(
                        "Please upload picture"
                );
            }

            Cake cake;

            if (id == null) {
                /*
                 * নতুন Cake add
                 */
                cake = new Cake();

                if (imageFile == null ||
                        imageFile.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Cake picture is required."
                    );
                }

            } else {
                /*
                 * Existing Cake edit
                 */
                cake = cakeRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cake was not found."
                                )
                        );
            }

            cake.setName(name.trim());

            cake.setPrice(price);
            cake.setAvailable(available);

            /*
             * Add-এর সময় picture save হবে।
             * Edit-এর সময় নতুন picture নির্বাচন করলে
             * picture পরিবর্তিত হবে।
             *
             * Edit-এর সময় picture না দিলে
             * আগের picture অপরিবর্তিত থাকবে।
             */
            if (imageFile != null &&
                    !imageFile.isEmpty()) {

                String newImagePath =
                        cakeImageService
                                .saveImage(imageFile);

                cake.setImage(newImagePath);
            }

            cakeRepository.save(cake);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    id == null
                            ? "Cake added successfully."
                            : "Cake updated successfully."
            );

            return redirectToManageCakes();

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return redirectToCakeForm(id);

        } catch (IOException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cake picture could not be uploaded."
            );

            return redirectToCakeForm(id);
        }
    }

    private void validateCakeInformation(
            String name,
            double price
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Cake name is required."
            );
        }

        if (name.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Cake name is too long."
            );
        }

        if (price <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero."
            );
        }
    }

    private String redirectToCakeForm(Long id) {
        if (id == null) {
            return "redirect:/cakeshop/admin/cakes/add";
        }

        return "redirect:/cakeshop/admin/cakes/edit/" + id;
    }

    private String redirectToManageCakes() {
        return "redirect:/cakeshop/admin?section=cakes";
    }
}