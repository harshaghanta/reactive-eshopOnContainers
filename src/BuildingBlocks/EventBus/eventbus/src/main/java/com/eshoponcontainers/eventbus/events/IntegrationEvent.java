package com.eshoponcontainers.eventbus.events;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegrationEvent {
    // @JsonProperty("id")
    private UUID id;
    // @JsonProperty("creationDate")
    private LocalDateTime creationDate;

    public IntegrationEvent() {
        this(UUID.randomUUID(), LocalDateTime.now());        
    }

    public IntegrationEvent(UUID id, LocalDateTime creationTime) {
        this.id = id;
        this.creationDate = creationTime;
    }
}
