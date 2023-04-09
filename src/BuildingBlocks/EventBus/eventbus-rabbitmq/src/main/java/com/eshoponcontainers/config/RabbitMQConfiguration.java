package com.eshoponcontainers.config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
@Slf4j
public class RabbitMQConfiguration {
    
    @Value("${rabbitmqconfig.host}")
    private String host;

    @Value("${rabbitmqconfig.port:15672}")
    private Integer port;

    private String connectionName = "eventbus";

    @Bean
    public Mono<Connection> getConnection() throws IOException, TimeoutException {
        log.debug("Trying to connect with host: {}, port: {}", host, port );
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        // connectionFactory.setPort(port);
        return Mono.fromCallable(() -> connectionFactory.newConnection(connectionName)).cache();
    }

    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono) {
        return new SenderOptions().connectionMono(connectionMono)
            .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }

    @Bean
    public ReceiverOptions receiverOptions(Mono<Connection> connectionMono) {
        return new ReceiverOptions().connectionMono(connectionMono);
    }

    @Bean
    public Receiver receiver(ReceiverOptions receiverOptions) {
        return new Receiver(receiverOptions);
    }
}
