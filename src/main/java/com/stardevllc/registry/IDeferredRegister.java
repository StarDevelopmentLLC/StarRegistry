package com.stardevllc.registry;

import com.stardevllc.registry.holder.DeferredHolder;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.registry.result.RegisterResult;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keys;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IDeferredRegister<V> {
    
    DeferredHolder<V> register(Key key, Supplier<V> supplier);
    
    DeferredHolder<V> register(Key key, Function<Key, V> supplierFunction);
    
    default RegistryHolder<V> register(String key, Supplier<V> supplier) {
        return register(createKey(key), supplier);
    }
    
    IRegistry<V> getRegistry();
    
    void registerEntries();
    
    Collection<DeferredHolder<V>> getEntries();
    
    RegisterResult<V> getResult(Key key);
    
    Collection<RegisterResult<V>> getResults();
    
    default Key createKey(String k) {
        return Keys.of(k);
    }
}
