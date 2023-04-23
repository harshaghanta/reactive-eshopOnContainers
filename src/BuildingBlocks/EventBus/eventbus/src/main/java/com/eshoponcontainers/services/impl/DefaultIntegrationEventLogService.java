package com.eshoponcontainers.services.impl;

import java.util.Comparator;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.eshoponcontainers.EventStateEnum;
import com.eshoponcontainers.entities.IntegrationEventLogEntry;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.eshoponcontainers.repositories.IntegrationEventLogRepository;
import com.eshoponcontainers.services.IntegrationEventLogService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class DefaultIntegrationEventLogService implements IntegrationEventLogService {

	private final IntegrationEventLogRepository eventLogRepository;

	@Override
	public Flux<IntegrationEventLogEntry> retrieveEventLogsPendingToPublish(UUID transactionId) {
		return eventLogRepository
				.findByTransactionIdAndState(transactionId.toString(), EventStateEnum.NOT_PUBLISHED)
				.sort(Comparator.comparing(IntegrationEventLogEntry::getCreationTime))
				.map(x ->  {
					x.deserializeEventContent();
					return x;
				});
	}

	@Override
	public Mono<Void> saveEvent(IntegrationEvent event, UUID transactionId) {
		IntegrationEventLogEntry logEntry = new IntegrationEventLogEntry(event, transactionId);
		System.out.println("logEntry : " + logEntry);
		return Mono.just(eventLogRepository.save(logEntry).subscribe()).then();
	}

	@Override
	public Mono<Void> markEventAsPublished(UUID eventId) {
		return updateEventStatus(eventId, EventStateEnum.PUBLISHED.getValue());
	}

	@Override
	public Mono<Void> markEventAsInProgress(UUID eventId) {
 		return updateEventStatus(eventId, EventStateEnum.IN_PROGRESS.getValue());
	}

	@Override
	public Mono<Void> markEventAsFailed(UUID eventId) {
		return updateEventStatus(eventId, EventStateEnum.PUBLISH_FAILED.getValue());
	}

	private Mono<Void> updateEventStatus(UUID eventId, Integer status) {
		return Mono.just(eventLogRepository.findById(eventId)
		.map(event -> {
			event.setState(status);
			System.out.println("eventId : " + eventId + " Status : " + status);
			if (event.getState() == EventStateEnum.IN_PROGRESS.getValue()) {
				event.setTimesSent(event.getTimesSent() + 1);
			}
			event.setNewIntegrationEventLogEntry(false);
			return eventLogRepository.save(event).subscribe();		
		}).subscribe()).then();
	}
}