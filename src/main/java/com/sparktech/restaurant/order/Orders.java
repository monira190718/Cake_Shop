package com.sparktech.restaurant.order;

import com.sparktech.restaurant.cake.Cake;
import com.sparktech.restaurant.registration.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private Customer customer;


    @ManyToOne
    private Cake cake;


    private int quantity;

    private double totalPrice;
    
    private String address;
    
    private String phone;

    private LocalDate deliveryDate;


    private LocalDateTime orderTime;


    @Column(nullable = false)
    private String status = "PENDING";

    private String customerMessage;

    private String paymentMethod;

    private String paymentStatus = "UNPAID";

    private String customImage;

    private String deliveryStatus;

    @ManyToOne
    private Customer deliveryMan;

    private String deliveryAssignStatus;

    
}
