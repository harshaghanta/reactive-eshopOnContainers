package com.eshoponcontainers.catalogapi.eventhandlers;

import com.eshoponcontainers.catalogapi.events.PriceChangedEvent;
import com.eshoponcontainers.eventbus.abstractions.IntegrationEventHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class PriceChangedEventHandler implements IntegrationEventHandler<PriceChangedEvent> {

    @Override
    public Mono<Void> handle(PriceChangedEvent event) {
        
        log.info("Price changed event handler completed");
        return Mono.empty().then(method1()).then(method2());
    }

    public Mono<Void> method1() {
        log.info("Executing method 1");
        return Mono.empty();
    }

    public Mono<Void> method2() {
        log.info("Executing method 2");
        return Mono.empty().then(Mono.empty());
    }
    
}
