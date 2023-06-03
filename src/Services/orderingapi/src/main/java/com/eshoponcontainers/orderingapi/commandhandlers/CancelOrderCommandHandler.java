package com.eshoponcontainers.orderingapi.commandhandlers;

import org.springframework.stereotype.Component;

import com.eshoponcontainers.orderingapi.commands.CancelOrderCommand;

import an.awesome.pipelinr.Command;

@Component

public class CancelOrderCommandHandler implements Command.Handler<CancelOrderCommand, Boolean> {

    @Override
    public Boolean handle(CancelOrderCommand command) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
    
}
