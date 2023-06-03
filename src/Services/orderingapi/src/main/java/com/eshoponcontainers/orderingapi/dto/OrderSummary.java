package com.eshoponcontainers.orderingapi.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderSummary {
    
    private int orderNumber;
    private LocalDate date;
    private String status;
    private double total;
}
