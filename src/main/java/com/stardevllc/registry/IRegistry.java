package com.stardevllc.registry;

import com.stardevllc.registry.event.*;
import com.stardevllc.starlib.objects.Nameable;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keyable;
import com.stardevllc.starlib.objects.key.impl.StringKey;

import java.util.Set;
import java.util.function.Function;

public interface IRegistry<V> extends Iterable<V>, Nameable, Keyable, Function<Key, V> {
    V get(Key key);
    
    default V get(String key) {
        return get(createKey(key));
    }
    
    <E extends RegistryEvent<V>> void addListener(Class<E> type, EventListener<V, E> listener);
    
    RegisterResult<V> register(Key key, V object);
    
    default RegisterResult<V> register(String key, V object) {
        return register(createKey(key, object), object);
    }
    
    void addRegisterListener(EventListener<V, RegisterEvent<V>> listener);
    
    V set(Key key, V object);
    
    int size();
    
    default boolean isEmpty() {
        return size() == 0;
    }
    
    default boolean isNotEmpty() {
        return size() > 0;
    }
    
    Set<RegistryFlag> getFlags();
    
    boolean hasFlag(RegistryFlag flag);
    
    /**
     * Creates a key giving a String input. Used for the String convenience methods, The value is provided if needed
     *
     * @param k     The raw key providedd
     * @param value The value being registered
     * @return The key
     */
    default Key createKey(String k, V value) {
        return new StringKey(k);
    }
    
    /**
     * Creates a key given a String input
     *
     * @param k The raw key
     * @return The key instance
     */
    default Key createKey(String k) {
        return new StringKey(k);
    }
    
    @Override
    default V apply(Key key) {
        return get(key);
    }
    
    @Override
    default String getName() {
        return "";
    }
    
    @Override
    default Key getKey() {
        return StringKey.EMPTY;
    }
}