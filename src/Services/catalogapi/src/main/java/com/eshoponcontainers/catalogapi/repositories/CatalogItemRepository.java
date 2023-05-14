package com.eshoponcontainers.catalogapi.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import com.eshoponcontainers.catalogapi.entities.CatalogItem;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogItemRepository extends ReactiveSortingRepository<CatalogItem, Integer> {
        Flux<CatalogItem> findAllByOrderByIdAsc(PageRequest pageRequest);

        Flux<CatalogItem> findByNameStartsWith(String name, PageRequest pageRequest);

        Mono<Long> countByNameStartsWith(String name);

        Flux<CatalogItem> findByCatalogTypeIdAndCatalogBrandId(Integer catalogTypeId, Integer catalogBrandId,
                        PageRequest pageRequest);

        Mono<Long> countByCatalogTypeIdAndCatalogBrandId(Integer catalogTypeId, Integer catalogBrandId,
                        PageRequest pageRequest);

        Flux<CatalogItem> findByCatalogTypeId(Integer catalogTypeId, PageRequest pageRequest);

        Mono<Long> countByCatalogTypeId(Integer catalogTypeId, PageRequest pageRequest);

        Flux<CatalogItem> findByCatalogBrandId(Integer catalogBrandId, PageRequest pageRequest);

        Mono<Long> countByCatalogBrandId(Integer catalogBrandId);
}
