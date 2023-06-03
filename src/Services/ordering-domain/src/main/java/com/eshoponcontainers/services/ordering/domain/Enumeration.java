package com.eshoponcontainers.services.ordering.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.eshoponcontainers.services.ordering.domain.aggregates.orderaggregate.OrderStatus;

import lombok.Data;

@Data
public abstract class Enumeration {
    
    private String name;
    private int id;
    
    protected Enumeration(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {

        Enumeration.getAll(OrderStatus.class);
        return name;
    }

    public static <T extends Enumeration> Collection<T> getAll(Class<T> enumeration) {
        return Arrays.stream(enumeration.getClass().getFields()).map(field -> {
            try {
                return (T) field.get(null);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        
    }

    
}
