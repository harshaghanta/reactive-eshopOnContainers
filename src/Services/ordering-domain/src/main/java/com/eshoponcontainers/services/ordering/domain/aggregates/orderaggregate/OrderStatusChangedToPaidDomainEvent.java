package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import java.util.Collection;
import java.util.List;

import an.awesome.pipelinr.Notification;
import lombok.Getter;

@Getter
public class OrderStatusChangedToPaidDomainEvent implements Notification {

    private Collection<OrderItem> orderItems;
    private Integer orderId;
    public OrderStatusChangedToPaidDomainEvent(Integer orderId, List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.orderId = orderId;
    }

}
