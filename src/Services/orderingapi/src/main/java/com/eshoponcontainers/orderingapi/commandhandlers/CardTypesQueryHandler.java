package com.eshoponcontainers.orderingapi.commandhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import com.eshoponcontainers.orderingapi.dto.CardType;
import com.eshoponcontainers.orderingapi.queries.GetAllCardTypesQuery;

import an.awesome.pipelinr.Command;
import reactor.core.publisher.Flux;

@Component
public class CardTypesQueryHandler implements Command.Handler<GetAllCardTypesQuery, Flux<CardType>> {

    @Autowired
    private DatabaseClient databaseClient;

    @Override
    public Flux<CardType> handle(GetAllCardTypesQuery query) {
        return databaseClient.sql("SELECT * FROM cardtypes").map(row -> 
            new CardType(row.get("id", Integer.class), row.get("Name", String.class))
        ).all();
    }

}
