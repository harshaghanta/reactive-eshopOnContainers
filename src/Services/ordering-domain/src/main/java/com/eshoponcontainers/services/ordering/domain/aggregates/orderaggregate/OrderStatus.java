package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.eshoponcontainers.services.ordering.domain.Enumeration;
import com.eshoponcontainers.services.ordering.domain.exceptions.OrderingDomainException;

public class OrderStatus extends Enumeration {

    public static final OrderStatus Submitted = new OrderStatus(1, "Submitted".toLowerCase());
    public static final OrderStatus AwaitingValidation = new OrderStatus(2, "AwaitingValidation".toLowerCase());
    public static final OrderStatus StockConfirmed = new OrderStatus(3, "StockConfirmed".toLowerCase());
    public static final OrderStatus Paid = new OrderStatus(4, "Paid".toLowerCase());
    public static final OrderStatus Shipped = new OrderStatus(5, "Shipped".toLowerCase());
    public static final OrderStatus Cancelled = new OrderStatus(6, "Cancelled".toLowerCase());

    public OrderStatus(int id, String name) {
        super(id, name);
    }

    public static Collection<OrderStatus> getList() {
        return Arrays.asList(Submitted, AwaitingValidation, StockConfirmed, Paid, Shipped, Cancelled);
    }

    public static OrderStatus fromName(String name) throws OrderingDomainException {
        Optional<OrderStatus> optionalOrderStatus = getList().stream()
                .filter(status -> status.getName().equalsIgnoreCase(name)).findFirst();
                
        if (!optionalOrderStatus.isPresent()) {
            String values = getList().stream().map(status -> status.getName()).collect(Collectors.joining());
            throw new OrderingDomainException("Possible values for OrderStatus: " + values);
        }

        return optionalOrderStatus.get();

    }

    public static OrderStatus from(int id) throws OrderingDomainException {
        Optional<OrderStatus> optionalOrderStatus = getList().stream().filter(status -> status.getId() == id)
                .findFirst();

        if (!optionalOrderStatus.isPresent()) {
            String values = getList().stream().map(status -> String.valueOf(status.getId()))
                    .collect(Collectors.joining());
            throw new OrderingDomainException("Possible values for OrderStatus: " + values);
        }

        return optionalOrderStatus.get();
    }

}
