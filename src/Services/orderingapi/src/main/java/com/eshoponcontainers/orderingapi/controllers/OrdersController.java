package com.eshoponcontainers.orderingapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshoponcontainers.orderingapi.commands.CancelOrderCommand;
import com.eshoponcontainers.orderingapi.dto.CardType;
import com.eshoponcontainers.orderingapi.dto.Order;
import com.eshoponcontainers.orderingapi.dto.OrderSummary;
import com.eshoponcontainers.orderingapi.queries.GetAllCardTypesQuery;

import an.awesome.pipelinr.Pipeline;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {


    @Autowired
    private Pipeline pipelinr;

    @PutMapping("/cancel")
    public Mono<ResponseEntity<Void>> cancelOrder(@RequestHeader("x-requestid") String requestId, @RequestBody CancelOrderCommand command) {
        Mono<Boolean> commandResult = null;

        return commandResult.flatMap(cancelled -> {
            if(cancelled)
                return Mono.just(ResponseEntity.ok().build());
            else
                return Mono.just(ResponseEntity.badRequest().build());
        });        
    }

    @PutMapping
    public Mono<ResponseEntity<Void>> shipOrder(@RequestHeader("x-requestid") String requestId, @RequestBody CancelOrderCommand command) {
        Mono<Boolean> commandResult = null;
        return commandResult.flatMap(cancelled -> {
            if(cancelled)
                return Mono.just(ResponseEntity.ok().build());
            else
                return Mono.just(ResponseEntity.badRequest().build());
        });        
    }

    @GetMapping("/{orderId}")
    public Mono<ResponseEntity<Order>> getOrder(@PathVariable Integer orderId) {
        Mono<Order> orderMono = null;
        return orderMono.flatMap(order -> {
            if(order == Order.NULL)
                return Mono.just(ResponseEntity.notFound().build());
            else
                return Mono.just(ResponseEntity.ok(order));
        });
    }

    @GetMapping
    public Mono<ResponseEntity<OrderSummary>> getOrders() {
        return Mono.just(ResponseEntity.ok(new OrderSummary()));
    }

    @GetMapping("/cardtypes")
    public Flux<CardType> getCardTypes() {
         return pipelinr.send(new GetAllCardTypesQuery());
    }
    
}
