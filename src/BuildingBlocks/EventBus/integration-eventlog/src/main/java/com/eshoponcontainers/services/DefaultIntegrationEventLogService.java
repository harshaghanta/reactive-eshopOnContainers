package com.eshoponcontainers.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.eshoponcontainers.entities.EventStateEnum;
import com.eshoponcontainers.entities.IntegrationEventLogEntry;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.eshoponcontainers.repositories.IntegrationEventLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultIntegrationEventLogService implements IntegrationEventLogService {

    private final IntegrationEventLogRepository eventLogRepository;

    @Override
    public Flux<IntegrationEventLogEntry> retrieveEventLogsPendingToPublish(UUID transactionId) {
        return eventLogRepository.findByTransactionIdAndState(transactionId, EventStateEnum.NOT_PUBLISHED.getValue())
                .flatMap(entry -> {
                    entry.deserializeEventContent();
                    return Mono.just(entry);
                });
    }

    @Override
    public Mono<IntegrationEventLogEntry> saveEvent(IntegrationEvent event, UUID transactionId) {
        IntegrationEventLogEntry entry = new IntegrationEventLogEntry(event, transactionId);
        return eventLogRepository.save(entry);
    }

    @Override
    public Mono<Void> markEventAsPublished(UUID eventId) {
        return udpateEventStatus(eventId, EventStateEnum.PUBLISHED);
    }

    private Mono<Void> udpateEventStatus(UUID eventId, EventStateEnum eventState) {
        return eventLogRepository.findById(eventId).flatMap(entry -> {
            entry.setState(eventState);
            if (entry.getState() == EventStateEnum.IN_PROGRESS) {
                entry.setTimesSent(entry.getTimesSent() + 1);
            }
            return eventLogRepository.save(entry).flatMap(savedentry -> Mono.empty());
        });
    }

    @Override
    public Mono<Void> markEventAsInProgress(UUID eventId) {
        return udpateEventStatus(eventId, EventStateEnum.IN_PROGRESS);
    }

    @Override
    public Mono<Void> markEventAsFailed(UUID eventId) {
        return udpateEventStatus(eventId, EventStateEnum.PUBLISH_FAILED);
    }

}
