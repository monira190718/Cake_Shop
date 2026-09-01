package com.sparktech.restaurant.order;

import com.sparktech.restaurant.registration.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;




    public Orders saveOrder(Orders order){


        order.setOrderTime(
                LocalDateTime.now()
        );


        order.setOrderTime(
                LocalDateTime.now()
        );

        if(order.getStatus()==null){
            order.setStatus("PENDING");
        }


        return orderRepository.save(order);

    }





    public List<Orders> getCustomerOrders(
            Customer customer
    ){

        return orderRepository.findByCustomer(customer);

    }

    public Orders getOrderById(Long id){

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

    }

    public List<Orders> getPaidOrders(){

        return orderRepository
                .findByPaymentStatus("PAID");

    }

    public void deleteOrder(Long id){

        orderRepository.deleteById(id);

    }


}
