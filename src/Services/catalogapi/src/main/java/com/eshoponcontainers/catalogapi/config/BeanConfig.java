package com.eshoponcontainers.catalogapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BeanConfig {
    
    @Bean
    public Jackson2JsonDecoder decoder(ObjectMapper objectMapper) {
        return new Jackson2JsonDecoder(objectMapper);
    }
    
    @Bean
    public Jackson2JsonEncoder encoder(ObjectMapper objectMapper) {
        return new Jackson2JsonEncoder(objectMapper);
    }
}