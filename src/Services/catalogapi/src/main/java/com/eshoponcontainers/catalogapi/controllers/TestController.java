package com.eshoponcontainers.catalogapi.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshoponcontainers.EventBus;
import com.eshoponcontainers.catalogapi.events.PriceChangedEvent;
import com.eshoponcontainers.entities.IntegrationEventLogEntry;
import com.eshoponcontainers.services.IntegrationEventLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final EventBus eventBus;
    private final IntegrationEventLogService eventLogService;

    @GetMapping("/test")
    public Mono<String> get() {

        log.info(eventBus.getClass().getName());

        PriceChangedEvent priceChangedEvent = new PriceChangedEvent(1, 5.65, 9.95);

        eventBus.publish(priceChangedEvent).subscribe();

        return Mono.just("Test");
    }

    @GetMapping("/test2")
    public Flux<IntegrationEventLogEntry> getEvents() {
         return eventLogService.retrieveEventLogsPendingToPublish(UUID.fromString("28f4487c-31d7-46ad-be4a-7bb5f4ddd35c"));

    }

    @GetMapping("/inprogress")
    public Mono<Void> updateEventToInProgress() {
        return eventLogService.markEventAsInProgress(UUID.fromString("F2967C19-6546-4C68-A80E-B04D5F08F788"));
    }

    @GetMapping("/save")
    public Mono<IntegrationEventLogEntry> saveEvent() {
        PriceChangedEvent priceChangedEvent = new PriceChangedEvent(1, 15d, 25d);
        return eventLogService.saveEvent(priceChangedEvent, UUID.randomUUID());
    }

    
}