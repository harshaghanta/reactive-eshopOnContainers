package com.eshoponcontainers.catalogapi.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		catalogItemRepository.save(requestedItem);
		System.out.println("In Save Event And Catalog Changes " +requestedItem);
		// TODO : HIGH : NEED TO GET TRANSSACTION ID AND Check @Transactional
		UUID transactionId = UUID.randomUUID();
		return eventLogService.saveEvent(event, transactionId);
	}

	public Mono<Void> publishThroughEventBus(IntegrationEvent event) {
		System.out.println("publishThroughEventBus " +event);
		eventLogService.markEventAsInProgress(event.getId());
		eventBus.publish(event);
		eventLogService.markEventAsPublished(event.getId());
		return Mono.empty();
	}
    
}