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

import com.eshoponcontainers.EventStateEnum;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;



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

    @Column(value =  "TimesSent")
    private Integer timesSent;

    @Column(value ="TransactionId")
    private String transactionId;

    @Transient
    private IntegrationEvent event;

//    //Added this as a constructor to avoid the error during save of entity. Seems hibernate needs a default constructor, but does't care if its public:
//    private IntegrationEventLogEntry() {
//
//    }

    public IntegrationEventLogEntry(IntegrationEvent event, UUID transId) {
        eventId = event.getId();
        creationTime = LocalDateTime.ofInstant(event.getCreationDate().toInstant(), ZoneId.systemDefault());
        eventTypeName = event.getClass().getName();
      
        try {
            content = new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {            
            e.printStackTrace();
        }
        state = EventStateEnum.NOT_PUBLISHED.getValue();
        timesSent = 0;
        transactionId = transId.toString();
        newIntegrationEventLogEntry = true;
        this.event = null;
        
    }
    

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public Integer getState() {
        return state;
    }

    public Integer getTimesSent() {
        return timesSent;
    }

    public void setTimesSent(Integer timesSent) {
        this.timesSent = timesSent;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public void deserializeEventContent() {
        this.event = new ObjectMapper().convertValue(this.content, IntegrationEvent.class);
    }
    
    @Transient
    private boolean newIntegrationEventLogEntry;

    @Override
    @Transient
    public boolean isNew() {
        return newIntegrationEventLogEntry || eventId == null;
    }

	@Override
	public UUID getId() {
		return eventId;
	}

	@PersistenceConstructor
	public IntegrationEventLogEntry(UUID eventId, String content, LocalDateTime creationTime, String eventTypeName,
			Integer state, Integer timesSent, String transactionId) {
		this.eventId = eventId;
		this.content = content;
		this.creationTime = creationTime;
		this.eventTypeName = eventTypeName;
		this.state = state;
		this.timesSent = timesSent;
		this.transactionId = transactionId;
		this.event = null;
		this.newIntegrationEventLogEntry = false;
	}
    
}