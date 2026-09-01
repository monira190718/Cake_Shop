package com.sparktech.restaurant.cake;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1500)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(length = 500)
   
    private String image;

    @Column(nullable = false)
    private boolean available=true;


}
