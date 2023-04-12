package com.eshoponcontainers.catalogapi.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.eshoponcontainers.catalogapi.entities.CatalogItem;
import com.eshoponcontainers.catalogapi.repositories.CatalogItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogItemController {
    
    private final CatalogItemRepository catalogItemRepository;

    @GetMapping("/items")
    public Flux<CatalogItem> getCatalogItems(
        @RequestParam( name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam (required = false, name = "ids" ) String ids) 
    {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        // PageRequest withSort = pageRequest.withSort(Sort.by("name").ascending());
        Flux<CatalogItem> catalogItemsFlux = catalogItemRepository.findAllByOrderByIdAsc(pageRequest);
        return catalogItemsFlux;
    }

    @GetMapping("/items/{id}")
    public Mono<CatalogItem> getItemById(@PathVariable Integer id) {
        return catalogItemRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog item with id:" + id + " not found")));

    }

}
