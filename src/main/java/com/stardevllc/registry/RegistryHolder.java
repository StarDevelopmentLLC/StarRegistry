package com.stardevllc.registry;

import com.stardevllc.starlib.objects.Holder;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.tuple.pair.Pair;

/**
 * Represents a holder of an item in a Registry
 *
 * @param <T> The object type
 */
public class RegistryHolder<T> implements Holder<Key, T>, Pair<Key, T> {
    
    private final IRegistry<T> registry;
    private final Key key;
    
    public RegistryHolder(IRegistry<T> registry, Key key) {
        this.registry = registry;
        this.key = key;
    }
    
    public IRegistry<T> getRegistry() {
        return registry;
    }
    
    @Override
    public Key key() {
        return key;
    }
    
    @Override
    public T value() {
        return registry.get(key);
    }
    
    @Override
    public Key getLeft() {
        return key;
    }
    
    @Override
    public T getRight() {
        return value();
    }
    
    @Override
    public T setValue(T value) {
        return registry.set(key, value);
    }
}
