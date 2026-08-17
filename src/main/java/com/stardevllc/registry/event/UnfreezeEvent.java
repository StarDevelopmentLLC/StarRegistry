package com.stardevllc.registry.event;

import com.stardevllc.registry.IRegistry;

public class UnfreezeEvent<V> extends RegistryEvent<V> {
    public UnfreezeEvent(IRegistry<V> registry) {
        super(registry);
    }
}
