package com.eshoponcontainers.services;

import java.util.UUID;

import com.eshoponcontainers.entities.IntegrationEventLogEntry;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IntegrationEventLogService {
    
    Flux<IntegrationEventLogEntry> retrieveEventLogsPendingToPublish(UUID transactionId);

    Mono<Void> saveEvent(IntegrationEvent event, UUID transactionId);

    Mono<Void> markEventAsPublished(UUID eventId);

    Mono<Void> markEventAsInProgress(UUID eventId);

    Mono<Void> markEventAsFailed(UUID eventId);
}