package com.eshoponcontainers.orderingapi.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order {

    public static final Order NULL = new NullOrder();
    private Integer orderNumber;
    private LocalDate date;
    private String status;
    private String description;
    private String street;
    private String city;
    private String zipcode;
    private String country;
    private List<OrderItem> orderItems;
    private double total;
}
