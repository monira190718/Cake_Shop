package com.sparktech.restaurant.order;

import com.sparktech.restaurant.registration.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {
    List<Orders> findByCustomer(Customer customer);

    List<Orders> findByStatus(String status);

    List<Orders> findByPaymentStatus(String paymentStatus);

    List<Orders> findByDeliveryStatus(String deliveryStatus);

    List<Orders> findByDeliveryMan(Customer deliveryMan);

    // Monthly revenue graph

    @Query("""
    SELECT MONTH(o.orderTime),
    SUM(o.totalPrice)
    FROM Orders o
    WHERE o.deliveryStatus='DELIVERED'
    GROUP BY MONTH(o.orderTime)
    ORDER BY MONTH(o.orderTime)
    """)
    List<Object[]> getMonthlyRevenue();



    // This month revenue

    @Query("""
    SELECT COALESCE(SUM(o.totalPrice),0)
    FROM Orders o
    WHERE o.deliveryStatus='DELIVERED'
    AND MONTH(o.orderTime)=MONTH(CURRENT_DATE)
    AND YEAR(o.orderTime)=YEAR(CURRENT_DATE)
    """)
    Double getThisMonthRevenue();
    
    // Total delivered orders

    @Query("""
    SELECT COUNT(o)
    FROM Orders o
    WHERE o.deliveryStatus='DELIVERED'
    """)
    Long getTotalDeliveredOrders();

    // Average order value

    @Query("""
    SELECT COALESCE(AVG(o.totalPrice),0)
    FROM Orders o
    WHERE o.deliveryStatus='DELIVERED'
    """)
    Double getAverageOrderValue();


}
