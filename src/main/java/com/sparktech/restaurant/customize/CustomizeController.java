package com.sparktech.restaurant.customize;

import com.sparktech.restaurant.order.OrderRepository;
import com.sparktech.restaurant.order.Orders;
import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import jakarta.servlet.http.HttpServletResponse;
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
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cakeshop")
public class CustomizeController {


    private final CustomerRepository customerRepository;

    private final CustomizeOrderRepository customizeOrderRepository;

    private final OrderRepository orderRepository;

    // Open customize form

    @GetMapping("/customize")
    public String customizeForm(
            @RequestParam(required = false) String success,
            Model model,
            HttpServletResponse response
    ){
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );

        if(success != null){

            model.addAttribute(
                    "successMessage",
                    "Your custom cake request has been submitted. Please wait for admin reply."
            );

        }


        return "customize";

    }




    // Save customize request

    @PostMapping("/customize/save")
    public String saveCustomize(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("description") String description,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("deliveryDate") LocalDate deliveryDate,
            Principal principal
    ) throws IOException {


        Customer customer =
                customerRepository.findByEmail(
                                principal.getName()
                        )
                        .orElseThrow();


        CustomizeOrder order = new CustomizeOrder();


        order.setCustomer(customer);

        order.setDescription(description);

        order.setPhone(phone);

        order.setAddress(address);

        order.setDeliveryDate(deliveryDate);


        order.setStatus("PENDING");



        // image upload

        if(!imageFile.isEmpty()){


            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + imageFile.getOriginalFilename();



            Path uploadPath =
                    Paths.get("uploads/customize");


            Files.createDirectories(uploadPath);



            Files.copy(
                    imageFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );



            order.setImage(
                    "/customize-images/" + fileName
            );

        }

        customizeOrderRepository.save(order);

        return "redirect:/cakeshop/customize?success=true";

    }

    @GetMapping("/customize/accept/{id}")
    public String acceptCustom(
            @PathVariable Long id
    ){

        CustomizeOrder custom =
                customizeOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Custom order not found")
                        );


        Orders order = new Orders();


        order.setCustomer(custom.getCustomer());

        order.setPhone(
                custom.getPhone()
        );


        order.setAddress(
                custom.getAddress()
        );


        order.setDeliveryDate(
                custom.getDeliveryDate()
        );

        order.setQuantity(1);

        order.setTotalPrice(
                custom.getQuotedPrice()
        );

        //customimage
        order.setCustomImage(
                custom.getImage()
        );

        order.setStatus("PENDING");

        order.setDeliveryStatus("PREPARING");

        order.setPaymentStatus("UNPAID");

        order.setCustomerMessage(
                "Your custom cake order has been accepted."
        );


        orderRepository.save(order);



        custom.setStatus("ACCEPTED");

        customizeOrderRepository.save(custom);


        return "redirect:/cakeshop/dashboard";

    }

    //customize cancel

    @GetMapping("/customize/cancel/{id}")
    public String cancelCustomOrder(
            @PathVariable Long id
    ){

        CustomizeOrder order =
                customizeOrderRepository.findById(id)
                        .orElseThrow();


        order.setStatus("CANCELLED");


        order.setAdminMessage(
                "Your custom cake request has been cancelled."
        );


        customizeOrderRepository.save(order);


        return "redirect:/cakeshop/dashboard";

    }
}
