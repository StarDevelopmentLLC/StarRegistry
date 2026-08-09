package com.stardevllc.registry.event;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.starlib.objects.key.Key;

public class RegisterEvent<V> extends RegistryEvent<V> {
    
    private final Key key;
    private final V object, existing;
    
    public RegisterEvent(IRegistry<V> registry, Key key, V object, V existing) {
        super(registry);
        this.key = key;
        this.object = object;
        this.existing = existing;
    }
    
    public Key getKey() {
        return key;
    }
    
    public V getObject() {
        return object;
    }
    
    public V getExisting() {
        return existing;
    }
}
