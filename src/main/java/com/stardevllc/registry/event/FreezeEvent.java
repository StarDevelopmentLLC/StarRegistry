package com.stardevllc.registry.event;

import com.stardevllc.registry.IRegistry;

public class FreezeEvent<V> extends RegistryEvent<V> {
    public FreezeEvent(IRegistry<V> registry) {
        super(registry);
    }
}
