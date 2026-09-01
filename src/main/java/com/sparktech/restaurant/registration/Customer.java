package com.sparktech.restaurant.registration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Username is required")
    private String name;

    @NotBlank(message = "phonenumber is required")
    private String phonenumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    /*
     * Customer registration-এর সময় address দেওয়া আবশ্যক নয়।
     * পরে Profile থেকে address যোগ করতে পারবে।
     */
    private String address;

    /*
     * Database-এ শুধু profile picture-এর filename থাকবে।
     * Actual picture uploads/profile-pictures directory-তে থাকবে।
     */
    private String profilePicture;


}
