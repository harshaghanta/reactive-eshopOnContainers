package com.eshoponcontainers.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.eshoponcontainers.EventStateEnum;
import com.eshoponcontainers.entities.IntegrationEventLogEntry;

import reactor.core.publisher.Flux;

public interface IntegrationEventLogRepository extends ReactiveCrudRepository<IntegrationEventLogEntry, UUID>{
	Flux<IntegrationEventLogEntry>  findByTransactionIdAndState(String name, EventStateEnum eventStateNum);
}
