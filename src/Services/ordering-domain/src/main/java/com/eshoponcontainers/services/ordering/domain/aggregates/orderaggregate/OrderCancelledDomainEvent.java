package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import an.awesome.pipelinr.Notification;
import lombok.Getter;

@Getter
public class OrderCancelledDomainEvent implements Notification {

    private final Order order;

    public OrderCancelledDomainEvent(Order order) {
        this.order = order;
    }

}
