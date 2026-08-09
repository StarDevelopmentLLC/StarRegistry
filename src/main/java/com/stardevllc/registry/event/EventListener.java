package com.stardevllc.registry.event;

@FunctionalInterface
public interface EventListener<V, E extends RegistryEvent<V>> {
    void onEvent(E event);
}
