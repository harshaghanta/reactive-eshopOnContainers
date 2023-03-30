package com.eshoponcontainers.eventbus.abstractions;

import com.eshoponcontainers.eventbus.events.IntegrationEvent;

import reactor.core.publisher.Mono;

public interface IntegrationEventHandler<T extends IntegrationEvent>  {
    
    Mono<Void> handle(T event);
}
