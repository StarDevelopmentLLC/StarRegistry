package com.stardevllc.registry.event;

import com.stardevllc.starlib.event.EventDispatcher;
import com.stardevllc.starlib.registry.IRegistry;

import java.util.ArrayList;
import java.util.List;

public class RegistryDispatcher implements EventDispatcher {
    private final List<EventListener<?, RegistryEvent<?>>> listeners = new ArrayList<>();
    
    @Override
    public <E> E dispatch(E event) {
        this.listeners.forEach(l -> {
            try {
                l.onEvent((RegistryEvent<?>) event);
            } catch (ClassCastException ignored) {}
        });
        return event;
    }
    
    @Override
    public void addListener(Object listener) {
        if (listener instanceof IRegistry.Listener<?, ?> l) {
            listeners.add((EventListener<?, RegistryEvent<?>>) l);
        }
    }
}
