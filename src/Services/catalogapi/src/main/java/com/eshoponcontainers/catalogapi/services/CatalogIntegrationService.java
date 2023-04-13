package com.eshoponcontainers.catalogapi.services;

import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eshoponcontainers.EventBus;
import com.eshoponcontainers.catalogapi.entities.CatalogItem;
import com.eshoponcontainers.catalogapi.repositories.CatalogItemRepository;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.eshoponcontainers.services.IntegrationEventLogService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CatalogIntegrationService {

    @Autowired
    private CatalogItemRepository catalogItemRepository;

    @Autowired
    private IntegrationEventLogService eventLogService;

    @Autowired
    private EventBus eventBus;
  

	@Transactional
	public Mono<Void> saveEventAndCatalogChanges(IntegrationEvent event, CatalogItem requestedItem) {
		catalogItemRepository.save(requestedItem);
		log.debug("In Save Event And Catalog Changes", requestedItem);
		// TODO : HIGH : NEED TO GET TRANSSACTION ID
		UUID transactionId = UUID.randomUUID();
		return eventLogService.saveEvent(event, transactionId);
	}

	public Mono<Void> publishThroughEventBus(IntegrationEvent event) {
		log.debug("publishThroughEventBus", event);
		eventLogService.markEventAsInProgress(event.getId());
		eventBus.publish(event);
		eventLogService.markEventAsPublished(event.getId());
		return Mono.empty();
	}
    
}