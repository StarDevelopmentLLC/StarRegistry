package com.stardevllc.registry.holder;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.starlib.objects.key.Key;

import java.util.function.Supplier;

public class DeferredHolder<T> extends RegistryHolder<T> {
    
    private boolean hasBeenRegistered;
    private final Supplier<T> supplier;
    
    public DeferredHolder(IRegistry<T> registry, Key key, Supplier<T> supplier) {
        super(registry, key);
        this.supplier = supplier;
    }
    
    public Supplier<T> getSupplier() {
        return supplier;
    }
    
    public boolean hasBeenRegistered() {
        return hasBeenRegistered;
    }
    
    public void markRegistered() {
        this.hasBeenRegistered = true;
    }
}
