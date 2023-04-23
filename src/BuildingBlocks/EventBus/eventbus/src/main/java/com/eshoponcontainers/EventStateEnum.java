package com.eshoponcontainers;

public enum EventStateEnum {
    NOT_PUBLISHED(0),
    IN_PROGRESS(1),
    PUBLISHED(2),
    PUBLISH_FAILED(3);

    private int value;
    private EventStateEnum(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}