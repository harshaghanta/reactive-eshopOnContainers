package com.eshoponcontainers.catalogapi.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriBuilder;

import com.eshoponcontainers.catalogapi.entities.CatalogBrand;
import com.eshoponcontainers.catalogapi.entities.CatalogItem;
import com.eshoponcontainers.catalogapi.entities.CatalogType;
import com.eshoponcontainers.catalogapi.repositories.CatalogBrandRepository;
import com.eshoponcontainers.catalogapi.repositories.CatalogItemRepository;
import com.eshoponcontainers.catalogapi.repositories.CatalogTypeRepository;
import com.eshoponcontainers.catalogapi.services.CatalogItemService;
import com.eshoponcontainers.catalogapi.viewmodels.DeletionStatus;
import com.eshoponcontainers.catalogapi.viewmodels.PaginatedItemViewModel;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
	private final CatalogItemService catalogItemService;

	@GetMapping("/items")
	public Mono<ResponseEntity<?>> getCatalogItems2(
			@RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false, name = "ids") String ids) {
		if (ids == null) {
			Mono<PaginatedItemViewModel<CatalogItem>> paginatedItemsModel = catalogItemService
					.getPaginatedItems(pageIndex, pageSize);
			return paginatedItemsModel.flatMap(model -> Mono.just(ResponseEntity.ok(model)));
		} else {
			List<Integer> idsList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
			Mono<List<CatalogItem>> catalogItemsListMono = catalogItemService.findAllById(idsList);
			return catalogItemsListMono.flatMap(catItems -> Mono.just(ResponseEntity.ok(catItems)));
		}
	}

	@GetMapping("/items/{id}")
	public Mono<ResponseEntity<CatalogItem>> getItemById(@PathVariable Integer id) {
		return catalogItemRepository.findById(id).map(catalog -> ResponseEntity.ok().body(catalog)).switchIfEmpty(Mono
				.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog item with id:" + id + " not found")));

	}

	@GetMapping("/items/withname/{name}")
	public Mono<ResponseEntity<PaginatedItemViewModel<CatalogItem>>> itemsWithName(@PathVariable String name,
			@RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {

		return catalogItemService.getCatalogItemsByName(name, pageIndex, pageSize).flatMap(model -> {
			return Mono.just(ResponseEntity.ok(model));
		});
	}

	@GetMapping(value = {
			"/items/type/{catalogTypeId}/brand/{catalogBrandId}",
			"/items/type/{catalogTypeId}"
	})
	public Mono<ResponseEntity<PaginatedItemViewModel<CatalogItem>>> itemsByTypeIdAndBrandId(
			@PathVariable(name = "catalogTypeId") Integer catalogTypeId,
			@PathVariable(name = "catalogBrandId", required = false) Integer catalogBrandId,
			@RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {

		Mono<PaginatedItemViewModel<CatalogItem>> paginatedViewModelMono = null;
		if (catalogBrandId != null) {
			paginatedViewModelMono = catalogItemService.findByCatalogTypeIdAndCatalogBrandId(catalogTypeId,
					catalogBrandId, pageIndex, pageSize);
		} else {
			paginatedViewModelMono = catalogItemService.findByCatalogTypeId(catalogTypeId, pageIndex, pageSize);
		}

		return paginatedViewModelMono.flatMap(model -> Mono.just(ResponseEntity.ok(model)));
	}

	@GetMapping(value = {
			"/items/type/all/brand/{catalogBrandId}",
			"/items/type/all/brand"
	})
	public Mono<ResponseEntity<PaginatedItemViewModel<CatalogItem>>> itemsByBrandId(
			@PathVariable(name = "catalogBrandId", required = false) Integer catalogBrandId,
			@RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {

		Mono<PaginatedItemViewModel<CatalogItem>> paginatedViewModelMono = catalogItemService
				.findByCatalogBrandId(catalogBrandId, pageIndex, pageSize);
		return paginatedViewModelMono.flatMap(model -> Mono.just(ResponseEntity.ok(model)));
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

	@PostMapping("/items")
	public Mono<ResponseEntity<Void>> createProduct(@RequestBody CatalogItem product) {
		return catalogItemService.saveProduct(product).then(Mono.fromSupplier(() -> {

			URI uri = null;
			try {
				uri = new URI("");
			} catch (URISyntaxException e) {
			}
			return ResponseEntity.created(uri).build();
		}));
	}

	@PutMapping("/items")
	public Mono<ResponseEntity<Void>> updateProduct(@RequestBody CatalogItem updatedItem) {
		return catalogItemService.updateProductIfExists(updatedItem).flatMap(exists -> {
			if (!exists)
				return Mono.just(ResponseEntity.notFound().build());
			else {
				return Mono.fromSupplier(() -> {
					URI uri = null;
					try {
						uri = new URI("");
					} catch (URISyntaxException e) {
					}
					return ResponseEntity.created(uri).build();
				});
			}
		});

	}

	@DeleteMapping("/items/{id}")
	public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Integer id) {

		return catalogItemService.deleteProduct(id).flatMap(deletionstatus -> {
			if (deletionstatus == DeletionStatus.NOT_FOUND)
				return Mono.just(ResponseEntity.notFound().build());
			else
				return Mono.just(ResponseEntity.noContent().build());
		});
	}

}
