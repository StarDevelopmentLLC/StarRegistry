package com.stardevllc.registry.holder;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.starlib.objects.key.Key;

import java.util.function.Function;
import java.util.function.Supplier;

public class DeferredHolder<T> extends RegistryHolder<T> {
    
    private boolean hasBeenRegistered;
    private final Supplier<T> supplier;
    private final Function<Key, T> supplierFunction;
    
    public DeferredHolder(IRegistry<T> registry, Key key, Supplier<T> supplier) {
        super(registry, key);
        this.supplier = supplier;
        this.supplierFunction = null;
    }
    
    public DeferredHolder(IRegistry<T> registry, Key key, Function<Key, T> supplierFunction) {
        super(registry, key);
        this.supplier = null;
        this.supplierFunction = supplierFunction;
    }
    
    public T create() {
        if (this.supplierFunction != null) {
            return this.supplierFunction.apply(key());
        } else if (this.supplier != null) {
            return this.supplier.get();
        }
        
        return null;
    }
    
    public boolean hasBeenRegistered() {
        return hasBeenRegistered;
    }
    
    public void markRegistered() {
        this.hasBeenRegistered = true;
    }
}
