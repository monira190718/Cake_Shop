package com.sparktech.restaurant.cake;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CakeRepository extends JpaRepository<Cake,Long> {

    List<Cake> findAllByOrderByIdDesc();

    List<Cake> findByAvailableTrueOrderByIdDesc();

}
