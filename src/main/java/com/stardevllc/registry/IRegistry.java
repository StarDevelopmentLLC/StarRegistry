package com.stardevllc.registry;

import com.stardevllc.registry.event.*;
import com.stardevllc.registry.event.EventListener;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.registry.result.*;
import com.stardevllc.starlib.event.EventDispatcher;
import com.stardevllc.starlib.objects.Nameable;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keyable;
import com.stardevllc.starlib.objects.key.impl.StringKey;

import java.util.*;
import java.util.function.*;

public interface IRegistry<V> extends Iterable<V>, Nameable, Keyable, Function<Key, V> {
    V get(Key key);
    
    default V get(String key) {
        return get(createKey(key));
    }
    
    default EventDispatcher getDispatcher() {
        return EventDispatcher.NOOP;
    }
    
    default <E extends RegistryEvent<V>> void addListener(Class<E> type, EventListener<V, E> listener) {
        getDispatcher().addListener(listener);
    }
    
    RegisterResult<V> register(Key key, V object);
    
    default RegisterResult<V> register(String key, V object) {
        return register(createKey(key, object), object);
    }
    
    default void addRegisterListener(EventListener<V, RegisterEvent<V>> listener) {
        getDispatcher().addListener(listener);
    }
    
    default void addSetListener(EventListener<V, SetEvent<V>> listener) {
        getDispatcher().addListener(listener);
    }
    
    SetResult<V> set(Key key, V object);
    
    int size();
    
    default boolean isFrozen() {
        return false;
    }
    
    default FreezeResult<V> freeze() {
        return new FreezeResult.Unsupported<>(this);
    }
    
    default void addFreezeListener(EventListener<V, FreezeEvent<V>> listener) {
        getDispatcher().addListener(listener);
    }
    
    default UnfreezeResult<V> unfreeze() {
        return new UnfreezeResult.Unsupported<>(this);
    }
    
    default void addUnfreezeListener(EventListener<V, UnfreezeEvent<V>> listener) {
        getDispatcher().addListener(listener);
    }
    
    UnregisterResult<V> unregister(Key key);
    
    default UnregisterResult<V> unregister(String key) {
        return unregister(createKey(key));
    }
    
    default void addUnregisterListener(EventListener<V, UnregisterEvent<V>> listener) {
        getDispatcher().addListener(listener);
    }
    
    default boolean isEmpty() {
        return size() == 0;
    }
    
    default boolean isNotEmpty() {
        return size() > 0;
    }
    
    Set<Map.Entry<Key, V>> entrySet();
    
    Set<RegistryHolder<V>> holderSet();
    
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
    
    boolean containsKey(Key key);
    
    boolean containsValue(V value);
    
    void forEach(BiConsumer<Key, V> consumer);
    
    void forEachKey(Consumer<Key> consumer);
    
    void forEachValue(Consumer<V> consumer);
    
    void forEachEntry(Consumer<Map.Entry<Key, V>> consumer);
    
    Set<Key> keySet();    
    
    Collection<V> values();
    
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
    
    static char separator() {
        return '/';
    }
}