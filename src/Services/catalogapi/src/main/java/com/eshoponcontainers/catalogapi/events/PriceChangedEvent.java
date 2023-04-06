package com.eshoponcontainers.catalogapi.events;

import com.eshoponcontainers.eventbus.events.IntegrationEvent;

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
    
    private Integer productId;
    private double oldPrice;
    private double newPrice;
}
