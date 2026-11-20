package com.stardevllc.registry;

import com.stardevllc.registry.holder.DeferredHolder;
import com.stardevllc.registry.result.RegisterResult;
import com.stardevllc.starlib.objects.key.Key;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IDeferredRegister<V> {
    
    static <V> IDeferredRegister<V> create(IRegistry<V> registry, Key key, String name) {
        try {
            return registry.createDeferredRegister(key, name);
        } catch (Exception e) {
            return new SimpleDeferredRegister<>(registry, key, name);
        }
    }
    
    static <V> IDeferredRegister<V> create(IRegistry<V> registry, String key, String name) {
        try {
            return registry.createDeferredRegister(key, name);
        } catch (Exception e) {
            return new SimpleDeferredRegister<>(registry, registry.createKey(key), name);
        }
    }
    
    static <V> IDeferredRegister<V> create(IRegistry<V> registry, String name) {
        try {
            return registry.createDeferredRegister(name);
        } catch (Exception e) {
            return new SimpleDeferredRegister<>(registry, registry.createKey(name), name);
        }
    }
    
    static <V> IDeferredRegister<V> create(IRegistry<V> registry) {
        try {
            return registry.createDeferredRegister();
        } catch (Exception e) {
            return new SimpleDeferredRegister<>(registry, Key.EMPTY, "");
        }
    }
    
    DeferredHolder<V> register(Key key, Supplier<V> supplier);
    
    DeferredHolder<V> register(Key key, Function<Key, V> supplierFunction);
    
    default DeferredHolder<V> register(Key key, V value) {
        return register(key, () -> value);
    }
    
    default DeferredHolder<V> register(String key, V value) {
        return register(createKey(key), () -> value);
    }
    
    default DeferredHolder<V> register(String key, Supplier<V> supplier) {
        return register(createKey(key), supplier);
    }
    
    default DeferredHolder<V> register(String key, Function<Key, V> supplierFunction) {
        return register(createKey(key), supplierFunction);
    }
    
    IRegistry<V> getRegistry();
    
    void registerEntries();
    
    Collection<DeferredHolder<V>> getEntries();
    
    RegisterResult<V> getResult(Key key);
    
    Collection<RegisterResult<V>> getResults();
    
    default Key createKey(String k) {
        if (getRegistry() != null) {
            return getRegistry().createKey(k);
        }
        
        return new StringKey(IRegistry.KEY_NORMALIZER.apply(k));
    }
}
