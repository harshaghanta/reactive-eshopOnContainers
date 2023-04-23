package com.eshoponcontainers.catalogapi.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.eshoponcontainers.EventBus;
import com.eshoponcontainers.catalogapi.entities.CatalogItem;
import com.eshoponcontainers.catalogapi.repositories.CatalogItemRepository;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.eshoponcontainers.services.IntegrationEventLogService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class CatalogIntegrationService {

	private final CatalogItemRepository catalogItemRepository;
	private final IntegrationEventLogService eventLogService;
	private final EventBus eventBus;

	public Mono<Void> saveEventAndCatalogChanges(IntegrationEvent event, CatalogItem requestedItem) {
		UUID transactionId = UUID.randomUUID();
		System.out.println("In SaveEventAndCatalogChanges");
		return Mono.just(catalogItemRepository.save(requestedItem).then(eventLogService.saveEvent(event, transactionId)).subscribe()).then();
	}

	public Mono<Void> publishThroughEventBus(IntegrationEvent event) {
		System.out.println("publishThroughEventBus");
		return eventLogService.markEventAsInProgress(event.getId()).thenReturn(eventBus.publish(event).subscribe())
				.then(eventLogService.markEventAsPublished(event.getId()));
	}

}