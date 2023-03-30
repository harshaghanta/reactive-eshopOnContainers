package com.eshoponcontainers.eventbus.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.eshoponcontainers.eventbus.events.TestIntegrationEvent;
import com.eshoponcontainers.eventbus.events.TestIntegrationEventHandler;

public class InMemoryEventBusSubscriptionManagerTest {

    @Test
    public void after_creation_should_be_empty() {
        InMemoryEventBusSubscriptionManager manager = new InMemoryEventBusSubscriptionManager();
        assertTrue(manager.isEmpty());
    }

    @Test
    public void after_one_event_subscription_should_contain_the_event() {
        InMemoryEventBusSubscriptionManager manager = new InMemoryEventBusSubscriptionManager();
        manager.addSubscription(TestIntegrationEvent.class, TestIntegrationEventHandler.class);
        assertTrue(manager.hasSubscriptionsForEvent(TestIntegrationEvent.class));
    }

    @Test
    public void after_all_subscriptions_are_deleted_event_should_no_longer_exist() {
        InMemoryEventBusSubscriptionManager manager = new InMemoryEventBusSubscriptionManager();
        manager.addSubscription(TestIntegrationEvent.class, TestIntegrationEventHandler.class);
        manager.removeSubscription(TestIntegrationEvent.class, TestIntegrationEventHandler.class);
        assertFalse(manager.hasSubscriptionsForEvent(TestIntegrationEvent.class));
    }

    @Test
    public void deleting_last_subscription_should_raise_on_deleted_event() {

    }

    @Test
    public void get_handlers_for_event_should_return_all_handlers() {

    }
}
