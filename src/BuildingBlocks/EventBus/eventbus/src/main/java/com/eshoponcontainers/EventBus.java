package com.eshoponcontainers;

import com.eshoponcontainers.eventbus.abstractions.IntegrationEventHandler;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;

import reactor.core.publisher.Mono;


public interface EventBus {
    
    Mono<Void> publish(IntegrationEvent event);

    <T extends IntegrationEvent, TH extends IntegrationEventHandler<T>> Mono<Void> subscribe(Class<T> eventType, Class<TH> eventHandlerType);

    // <T extends IntegrationEvent, TH extends IntegrationEventHandler<T>> Mono<Void> unsubscribe();
}
