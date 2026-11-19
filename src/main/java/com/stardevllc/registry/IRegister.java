package com.stardevllc.registry;

import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.starlib.objects.Nameable;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keyable;

import java.util.Collection;
import java.util.function.Function;

public interface IRegister<V> extends Nameable, Keyable {
    
    static <V> IRegister<V> create(IRegistry<V> registry, Key key, String name) {
        try {
            return registry.createRegister(key, name);
        } catch (Exception e) {
            return new SimpleRegister<>(registry, key, name);
        }
    }
    
    static <V> IRegister<V> create(IRegistry<V> registry, String key, String name) {
        try {
            return registry.createRegister(key, name);
        } catch (Exception e) {
            return new SimpleRegister<>(registry, registry.createKey(key), name);
        }
    }
    
    static <V> IRegister<V> create(IRegistry<V> registry, String name) {
        try {
            return registry.createRegister(name);
        } catch (Exception e) {
            return new SimpleRegister<>(registry, registry.createKey(name), name);
        }
    }
    
    static <V> IRegister<V> create(IRegistry<V> registry) {
        try {
            return registry.createRegister();
        } catch (Exception e) {
            return new SimpleRegister<>(registry, Key.EMPTY, "");
        }
    }
    
    RegistryHolder<V> register(Key key, Function<Key, V> function);
    
    default RegistryHolder<V> register(Key key, V object) {
        return register(key, k -> object);
    }
    
    default RegistryHolder<V> register(String key, V object) {
        return register(createKey(key, object), object);
    }
    
    default RegistryHolder<V> register(String key, Function<Key, V> function) {
        Key k = createKey(key);
        return register(k, function);
    }
    
    IRegistry<V> getRegistry();
    
    Collection<RegistryHolder<V>> getEntries();
    
    default Key createKey(String k, V value) {
        return getRegistry().createKey(k, value);
    }
    
    default Key createKey(String k) {
        return getRegistry().createKey(k);
    }
}