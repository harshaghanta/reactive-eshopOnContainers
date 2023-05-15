package com.eshoponcontainers.catalogapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eshoponcontainers.EventBus;
import com.eshoponcontainers.catalogapi.entities.CatalogItem;
import com.eshoponcontainers.catalogapi.entities.NullCatalogItem;
import com.eshoponcontainers.catalogapi.events.PriceChangedEvent;
import com.eshoponcontainers.catalogapi.repositories.CatalogItemRepository;
import com.eshoponcontainers.catalogapi.viewmodels.DeletionStatus;
import com.eshoponcontainers.catalogapi.viewmodels.PaginatedItemViewModel;
import com.eshoponcontainers.services.IntegrationEventLogService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CatalogItemService {

    private final CatalogItemRepository catalogItemRepository;
    private final IntegrationEventLogService eventLogService;
    private final EventBus eventBus;

    @Value("${picBaseUrl}")
    private String picBaseUrl;

    public Mono<PaginatedItemViewModel<CatalogItem>> getPaginatedItems(Integer pageIndex, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        // PageRequest withSort = pageRequest.withSort(Sort.by("name").ascending());
        Mono<List<CatalogItem>> catalogItemsMono = catalogItemRepository.findAllByOrderByIdAsc(pageRequest)
                .collectList();
        Mono<Long> itemsCountMono = catalogItemRepository.count();
        return Mono.zip(catalogItemsMono, itemsCountMono).flatMap(item -> {
            item.getT1().forEach(catalogitem -> {
                catalogitem.fillProductUrl(picBaseUrl);
            });
            return Mono.just(new PaginatedItemViewModel<CatalogItem>(pageIndex, pageSize, item.getT2(), item.getT1()));
        });
    }

    public Mono<List<CatalogItem>> findAllById(Iterable<Integer> ids) {
        return catalogItemRepository.findAllById(ids).collectList().flatMap(catItems -> {
            catItems.forEach(catitem -> catitem.fillProductUrl(picBaseUrl));
            return Mono.just(catItems);
        });
    }

    public Mono<PaginatedItemViewModel<CatalogItem>> getCatalogItemsByName(String name,
            Integer pageIndex, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);

        Mono<List<CatalogItem>> catalogItemsMono = catalogItemRepository.findByNameStartsWith(name, pageRequest)
                .collectList();

        Mono<Long> matchingItemsCountMono = catalogItemRepository.countByNameStartsWith(name);

        return Mono.zip(catalogItemsMono, matchingItemsCountMono).flatMap(item -> {
            List<CatalogItem> catItems = item.getT1();
            Long count = item.getT2();
            catItems.forEach(catitem -> catitem.fillProductUrl(picBaseUrl));
            PaginatedItemViewModel<CatalogItem> model = new PaginatedItemViewModel<CatalogItem>(pageIndex, pageSize,
                    count, catItems);
            return Mono.just(model);
        });
    }

    public Mono<PaginatedItemViewModel<CatalogItem>> findByCatalogTypeIdAndCatalogBrandId(Integer catalogTypeId,
            Integer catalogBrandId, Integer pageIndex, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);

        Flux<CatalogItem> catalogItemsFlux = catalogItemRepository.findByCatalogTypeIdAndCatalogBrandId(catalogTypeId,
                catalogBrandId, pageRequest);
        Mono<Long> countMono = catalogItemRepository.countByCatalogTypeIdAndCatalogBrandId(catalogTypeId,
                catalogBrandId, pageRequest);

        Mono<List<CatalogItem>> catalogItemsMono = catalogItemsFlux.collectList();
        return Mono.zip(catalogItemsMono, countMono).flatMap(item -> {
            List<CatalogItem> catItems = item.getT1();
            Long count = item.getT2();
            catItems.forEach(catitem -> catitem.fillProductUrl(picBaseUrl));
            PaginatedItemViewModel<CatalogItem> model = new PaginatedItemViewModel<CatalogItem>(pageIndex, pageSize,
                    count, catItems);
            return Mono.just(model);
        });
    }

    public Mono<PaginatedItemViewModel<CatalogItem>> findByCatalogTypeId(Integer catalogTypeId, Integer pageIndex,
            Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);

        Flux<CatalogItem> catalogItemsFlux = catalogItemRepository.findByCatalogTypeId(catalogTypeId, pageRequest);
        Mono<Long> countMono = catalogItemRepository.countByCatalogTypeId(catalogTypeId, pageRequest);

        Mono<List<CatalogItem>> catalogItemsMono = catalogItemsFlux.collectList();
        return Mono.zip(catalogItemsMono, countMono).flatMap(item -> {
            List<CatalogItem> catItems = item.getT1();
            Long count = item.getT2();
            catItems.forEach(catitem -> catitem.fillProductUrl(picBaseUrl));
            PaginatedItemViewModel<CatalogItem> model = new PaginatedItemViewModel<CatalogItem>(pageIndex, pageSize,
                    count, catItems);
            return Mono.just(model);
        });
    }

    public Mono<PaginatedItemViewModel<CatalogItem>> findByCatalogBrandId(Integer catalogBrandId, Integer pageIndex,
            Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Mono<List<CatalogItem>> catalogItemsMono = null;
        Mono<Long> countMono = null;

        if (catalogBrandId != null) {
            catalogItemsMono = catalogItemRepository.findByCatalogBrandId(catalogBrandId, pageRequest).collectList();
            countMono = catalogItemRepository.countByCatalogBrandId(catalogBrandId);
        } else {
            catalogItemsMono = catalogItemRepository.findAllByOrderByIdAsc(pageRequest).collectList();
            countMono = catalogItemRepository.count();
        }

        return Mono.zip(catalogItemsMono, countMono).flatMap(item -> {
            List<CatalogItem> catItems = item.getT1();
            catItems.forEach(catitem -> catitem.fillProductUrl(picBaseUrl));
            return Mono.just(new PaginatedItemViewModel<>(pageIndex, pageSize, item.getT2(), catItems));
        });
    }

    public Mono<Boolean> updateProductIfExists(CatalogItem updatedProduct) {

        return catalogItemRepository.findById(updatedProduct.getId())
            .defaultIfEmpty(CatalogItem.NULL)
            // .switchIfEmpty(Mono.just((CatalogItem)null))
            .flatMap(savedProduct -> {
                if (savedProduct == CatalogItem.NULL)
                    return Mono.just(false);
                else {
                    return updateProductAndRaiseEvents(savedProduct, updatedProduct).thenReturn(true);
                }
        });
    }

    private Mono<Void> updateProductAndRaiseEvents(CatalogItem savedProduct, CatalogItem updatedProduct) {

        Boolean raisePriceChangedEvent = !(savedProduct.getPrice().equals(updatedProduct.getPrice()));

        if (raisePriceChangedEvent) {
            PriceChangedEvent event = new PriceChangedEvent(savedProduct.getId(), savedProduct.getPrice(),
                    updatedProduct.getPrice());
            return saveEventAndCatalogChanges(updatedProduct, event).then(publishThroughEventBus(event));

        } else
            return catalogItemRepository.save(updatedProduct).then();
    }

    private Mono<Void> saveEventAndCatalogChanges(CatalogItem updatedProduct, PriceChangedEvent event) {
        UUID transactionId = UUID.randomUUID();        
        // return catalogItemRepository.save(updatedProduct).then(Mono.fromSupplier(() ->  eventLogService.saveEvent(event, transactionId)).then());
        return catalogItemRepository.save(updatedProduct).flatMap(catitem -> eventLogService.saveEvent(event, transactionId).flatMap(entry-> Mono.empty()));
    }

    private Mono<Void> publishThroughEventBus(PriceChangedEvent event) {
        return eventLogService.markEventAsInProgress(event.getId())
        .then(eventBus.publish(event))
        .then(eventLogService.markEventAsPublished(event.getId()));
    }

    public Mono<CatalogItem> saveProduct(CatalogItem product) {

        return catalogItemRepository.save(product);
    }

    public Mono<DeletionStatus> deleteProduct(Integer productId) {

        return catalogItemRepository.existsById(productId).flatMap(exists -> {
            if (!exists)
                return Mono.just(DeletionStatus.NOT_FOUND);
            else {
                return catalogItemRepository.deleteById(productId)
                        .then(Mono.fromSupplier(() -> DeletionStatus.SUCCESS));
            }
        });
    }
}
