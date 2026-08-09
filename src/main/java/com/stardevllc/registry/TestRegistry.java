package com.stardevllc.registry;

import com.stardevllc.registry.event.*;
import com.stardevllc.registry.event.EventListener;
import com.stardevllc.starlib.objects.key.Key;

import java.util.*;

public class TestRegistry<V> implements IRegistry<V> {
    
    private final Map<Key, V> backingMap = new HashMap<>();
    private final Map<Key, RegistryHolder<V>> holders = new HashMap<>();
    
    private final List<EventListener<V, RegistryEvent<V>>> registerListeners = new ArrayList<>();
    
    private final Set<RegistryFlag> flags = EnumSet.noneOf(RegistryFlag.class);
    
    @Override
    public V get(Key key) {
        return backingMap.get(key);
    }
    
    @Override
    public <E extends RegistryEvent<V>> void addListener(Class<E> type, EventListener<V, E> listener) {
        if (RegisterEvent.class.isAssignableFrom(type)) {
            registerListeners.add((EventListener<V, RegistryEvent<V>>) listener);
        }
        
        //TODO The other types when implemented
    }
    
    @Override
    public RegisterResult<V> register(Key key, V object) {
        V existing = this.backingMap.get(key);
        
        if (Objects.equals(object, existing)) {
            return new RegisterResult.AlreadyRegistered<>(holders.get(key));
        }
        
        RegisterEvent<V> event = new RegisterEvent<>(this, key, object, existing);
        for (EventListener<V, RegistryEvent<V>> listener : this.registerListeners) {
            listener.onEvent(event);
        }
        
        if (event.isCancelled()) {
            return new RegisterResult.EventCancelled<>(this, key, object, existing);
        }
        
        return null;
    }
    
    @Override
    public void addRegisterListener(EventListener<V, RegisterEvent<V>> listener) {
        
    }
    
    @Override
    public V set(Key key, V object) {
        return null;
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public Set<RegistryFlag> getFlags() {
        return Set.of();
    }
    
    @Override
    public boolean hasFlag(RegistryFlag flag) {
        return false;
    }
    
    @Override
    public Iterator<V> iterator() {
        return null;
    }
}
