package com.eshoponcontainers.catalogapi.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.eshoponcontainers.catalogapi.entities.CatalogType;

public interface CatalogTypeRepository extends ReactiveCrudRepository<CatalogType, Integer> {

}
