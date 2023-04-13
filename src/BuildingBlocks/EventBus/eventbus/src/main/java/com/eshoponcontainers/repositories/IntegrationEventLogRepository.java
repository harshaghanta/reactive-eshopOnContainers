package com.eshoponcontainers.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.eshoponcontainers.EventStateEnum;
import com.eshoponcontainers.entities.IntegrationEventLogEntry;

public interface IntegrationEventLogRepository extends ReactiveCrudRepository<IntegrationEventLogEntry, UUID>{
	List<IntegrationEventLogEntry>  findByTransactionIdAndState(String name, EventStateEnum eventStateNum);
}
