package com.eshoponcontainers.catalogapi.events;

import java.time.LocalDateTime;
import java.util.UUID;

import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class PriceChangedEvent extends IntegrationEvent {
    
    public PriceChangedEvent(Integer productId, Double oldPrice, Double newPrice) {
        super();
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    @JsonCreator
    public PriceChangedEvent(Integer productId, Double oldPrice, Double newPrice, UUID id, LocalDateTime creationTime) {
        super(id, creationTime);
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }    

    // @JsonProperty("productId")
    private Integer productId;

    // @JsonProperty("oldPrice")
    private double oldPrice;

    // @JsonProperty("newPrice")
    private double newPrice;
}
