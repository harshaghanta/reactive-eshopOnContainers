package com.eshoponcontainers.catalogapi.repositories;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import com.eshoponcontainers.catalogapi.entities.CatalogItem;

public interface CatalogItemRepository extends ReactiveSortingRepository<CatalogItem, Integer> {
    
}
