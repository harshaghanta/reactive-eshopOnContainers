package com.eshoponcontainers.catalogapi.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import com.eshoponcontainers.catalogapi.entities.CatalogItem;

import reactor.core.publisher.Flux;

public interface CatalogItemRepository extends ReactiveSortingRepository<CatalogItem, Integer> {
	Flux<CatalogItem> findAllByOrderByIdAsc(PageRequest pageRequest);
}
