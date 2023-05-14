package com.eshoponcontainers.entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(name = "IntegrationEventLog")
@Data
public class IntegrationEventLogEntry implements Persistable<UUID> {

    @Id
    @Column(value = "EventId")
    private UUID eventId;

    @Column(value = "Content")
    private String content;

    @Column(value = "CreationTime")
    private LocalDateTime creationTime;

    @Column(value = "EventTypeName")
    private String eventTypeName;

    @Column(value = "State")
    private Integer state;

    @Column(value = "TimesSent")
    private Integer timesSent;

    @Column(value = "TransactionId")
    private String transactionId;

    @Transient
    private IntegrationEvent event;

    @Transient
    private boolean isNewEntry;

    public IntegrationEventLogEntry(IntegrationEvent event, UUID transId) {
        eventId = event.getId();
        creationTime = LocalDateTime.now(ZoneId.systemDefault());
        eventTypeName = event.getClass().getName();

        try {          
            content = getObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        state = EventStateEnum.NOT_PUBLISHED.getValue();
        timesSent = 0;
        transactionId = transId.toString();
        isNewEntry = true;
    }

    @PersistenceConstructor
    public IntegrationEventLogEntry(UUID eventId, String content, LocalDateTime creationTime, String eventTypeName, Integer state, Integer timesSent,
         String transactionId) {
        this.eventId = eventId;
        this.content = content;
        this.creationTime = creationTime;
        this.eventTypeName = eventTypeName;
        this.state = state;
        this.timesSent = timesSent;
        this.transactionId = transactionId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public EventStateEnum getState() {
        return EventStateEnum.valueOf(state);
    }

    public Integer getTimesSent() {
        return timesSent;
    }

    public void setTimesSent(Integer timesSent) {
        this.timesSent = timesSent;
    }

    public void setState(EventStateEnum state) {
        this.state = state.getValue();
    }

    public void deserializeEventContent() {
       
        JavaType javaType = getObjectMapper().getTypeFactory().constructFromCanonical(eventTypeName);
        try {
            this.event = getObjectMapper().readValue(this.content, javaType);
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Override
    @Nullable
    public UUID getId() {
        return this.eventId;
    }

    @Override
    public boolean isNew() {
        return isNewEntry;
    }

}
