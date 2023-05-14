package com.eshoponcontainers.catalogapi.controllers;

import static org.mockito.ArgumentMatchers.nullable;

import java.io.Console;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class TestControllerTest {
    @Test
    void testGet() throws InterruptedException {
            log.info("Before mono declaration");
            Mono<Long> delayMono = Mono.delay(Duration.ofSeconds(5));
            log.info("After mono declaration ");
            delayMono.subscribe(delay -> {log.info("After mono subscribe and completion");});
            log.info("back to main thread.");
            Thread.sleep(30000);
    }

    @Test
    public void testMonoThen() {
        Mono<Void> fromRunnable = Mono.fromRunnable(() -> log.info("message from main"));
        fromRunnable.then(getMono1()).then(getMono2()).subscribe();

        //vs

        Mono<Void> fromRunnable2 = Mono.fromRunnable(() -> log.info("message from main2"));
        fromRunnable2.then(getMono3()).then(getMono4()).subscribe();

        
    }

    public Mono<Void> getMono1() {

        return Mono.fromRunnable(
                () -> log.info("Message from thread: {} and method mono1", Thread.currentThread().getId()));
    }

    public Mono<Void> getMono2() {

        return Mono.fromRunnable(
                () -> log.info("Message from thread: {} and method mono2", Thread.currentThread().getId()));
    }

    public Mono<Void> getMono3() {

        log.info("Message from thread: {} and method mono3", Thread.currentThread().getId());
        return Mono.empty();
    }

    public Mono<Void> getMono4() {
        log.info("Message from thread: {} and method mono4", Thread.currentThread().getId());
        return Mono.empty();
    }
}
