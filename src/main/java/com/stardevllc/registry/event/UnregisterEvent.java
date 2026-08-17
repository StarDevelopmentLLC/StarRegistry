package com.stardevllc.registry.event;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.starlib.objects.key.Key;

public class UnregisterEvent<V> extends RegistryEvent<V> {
    
    private final Key key;
    private final V object;
    
    public UnregisterEvent(IRegistry<V> registry, Key key, V object) {
        super(registry);
        this.key = key;
        this.object = object;
    }
    
    public Key getKey() {
        return key;
    }
    
    public V getObject() {
        return object;
    }
}
