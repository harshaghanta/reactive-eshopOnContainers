package com.eshoponcontainers.entities;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.eshoponcontainers.EventStateEnum;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;



@Table(name = "IntegrationEventLog")
@Data
public class IntegrationEventLogEntry {
    
    @Id
    @Column(value = "EventId")
    private UUID eventId;

    @Column(value = "Content")
    private String content;

    @Column(value = "CreationTime")
    private Date creationTime;

    @Column(value = "EventTypeName")
    private String eventTypeName;

    @Column(value = "State")
    private EventStateEnum state;

    @Column(value =  "TimesSent")
    private Integer timesSent;

    @Column(value ="TransactionId")
    private String transactionId;

    @Transient
    private IntegrationEvent event;

    //Added this as a constructor to avoid the error during save of entity. Seems hibernate needs a default constructor, but does't care if its public:
    private IntegrationEventLogEntry() {

    }

    public IntegrationEventLogEntry(IntegrationEvent event, UUID transId) {
        eventId = event.getId();
        creationTime = event.getCreationDate();
        eventTypeName = event.getClass().getName();
      
        try {
            content = new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {            
            e.printStackTrace();
        }
        state = EventStateEnum.NOT_PUBLISHED;
        timesSent = 0;
        transactionId = transId.toString();
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public EventStateEnum getState() {
        return state;
    }

    public Integer getTimesSent() {
        return timesSent;
    }

    public void setTimesSent(Integer timesSent) {
        this.timesSent = timesSent;
    }

    public void setState(EventStateEnum state) {
        this.state = state;
    }

    public void deserializeEventContent() {
        this.event = new ObjectMapper().convertValue(this.content, IntegrationEvent.class);
    }
    
}