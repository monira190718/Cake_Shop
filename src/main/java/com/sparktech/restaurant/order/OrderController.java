package com.sparktech.restaurant.order;

import com.sparktech.restaurant.cake.Cake;
import com.sparktech.restaurant.cake.CakeRepository;
import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cakeshop")
public class OrderController {
    private final OrderService orderService;

    private final CakeRepository cakeRepository;

    private final CustomerRepository customerRepository;



    // Open Order Page

    @GetMapping("/order/{cakeId}")
    public String orderPage(
            @PathVariable Long cakeId,
            @RequestParam(required = false) String success,
            Model model
    ) {
        Cake cake = cakeRepository.findById(cakeId)
                .orElseThrow(() ->
                        new RuntimeException("Cake not found")
                );

        model.addAttribute("cake", cake);
        if(success != null){

            model.addAttribute(
                    "successMessage",
                    "Your order has been placed. Please wait for approval."
            );

        }

        return "order-form";
    }

//new



    // Save Order

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam Long cakeId,
            @RequestParam int quantity,
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam String deliveryDate,
            Principal principal
    ) {


        String email = principal.getName();


        Customer customer =
                customerRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found")
                        );



        Cake cake =
                cakeRepository.findById(cakeId)
                        .orElseThrow(() ->
                                new RuntimeException("Cake not found")
                        );



        Orders order = new Orders();


        order.setCustomer(customer);

        order.setCake(cake);

        order.setQuantity(quantity);

        order.setTotalPrice(
                cake.getPrice() * quantity
        );

        order.setAddress(address);

        order.setPhone(phone);


        order.setDeliveryDate(
                LocalDate.parse(deliveryDate)
        );


        log.info("New order placed by {}", customer.getEmail());


        orderService.saveOrder(order);



        return "redirect:/cakeshop/order/" + cakeId + "?success=true";

    }





    // Show Customer Orders

    @GetMapping("/my-orders")
    public String myOrders(
            Principal principal,
            Model model
    ) {


        String email = principal.getName();



        Customer customer =
                customerRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found")
                        );



        model.addAttribute(
                "orders",
                orderService.getCustomerOrders(customer)
        );


        return "my-orders";

    }

    @GetMapping("/payment-success/{id}")
    public String paymentSuccess(
            @PathVariable Long id,
            @RequestParam String method
    ){

        Orders order =
                orderService.getOrderById(id);


        order.setPaymentStatus("PAID");

        order.setPaymentMethod(method);

        order.setCustomerMessage(
                "Thank you for your order!"
        );


        orderService.saveOrder(order);


        return "redirect:/cakeshop/dashboard";

    }

    @PostMapping("/order/update-status/{id}")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ){


        Orders order =
                orderService.getOrderById(id);

        order.setDeliveryStatus(status);
        
        orderService.saveOrder(order);

        return "redirect:/cakeshop/admin?section=status";

    }

   /* @GetMapping("/order/cancel/{id}")
    public String cancelOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ){

        Orders order =
                orderService.getOrderById(id);


        // শুধু pending order cancel হবে

        if("PENDING".equals(order.getStatus())){


            orderService.deleteOrder(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your order has been cancelled."
            );


        }


        return "redirect:/cakeshop/dashboard";

    }  */

    @GetMapping("/order/cancel/{id}")
    public String cancelOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ){

        Orders order =
                orderService.getOrderById(id);


        // Payment complete না হলে cancel allowed

        if(!"PAID".equals(order.getPaymentStatus())){


            orderService.deleteOrder(id);


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your order has been cancelled."
            );


        }


        return "redirect:/cakeshop/dashboard";

    }

}
