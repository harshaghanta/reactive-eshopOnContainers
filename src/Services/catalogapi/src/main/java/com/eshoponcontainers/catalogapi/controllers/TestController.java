package com.eshoponcontainers.catalogapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshoponcontainers.EventBus;
import com.eshoponcontainers.catalogapi.events.PriceChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final EventBus eventBus;

    @GetMapping("/test")
    public Mono<String> get() {

        log.info(eventBus.getClass().getName());

        PriceChangedEvent priceChangedEvent = new PriceChangedEvent(1, 5.65, 9.95);

        eventBus.publish(priceChangedEvent).subscribe();

        return Mono.just("Test");
    }
}