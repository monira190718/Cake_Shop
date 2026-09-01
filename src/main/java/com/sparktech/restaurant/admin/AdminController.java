package com.sparktech.restaurant.admin;

import com.sparktech.restaurant.cake.Cake;
import com.sparktech.restaurant.cake.CakeRepository;
import com.sparktech.restaurant.category.CategoryRepository;
import com.sparktech.restaurant.customize.CustomizeOrder;
import com.sparktech.restaurant.customize.CustomizeOrderRepository;
import com.sparktech.restaurant.order.OrderRepository;
import com.sparktech.restaurant.order.OrderService;
import com.sparktech.restaurant.order.Orders;
import com.sparktech.restaurant.registration.Customer;
import com.sparktech.restaurant.registration.CustomerRepository;
import com.sparktech.restaurant.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/cakeshop")
@RequiredArgsConstructor
public class AdminController {

    private final CustomerRepository customerRepository;
   
    private final CakeRepository cakeRepository;
    @Autowired
    private OrderRepository orderRepository;

    private final CustomizeOrderRepository customizeOrderRepository;

    private final OrderService orderService;

    private final ReportService reportService;

    @GetMapping("/admin")
    public String adminDashboard(
            Authentication authentication,
            Model model
    ) {
        Customer admin = customerRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin not found"
                        )
                );

        /*
         * Logged-in Admin/Head Admin information
         */
        model.addAttribute("admin", admin);



        /*
         * Cakes:
         * ID descending — সর্বশেষ Add করা Cake আগে
         */
        model.addAttribute(
                "cakes",
                cakeRepository
                        .findAllByOrderByIdDesc()
        );


       /* model.addAttribute(
                "customOrders",
                customizeOrderRepository.findAll()
        ); */

       /* model.addAttribute(
                "customOrders",
                customizeOrderRepository.findByStatusNot("CANCELLED")
        );*/

        model.addAttribute(
                "customOrders",
                customizeOrderRepository
                        .findByStatusNotIn(
                                List.of(
                                        "CANCELLED",
                                        "WAITING_CONFIRMATION",
                                        "ACCEPTED"
                                )
                        )
        );


        model.addAttribute(
                "orders",
                orderRepository.findAll()
        );

        model.addAttribute(
                "payments",
                orderRepository.findByStatus("APPROVED")
        );

        

        model.addAttribute(
                "paidOrders",
                orderService.getPaidOrders()
        );

        model.addAttribute(
                "assignOrders",
                orderRepository.findByDeliveryStatus("PREPARING")
        );

        model.addAttribute(
                "deliveryMen",
                customerRepository.findByRole("DELIVERYMAN")
        );

        
        //
        model.addAttribute(
                "thisMonthRevenue",
                reportService.getThisMonthRevenue()
        );


        model.addAttribute(
                "totalDeliveredOrders",
                reportService.getTotalDeliveredOrders()
        );


        model.addAttribute(
                "averageOrderValue",
                reportService.getAverageOrderValue()
        );


        model.addAttribute(
                "monthlyRevenue",
                reportService.getMonthlyRevenue()
        );

        return "admin";


    }

    //delete

    @PostMapping("/admin/cakes/delete/{id}")
    public String deleteCake(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Cake cake = cakeRepository.findById(id)
                .orElse(null);

        if (cake == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cake was not found."
            );

            return "redirect:/cakeshop/admin?section=cakes";
        }

        String imageUrl = cake.getImage();

        try {
            /*
             * Database থেকে Cake delete।
             */
            cakeRepository.delete(cake);
            cakeRepository.flush();

            /*
             * uploads/cakes থেকে picture delete।
             */
            if (imageUrl != null && !imageUrl.isBlank()) {

                String fileName = Paths.get(imageUrl)
                        .getFileName()
                        .toString();

                Path imagePath = Paths.get(
                        "uploads",
                        "cakes",
                        fileName
                ).toAbsolutePath().normalize();

                Files.deleteIfExists(imagePath);
            }

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Cake deleted successfully."
            );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cake could not be deleted."
            );
        }

        return "redirect:/cakeshop/admin?section=cakes";
    }

    //view order
    @GetMapping("/view-orders")
    public String viewOrders(Model model){

        List<Orders> orders = orderRepository.findAll();

        model.addAttribute("orders", orders);

        return "admin";
    }

    //approve
    @GetMapping("/order/approve/{id}")
    public String approveOrder(
            @PathVariable Long id
    ){

        Orders order =
                orderRepository.findById(id)
                        .orElseThrow();


        order.setStatus("APPROVED");

        order.setCustomerMessage(
                "Your order has been approved. Please complete the payment to confirm your order."
        );

        order.setPaymentStatus("PENDING");
        orderRepository.save(order);


        return "redirect:/cakeshop/admin?section=orders";
    }
    
    //reject
    @GetMapping("/order/reject/{id}")
    public String rejectOrder(
            @PathVariable Long id
    ){

        Orders order =
                orderRepository.findById(id)
                        .orElseThrow();


        order.setStatus("REJECTED");


        orderRepository.save(order);


        return "redirect:/cakeshop/admin?section=orders";
    }

    //customize
    @PostMapping("/customize/send-price")
    public String sendPrice(
            @RequestParam Long id,
            @RequestParam Double price,
            @RequestParam String message
    ){


        CustomizeOrder custom =
                customizeOrderRepository.findById(id)
                        .orElseThrow();



        custom.setQuotedPrice(price);


        custom.setAdminMessage(message);


        custom.setStatus(
                "WAITING_CONFIRMATION"
        );



        customizeOrderRepository.save(custom);



        return "redirect:/cakeshop/admin?section=customize";

    }

    //showcustom
    @GetMapping("/customize/price/{id}")
    public String showPriceForm(
            @PathVariable Long id,
            Model model
    ){

        CustomizeOrder customOrder =
                customizeOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Custom order not found")
                        );


        model.addAttribute(
                "customOrder",
                customOrder
        );


        return "custom-price";

    }

    //delivberyman
    
    @PostMapping("/admin/assign-delivery/{id}")
    public String assignDeliveryMan(
            @PathVariable Long id,
            @RequestParam Long deliveryManId
    ) {

        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Customer deliveryMan = customerRepository.findById(deliveryManId)
                .orElseThrow(() -> new RuntimeException("Delivery man not found"));

        if (!"DELIVERYMAN".equals(deliveryMan.getRole())) {
            throw new RuntimeException("Selected user is not a delivery man");
        }

        order.setDeliveryMan(deliveryMan);

        order.setDeliveryAssignStatus(
                "PENDING_CONFIRMATION"
        );

        orderRepository.save(order);

        return "redirect:/cakeshop/admin?section=assign-delivery";
    }


    //reassign
    @PostMapping("/admin/reassign-delivery/{id}")
    public String reassignDeliveryMan(
            @PathVariable Long id,
            @RequestParam Long deliveryManId
    ) {


        Orders order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Order not found")
                        );


        Customer newDeliveryMan =
                customerRepository.findById(deliveryManId)
                        .orElseThrow(() ->
                                new RuntimeException("Delivery man not found")
                        );


        if(!"DELIVERYMAN".equals(newDeliveryMan.getRole())){

            throw new RuntimeException(
                    "Selected user is not delivery man"
            );

        }


        // পুরাতন delivery man replace হবে

        order.setDeliveryMan(newDeliveryMan);


        // আবার confirmation লাগবে

        order.setDeliveryAssignStatus(
                "PENDING_CONFIRMATION"
        );


        orderRepository.save(order);



        return "redirect:/cakeshop/admin?section=assign-delivery";

    }

    @GetMapping("/admin/customize/cancel/{id}")
    public String adminCancelCustomOrder(
            @PathVariable Long id
    ){

        CustomizeOrder order =
                customizeOrderRepository.findById(id)
                        .orElseThrow();


        order.setStatus("CANCELLED");


        customizeOrderRepository.save(order);


        return "redirect:/cakeshop/admin?section=customize";

    }
}