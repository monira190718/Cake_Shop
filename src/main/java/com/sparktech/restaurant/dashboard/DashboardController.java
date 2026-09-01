package com.sparktech.restaurant.dashboard;

import com.sparktech.restaurant.cake.Cake;
import com.sparktech.restaurant.cake.CakeRepository;
import com.sparktech.restaurant.category.Category;
import com.sparktech.restaurant.category.CategoryRepository;
import com.sparktech.restaurant.customize.CustomizeOrder;
import com.sparktech.restaurant.customize.CustomizeOrderRepository;
import com.sparktech.restaurant.order.OrderService;
import com.sparktech.restaurant.order.Orders;
import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cakeshop")
public class DashboardController {


    private final CustomerRepository customerRepository;

    private final CategoryRepository CategoryRepository;

    private final CakeRepository cakeRepository;

    private final OrderService orderService;

    private final CustomizeOrderRepository customizeOrderRepository;



    @GetMapping("/dashboard")
    public String dashboard(
            Principal principal,
            Model model
    ) {



        // Logged in customer email

        String email = principal.getName();


        // Find customer

        Customer customer =
                customerRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found")
                        );


        // Customer name for dashboard

        model.addAttribute(
                "customerName",
                customer.getName()
        );


        // Load all categories

        List<Category> categories =
                CategoryRepository.findAll();



        model.addAttribute(
                "categories",
                categories
        );

        
        // Load available cakes

        List<Cake> cakes =
                cakeRepository.findByAvailableTrueOrderByIdDesc();



        model.addAttribute(
                "cakes",
                cakes
        );


        // Customer orders

        List<Orders> orders =
                orderService.getCustomerOrders(customer);



        model.addAttribute(
                "orders",
                orders
        );

        //customize
        List<CustomizeOrder> customOrders =
                customizeOrderRepository.findByCustomer(customer);


        model.addAttribute(
                "customOrders",
                customOrders
        );
        // Total Orders

        long totalOrders =
                orders.stream()
                        .filter(order ->
                                "PAID".equals(order.getPaymentStatus())
                        )
                        .count();

        

        model.addAttribute(
                "totalOrders",
                totalOrders
        );


        // Preparing Orders

        long preparingOrders =
                orders.stream()
                        .filter(order ->
                                "PREPARING".equals(order.getDeliveryStatus())
                        )
                        .count();



        model.addAttribute(
                "preparingOrders",
                preparingOrders
        );


        // Completed Orders

        long completedOrders =
                orders.stream()
                        .filter(order ->
                                "DELIVERED".equals(order.getDeliveryStatus())
                        )
                        .count();

       //RECEIVED

        model.addAttribute(
                "completedOrders",
                completedOrders
        );

        // Cancelable Orders

        long cancelableOrders =
                orders.stream()
                        .filter(order ->
                                "CANCELLED".equals(order.getDeliveryStatus())
                        )
                        .count();


        model.addAttribute(
                "cancelableOrders",
                cancelableOrders
        );

        log.info(
                "Dashboard loaded for customer: {}",
                customer.getEmail()
        );



        return "dashboard";

    }


}
