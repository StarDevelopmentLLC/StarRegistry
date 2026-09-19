package com.stardevllc.registry;

import com.stardevllc.registry.holder.DeferredHolder;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.registry.result.RegisterResult;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keys;
import com.stardevllc.starlib.registry.RegistryObject;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public interface IDeferredRegisterer<V> {
    
    DeferredHolder<V> register(Key key, Supplier<V> supplier);
    
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
