package com.eshoponcontainers.catalogapi.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.eshoponcontainers.catalogapi.entities.CatalogBrand;

public interface CatalogBrandRepository extends ReactiveCrudRepository<CatalogBrand, Integer>  {
        
}
