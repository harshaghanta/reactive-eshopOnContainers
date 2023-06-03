package com.eshoponcontainers.orderingapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrderItem {
    
    private String productName;
    private int units;
    private double unitPrice;
    private String pictureUrl;
}
