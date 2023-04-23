package com.eshoponcontainers.catalogapi.eventhandlers;

import com.eshoponcontainers.catalogapi.events.ProductPriceChangedIntegrationEvent;
import com.eshoponcontainers.eventbus.abstractions.IntegrationEventHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ProductPriceChangedEventHandler implements IntegrationEventHandler<ProductPriceChangedIntegrationEvent> {

	public Mono<Void> handle(ProductPriceChangedIntegrationEvent event) {
		log.info("Product price changed from " + event.getOldPrice() + " to " + event.getNewPrice());
		return Mono.empty();
	}

}