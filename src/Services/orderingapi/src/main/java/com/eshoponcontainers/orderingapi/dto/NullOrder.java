package com.eshoponcontainers.orderingapi.dto;

public class NullOrder extends Order {
    
    @Override
    public Integer getOrderNumber() {        
        return null;
    }
}
