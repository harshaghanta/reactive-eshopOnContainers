package com.eshoponcontainers.services.ordering.domain.events;

import java.util.Collection;
import java.util.List;

import com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate.OrderItem;

import an.awesome.pipelinr.Notification;
import lombok.Getter;

@Getter
public class OrderStatusChangedToAwaitingValidationDomainEvent implements Notification {

    private Integer orderId;
    private Collection<OrderItem> orderItems;

    public OrderStatusChangedToAwaitingValidationDomainEvent(Integer id, List<OrderItem> orderItems) {
        this.orderId = id;
        this.orderItems = orderItems;
    }

}
