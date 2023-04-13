package com.eshoponcontainers.services.impl;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.eshoponcontainers.EventStateEnum;
import com.eshoponcontainers.entities.IntegrationEventLogEntry;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.eshoponcontainers.repositories.IntegrationEventLogRepository;
import com.eshoponcontainers.services.IntegrationEventLogService;

import reactor.core.publisher.Mono;

@Service
public class DefaultIntegrationEventLogService implements IntegrationEventLogService {

	IntegrationEventLogRepository eventLogRepository;

	public DefaultIntegrationEventLogService(IntegrationEventLogRepository eventLogRepository) {
		this.eventLogRepository = eventLogRepository;
	}

	@Override
	public List<IntegrationEventLogEntry> retrieveEventLogsPendingToPublish(UUID transactionId) {
		List<IntegrationEventLogEntry> pendingEvents = eventLogRepository
				.findByTransactionIdAndState(transactionId.toString(), EventStateEnum.NOT_PUBLISHED);

		if (pendingEvents != null && !pendingEvents.isEmpty()) {
			pendingEvents.sort(Comparator.comparing(IntegrationEventLogEntry::getCreationTime));
			pendingEvents.forEach(x -> x.deserializeEventContent());
			return pendingEvents;
		}

		return null;
	}

	@Override
	public Mono<Void> saveEvent(IntegrationEvent event, UUID transactionId) {
		IntegrationEventLogEntry logEntry = new IntegrationEventLogEntry(event, transactionId);
		eventLogRepository.save(logEntry);
		return Mono.empty();
	}

	@Override
	public Mono<Void> markEventAsPublished(UUID eventId) {

		return updateEventStatus(eventId, EventStateEnum.PUBLISHED);
	}

	@Override
	public Mono<Void> markEventAsInProgress(UUID eventId) {
		return updateEventStatus(eventId, EventStateEnum.IN_PROGRESS);
	}

	@Override
	public Mono<Void> markEventAsFailed(UUID eventId) {
		return updateEventStatus(eventId, EventStateEnum.PUBLISH_FAILED);

	}

	private Mono<Void> updateEventStatus(UUID eventId, EventStateEnum status) {
		eventLogRepository.findById(eventId).map(event -> {
			event.setState(status);
			if (event.getState() == EventStateEnum.IN_PROGRESS) {
				event.setTimesSent(event.getTimesSent() + 1);
			}
			return eventLogRepository.save(event);		
		});
		return Mono.empty();
	}
}