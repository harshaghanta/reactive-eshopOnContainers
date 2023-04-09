package com.eshoponcontainers.catalogapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshoponcontainers.catalogapi.entities.CatalogBrand;
import com.eshoponcontainers.catalogapi.repositories.CatalogBrandRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogBrandController {
    
    private final CatalogBrandRepository catalogBrandRepository;

    @GetMapping("/CatalogBrands")
    public Flux<CatalogBrand> getCatalogBrands() {
        return catalogBrandRepository.findAll();
    }

}
