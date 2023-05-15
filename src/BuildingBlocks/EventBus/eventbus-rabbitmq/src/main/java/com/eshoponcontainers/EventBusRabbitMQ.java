package com.eshoponcontainers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.stereotype.Component;

import com.eshoponcontainers.eventbus.SubscriptionInfo;
import com.eshoponcontainers.eventbus.abstractions.IntegrationEventHandler;
import com.eshoponcontainers.eventbus.events.IntegrationEvent;
import com.eshoponcontainers.eventbus.impl.InMemoryEventBusSubscriptionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbitmq.client.AMQP.BasicProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventBusRabbitMQ implements EventBus {

    private static final String EXCHANGE_TYPE_DIRECT = "direct";
    private static final String BROKER_NAME = "eshop_event_bus_exchange";
    private static final String QUEUE_NAME = "Catalog";

    private final Sender sender;
    private final InMemoryEventBusSubscriptionManager subscriptionManager;
    private final Receiver receiver;

    @Override
    public Mono<Void> publish(IntegrationEvent event) {

        ObjectMapper mapper = getObjectMapper();
        String eventName = event.getClass().getSimpleName();
        BasicProperties basicProperties = new BasicProperties().builder().deliveryMode(2).build();

        try {

            byte[] body = mapper.writeValueAsBytes(event);
            OutboundMessage message = new OutboundMessage(BROKER_NAME, eventName, basicProperties, body);

            return sender.declare(ExchangeSpecification.exchange(BROKER_NAME).type(EXCHANGE_TYPE_DIRECT))
                    .then(sender.send(Mono.just(message)));

        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }


    @Override
    public <T extends IntegrationEvent, TH extends IntegrationEventHandler<T>> Mono<Void> subscribe(Class<T> eventType,
            Class<TH> eventHandler) {

        subscriptionManager.addSubscription(eventType, eventHandler);
        log.info("Entered subscribe method");

        Mono<Void> mono = Mono.fromRunnable(() -> {
            receiver.consumeAutoAck(QUEUE_NAME).subscribe(message -> {

                String routingKey = message.getEnvelope().getRoutingKey();
                // Class eventType = subscriptionManager.getEventTypeByName(routingKey);
                byte[] messageBody = message.getBody();
                try {
                    Object event = getObjectMapper().readValue(messageBody, eventType);
                    log.info(event.toString());
                    List<SubscriptionInfo> subscriptions = subscriptionManager.getHandlersForEvent(eventType);
                    if (subscriptions != null && !subscriptions.isEmpty()) {
                        for (SubscriptionInfo subscription : subscriptions) {
                            Class handler = subscription.getHandler();
                            Method methodInfo = null;
                            try {
                                methodInfo = handler.getDeclaredMethod("handle", IntegrationEvent.class);
                            } catch (NoSuchMethodException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if(methodInfo != null)
                                try {
                                    methodInfo.invoke(handler.newInstance(), event);
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        return sender.declare(ExchangeSpecification.exchange(BROKER_NAME))
                .then(sender.declareQueue(QueueSpecification.queue(QUEUE_NAME)))
                .then(sender.bind(BindingSpecification.binding(BROKER_NAME, eventType.getSimpleName(), QUEUE_NAME)))
                .then(mono);

    }

}
