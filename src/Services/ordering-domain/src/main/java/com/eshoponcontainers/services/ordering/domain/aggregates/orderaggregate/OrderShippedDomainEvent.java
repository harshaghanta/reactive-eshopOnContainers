package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import an.awesome.pipelinr.Notification;
import lombok.Getter;

@Getter
public class OrderShippedDomainEvent implements Notification {

    private final Order order;

    public OrderShippedDomainEvent(Order order) {
        this.order = order;
    }

}
