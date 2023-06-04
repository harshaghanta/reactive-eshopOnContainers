package com.eshoponcontainers.services.ordering.domain.events;

import java.util.Date;

import com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate.Order;

import an.awesome.pipelinr.Notification;
import lombok.Getter;

@Getter
public class OrderStartedDomainEvent implements Notification {

    private String userId;
    private String userName;
    private int cardTypeId;
    private String cardNumber;
    private String cardSecurityNumber;
    private String cardHolderName;
    private Date cardExpiration;
    private final Order order;

    public OrderStartedDomainEvent(Order order, String userId, String userName, int cardTypeId, String cardNumber,
            String cardSecurityNumber, String cardHolderName, Date cardExpiration) {

        this.order = order;
        this.userId = userId;
        this.userName = userName;
        this.cardTypeId = cardTypeId;
        this.cardNumber = cardNumber;
        this.cardSecurityNumber = cardSecurityNumber;
        this.cardHolderName = cardHolderName;
        this.cardExpiration = cardExpiration;
    }
}
