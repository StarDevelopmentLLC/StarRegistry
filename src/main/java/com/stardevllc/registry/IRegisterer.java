package com.stardevllc.registry;

import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.starlib.objects.Nameable;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keyable;
import com.stardevllc.starlib.objects.key.impl.StringKey;

import java.util.Collection;

public interface IRegisterer<V> extends Nameable, Keyable {
    
    RegistryHolder<V> register(Key key, V object);
    
    default RegistryHolder<V> register(String key, V object) {
        return register(createKey(key, object), object);
    }
    
    IRegistry<V> getRegistry();
    
    Collection<RegistryHolder<V>> getEntries();
    
    default Key createKey(String k, V value) {
        return new StringKey(k);
    }
    
    default Key createKey(String k) {
        return new StringKey(k);
    }
}