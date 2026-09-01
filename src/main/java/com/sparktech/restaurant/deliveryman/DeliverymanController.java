package com.sparktech.restaurant.deliveryman;


import com.sparktech.restaurant.order.OrderRepository;
import com.sparktech.restaurant.order.Orders;
import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
@RequestMapping("/cakeshop")
public class DeliverymanController {


    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;



    @GetMapping("/deliveryman")
    public String deliveryDashboard(
            Principal principal,
            Model model
    ){


        Customer deliveryman =
                customerRepository.findByEmail(
                                principal.getName()
                        )
                        .orElseThrow();



        List<Orders> orders =
                orderRepository.findByDeliveryMan(
                        deliveryman
                );

        model.addAttribute(
                "deliveryman",
                deliveryman
        );


        model.addAttribute(
                "assignedOrders",
                orders
        );

        return "deliveryman";

    }

    @PostMapping("/deliveryman/confirm/{id}")
    public String confirmDelivery(
            @PathVariable Long id
    ){


        Orders order =
                orderRepository.findById(id)
                        .orElseThrow();



        order.setDeliveryAssignStatus(
                "CONFIRMED"
        );



        orderRepository.save(order);



        return "redirect:/cakeshop/deliveryman";

    }

    @PostMapping("/deliveryman/profile/upload")
    public String uploadProfilePicture(
            @RequestParam("imageFile") MultipartFile imageFile,
            Principal principal
    ) throws IOException {


        Customer deliveryman =
                customerRepository.findByEmail(
                                principal.getName()
                        )
                        .orElseThrow();



        if(!imageFile.isEmpty()){


            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + imageFile.getOriginalFilename();



            Path uploadPath =
                    Paths.get(
                            "uploads",
                            "profile-pictures"
                    );


            Files.createDirectories(
                    uploadPath
            );



            Files.copy(
                    imageFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );



            deliveryman.setProfilePicture(
                    "/profile-images/" + fileName
            );



            customerRepository.save(
                    deliveryman
            );

        }


        return "redirect:/cakeshop/deliveryman";

    }

   
}