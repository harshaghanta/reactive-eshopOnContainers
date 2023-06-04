package com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.eshoponcontainers.services.ordering.domain.Entity;
import com.eshoponcontainers.services.ordering.domain.IAggregateRoot;
import com.eshoponcontainers.services.ordering.domain.events.OrderStartedDomainEvent;
import com.eshoponcontainers.services.ordering.domain.events.OrderStatusChangedToAwaitingValidationDomainEvent;
import com.eshoponcontainers.services.ordering.domain.exceptions.OrderingDomainException;

import lombok.Getter;

@Getter
public class Order extends Entity implements IAggregateRoot {

    private Date orderDate;
    private Address address;
    private Integer buyerId;
    private OrderStatus orderStatus;
    private String description;
    private boolean isDraft;
    private final List<OrderItem> orderItems;
    private Integer paymentMethodId;

    public static Order newDraft() {
        Order order = new Order();
        order.isDraft = true;
        return order;
    }

    protected Order() {
        orderItems = new ArrayList<OrderItem>();
        isDraft = false;
    }

    public Order(String userId, String userName, Address address, int cardTypeId, String cardNumber,
            String cardSecurityNumber,
            String cardHolderName, Date cardExpiration, Integer buyerId, Integer paymentMethodId) {

        this();
        this.buyerId = buyerId;
        this.orderStatus = OrderStatus.Submitted;
        this.paymentMethodId = paymentMethodId;
        this.orderDate = new Date();
        this.address = address;

        addOrderStartedDomainEvent(userId, userName, cardTypeId, cardNumber, cardSecurityNumber, cardHolderName, cardExpiration);

    }

    private void addOrderStartedDomainEvent(String userId, String userName, int cardTypeId, String cardNumber,
            String cardSecurityNumber, String cardHolderName, Date cardExpiration) {
        
        OrderStartedDomainEvent event = new OrderStartedDomainEvent(this, userId, userName, cardTypeId, cardNumber, cardSecurityNumber, cardHolderName, cardExpiration);
        this.addDomainEvent(event);
    }

    public void addOrderItem(int productId, String productName, double unitPrice, double discount, String pictureUrl, int units) throws OrderingDomainException {
        OrderItem orderItem = null;
        Optional<OrderItem> optionOrderItem = orderItems.stream().filter(item -> item.getProductId() == productId).findFirst();
        if(optionOrderItem.isPresent()) {
            orderItem = optionOrderItem.get();
            if(discount > orderItem.getDiscount()) {
                orderItem.setNewDiscount(discount);
            }
            orderItem.addUnits(units);
        }
        else {
            orderItem = new OrderItem(productId, productName, unitPrice, discount, pictureUrl, units);
            orderItems.add(orderItem);
        }
    }

    public void setPaymentId(int id) {
        this.paymentMethodId = id;
    }

    public void setBuyerId(int id) {
        this.buyerId = id;
    }

    public void setAwaitingValidationStatus() {
        if(orderStatus == OrderStatus.Submitted) {
            addDomainEvent(new OrderStatusChangedToAwaitingValidationDomainEvent(this.getId(), orderItems));
            this.orderStatus = OrderStatus.AwaitingValidation;
        }
    }

    public void setStockConfirmedStatus() {
        if(orderStatus == OrderStatus.AwaitingValidation) {
            addDomainEvent(new OrderStatusChangedToStockConfirmedDomainEvent(this.getId()));
            orderStatus = OrderStatus.StockConfirmed;
            description = "All the items were confirmed with available stock.";
        }
    }

    public void setPaidStatus() {
        if(orderStatus == OrderStatus.StockConfirmed) {
            addDomainEvent(new OrderStatusChangedToPaidDomainEvent(getId(), orderItems));
            orderStatus = OrderStatus.Paid;
            description = "The payment was performed at a simulated \"American Bank checking bank account ending on XX35071\"";
        }
    }

    public void setShippedStatus() throws OrderingDomainException {
        if(orderStatus != OrderStatus.Paid) {
            statusChangeException(OrderStatus.Shipped);
        }
        orderStatus = OrderStatus.Shipped;
        description = "The order was shipped.";
        addDomainEvent(new OrderShippedDomainEvent(this));
    }

    public void setCancelledStatus() throws OrderingDomainException {
        if(orderStatus == OrderStatus.Paid || orderStatus == OrderStatus.Shipped) {
            statusChangeException(OrderStatus.Cancelled);
        }
        orderStatus = OrderStatus.Cancelled;
        description = "The order was canncelled.";
        addDomainEvent(new OrderCancelledDomainEvent(this));
    }

    public void setCancelledStatusWhenStockIsRejected(Collection<Integer> orderStockRejectedItems) {

        if(orderStatus == OrderStatus.AwaitingValidation) {
            orderStatus = OrderStatus.Cancelled;

            String rejectedItems = orderItems.stream().filter(item -> orderStockRejectedItems.contains(item.getProductId()))
                .map(item -> item.getProductName())
                .collect(Collectors.joining());
            
            description = "The product items don't have stock:(" + rejectedItems + ").";
        }
    }

    public double getTotal() {
        return orderItems.stream().mapToDouble(item -> item.getUnits() * item.getUnitPrice())
        .sum();
    }

    private void statusChangeException(OrderStatus orderStatusToChange) throws OrderingDomainException {
        throw new OrderingDomainException("Is not possible to change the order status from " +orderStatus.getName() + " to " + orderStatusToChange.getName() + "." );
    }

}
