package com.sparktech.restaurant.customize;

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
public class CustomizeOrder {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private Customer customer;


    private String image;


    private String description;


    private String phone;


    private String address;


    private LocalDate deliveryDate;


    private String status = "PENDING";


    private Double quotedPrice;


    private String adminMessage;


    private LocalDateTime createdAt;

}
