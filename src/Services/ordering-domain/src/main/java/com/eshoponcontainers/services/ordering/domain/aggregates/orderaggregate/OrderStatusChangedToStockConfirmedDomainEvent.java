package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import an.awesome.pipelinr.Notification;
import lombok.Getter;

@Getter
public class OrderStatusChangedToStockConfirmedDomainEvent implements Notification {

    private Integer orderId;
    public OrderStatusChangedToStockConfirmedDomainEvent(Integer orderId) {
        this.orderId = orderId;
    }

}
