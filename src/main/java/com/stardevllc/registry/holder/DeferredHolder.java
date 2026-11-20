package com.stardevllc.registry.holder;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.starlib.objects.key.Key;

import java.util.function.Function;
import java.util.function.Supplier;

public class DeferredHolder<T> extends RegistryHolder<T> {
    
    private boolean hasBeenRegistered;
    private final Supplier<T> supplier;
    private final Function<Key, T> supplierFunction;
    
    private T object;
    
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
        if (this.object == null) {
            if (!hasBeenRegistered) {
                if (this.supplierFunction != null) {
                    return this.object = this.supplierFunction.apply(key());
                } else if (this.supplier != null) {
                    return this.object = this.supplier.get();
                }
            } else {
                return super.value();
            }
        }
        
        return this.object;
    }
    
    public boolean hasBeenRegistered() {
        return hasBeenRegistered;
    }
    
    public void markRegistered() {
        this.hasBeenRegistered = true;
        if (this.object != null) {
            this.object = null;
        }
    }
    
    @Override
    public String toString() {
        return "DeferredHolder{" +
                "registry=" + registry +
                ", key=" + key +
                ", hasBeenRegistered=" + hasBeenRegistered +
                ", supplier=" + supplier +
                ", supplierFunction=" + supplierFunction +
                ", object=" + object +
                '}';
    }
}
