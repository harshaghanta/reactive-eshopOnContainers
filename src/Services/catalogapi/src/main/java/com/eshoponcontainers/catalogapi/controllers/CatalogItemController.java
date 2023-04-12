package com.eshoponcontainers.catalogapi.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.eshoponcontainers.catalogapi.entities.CatalogBrand;
import com.eshoponcontainers.catalogapi.entities.CatalogItem;
import com.eshoponcontainers.catalogapi.entities.CatalogType;
import com.eshoponcontainers.catalogapi.repositories.CatalogBrandRepository;
import com.eshoponcontainers.catalogapi.repositories.CatalogItemRepository;
import com.eshoponcontainers.catalogapi.repositories.CatalogTypeRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogItemController {

	private final CatalogItemRepository catalogItemRepository;
	private final CatalogTypeRepository catalogTypeRepository;
	private final CatalogBrandRepository catalogBrandRepository;

	@GetMapping("/items")
	public Flux<CatalogItem> getCatalogItems(
			@RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false, name = "ids") String ids) {
		PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
		// PageRequest withSort = pageRequest.withSort(Sort.by("name").ascending());
		Flux<CatalogItem> catalogItemsFlux = catalogItemRepository.findAllByOrderByIdAsc(pageRequest);
		return catalogItemsFlux;
	}

	@GetMapping("/items/{id}")
	public Mono<ResponseEntity<CatalogItem>> getItemById(@PathVariable Integer id) {
		return catalogItemRepository.findById(id).map(catalog -> ResponseEntity.ok().body(catalog)).switchIfEmpty(Mono
				.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog item with id:" + id + " not found")));

	}

	@GetMapping("/catalogTypes")
	@ResponseStatus(HttpStatus.OK)
	public Flux<CatalogType> getAllCatalogTypes() {
		return catalogTypeRepository.findAll();
	}

	@GetMapping("/catalogBrands")
	@ResponseStatus(HttpStatus.OK)
	public Flux<CatalogBrand> getAllCatalogBrands() {
		return catalogBrandRepository.findAll();
	}

}
