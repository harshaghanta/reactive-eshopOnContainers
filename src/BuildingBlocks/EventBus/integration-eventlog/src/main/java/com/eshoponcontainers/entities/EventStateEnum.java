package com.eshoponcontainers.entities;

import java.util.HashMap;
import java.util.Map;

public enum EventStateEnum {
    NOT_PUBLISHED(0),
    IN_PROGRESS(1),
    PUBLISHED(2),
    PUBLISH_FAILED(3);

    private int value;
    private static Map map = new HashMap<>();
    
    private EventStateEnum(int value) {
        this.value = value;
    }

    static {
        for (EventStateEnum state : EventStateEnum.values()) {
            map.put(state.value, state);
        }
    }

    public static EventStateEnum valueOf(int state) {
        return (EventStateEnum) map.get(state);
    }

    public int getValue() {
        return value;
    }
}