package com.eshoponcontainers.eventbus.events;

import com.eshoponcontainers.eventbus.abstractions.IntegrationEventHandler;

import reactor.core.publisher.Mono;

public class TestIntegrationEventHandler implements IntegrationEventHandler<TestIntegrationEvent> {

    @Override
    public Mono<Void> handle(TestIntegrationEvent event) {        
        return Mono.empty();
    }
    
}
