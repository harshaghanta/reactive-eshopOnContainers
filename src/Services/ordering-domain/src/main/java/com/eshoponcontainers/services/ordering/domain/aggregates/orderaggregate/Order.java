package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import java.util.Date;

import com.eshoponcontainers.services.ordering.domain.Entity;
import com.eshoponcontainers.services.ordering.domain.IAggregateRoot;

public class Order extends Entity implements IAggregateRoot {

    private Date orderDate;
    private Address address;
    private Integer buyerId;
    private OrderStatus orderStatus;
    private String description;
    private boolean isDraft;
    

    public static Order newDraft() {
        Order order = new Order();
        order.isDraft = true;
        return order;
    }

    protected Order() {

    }

    
}
