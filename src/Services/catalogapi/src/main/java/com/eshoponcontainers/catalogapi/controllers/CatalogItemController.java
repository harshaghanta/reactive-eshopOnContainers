package com.eshoponcontainers.catalogapi.controllers;

import java.net.URI;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.eshoponcontainers.catalogapi.entities.CatalogBrand;
import com.eshoponcontainers.catalogapi.entities.CatalogItem;
import com.eshoponcontainers.catalogapi.entities.CatalogType;
import com.eshoponcontainers.catalogapi.events.ProductPriceChangedIntegrationEvent;
import com.eshoponcontainers.catalogapi.repositories.CatalogBrandRepository;
import com.eshoponcontainers.catalogapi.repositories.CatalogItemRepository;
import com.eshoponcontainers.catalogapi.repositories.CatalogTypeRepository;
import com.eshoponcontainers.catalogapi.services.CatalogIntegrationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/catalog")
@AllArgsConstructor
@Slf4j
public class CatalogItemController {

	private final CatalogItemRepository catalogItemRepository;
	private final CatalogTypeRepository catalogTypeRepository;
	private final CatalogBrandRepository catalogBrandRepository;
	private final CatalogIntegrationService catalogIntegrationService;

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

	@PutMapping("/items")
	public Mono<ResponseEntity<?>> updateProduct(@RequestBody CatalogItem requestedItem) {
		return catalogItemRepository.findById(requestedItem.getId())
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Catalog item with id:" + requestedItem.getId() + " not found")))
				.map(catalogItem -> {
					Double oldPrice = catalogItem.getPrice();
					Double newPrice = requestedItem.getPrice();
					boolean raisePriceChangedEvent = oldPrice != newPrice;
					if (raisePriceChangedEvent) {
						System.out.println("In UpdateProduct raisePriceChangedEvent");
						ProductPriceChangedIntegrationEvent productPriceChangedIntegrationEvent = new ProductPriceChangedIntegrationEvent(
								requestedItem.getId(), oldPrice, newPrice);
						catalogIntegrationService
								.saveEventAndCatalogChanges(productPriceChangedIntegrationEvent, requestedItem)
								.then(catalogIntegrationService
										.publishThroughEventBus(productPriceChangedIntegrationEvent));
					} else {
						catalogItemRepository.save(requestedItem);
					}
					URI location = URI.create("/items/" + requestedItem.getId());
					return ResponseEntity.created(location).build();
				});
	}

}
