package com.eshoponcontainers.catalogapi;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import com.eshoponcontainers.EventBus;
import com.eshoponcontainers.catalogapi.eventhandlers.PriceChangedEventHandler;
import com.eshoponcontainers.catalogapi.events.PriceChangedEvent;

@SpringBootApplication(scanBasePackages = {"com.eshoponcontainers"})
@EnableR2dbcRepositories(basePackages="com.eshoponcontainers")
public class CatalogapiApplication {

	@Autowired
	private EventBus eventBus;

	public static void main(String[] args) {
		SpringApplication.run(CatalogapiApplication.class, args);
	}

	@PostConstruct
    public void configureEventBus() {
        eventBus.subscribe(PriceChangedEvent.class, PriceChangedEventHandler.class).subscribe();    
        
    }

}
