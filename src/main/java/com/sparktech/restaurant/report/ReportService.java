package com.sparktech.restaurant.report;


import com.sparktech.restaurant.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReportService {


    private final OrderRepository orderRepository;



    public Double getThisMonthRevenue(){

        return orderRepository.getThisMonthRevenue();

    }



    public Long getTotalDeliveredOrders(){

        return orderRepository.getTotalDeliveredOrders();

    }



    public Double getAverageOrderValue(){

        return orderRepository.getAverageOrderValue();

    }



    public List<Object[]> getMonthlyRevenue(){

        List<Object[]> data =
                orderRepository.getMonthlyRevenue();


        double max = data.stream()
                .mapToDouble(row ->
                        ((Number)row[1]).doubleValue()
                )
                .max()
                .orElse(1);



        return data.stream()
                .map(row -> new Object[]{

                        getMonthName(
                                ((Number)row[0]).intValue()
                        ),

                        row[1],

                        (((Number)row[1]).doubleValue()
                                / max) * 80

                })
                .toList();

    }



    private String getMonthName(int month){

        return switch(month){

            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";

            default -> "";

        };

    }




}