package com.stardevllc.registry;

import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.registry.result.RegisterResult;
import com.stardevllc.starlib.objects.key.Key;

import java.util.*;
import java.util.function.Function;

class SimpleRegister<V> implements IRegister<V> {
    private final IRegistry<V> registry;
    private final Key key;
    private final String name;
    private final Map<Key, RegistryHolder<V>> holders = new HashMap<>();
    
    public SimpleRegister(IRegistry<V> registry, Key key, String name) {
        this.registry = registry;
        this.key = key != null ? key : Key.EMPTY;
        this.name = name != null ? name : "";
    }
    
    @Override
    public RegistryHolder<V> register(Key key, Function<Key, V> function) {
        if (this.holders.containsKey(key)) {
            return this.holders.get(key);
        }
        
        V object = function.apply(key);
        
        RegisterResult<V> result = registry.register(key, object);
        
        RegistryHolder<V> holder = switch (result) {
            case RegisterResult.AlreadyRegistered<V> v -> v.holder();
            case RegisterResult.EventCancelled<V> ignored -> throw new UnsupportedOperationException("Event Cancelled");
            case RegisterResult.Frozen<V> ignored -> throw new UnsupportedOperationException("Registry Frozen");
            case RegisterResult.ReplaceNotAllowed<V> ignored -> throw new UnsupportedOperationException("Replace Not Allowed");
            case RegisterResult.Success<V> v -> v.holder();
        };
        
        this.holders.put(key, holder);
        
        return holder;
    }
    
    @Override
    public IRegistry<V> getRegistry() {
        return registry;
    }
    
    private final class EntryItr implements Iterator<RegistryHolder<V>> {
        
        private final Iterator<RegistryHolder<V>> backingIterator;
        
        public EntryItr() {
            this.backingIterator = holders.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }
        
        @Override
        public RegistryHolder<V> next() {
            return backingIterator.next();
        }
    }
    
    private final class Entries extends AbstractCollection<RegistryHolder<V>> {
        
        @Override
        public Iterator<RegistryHolder<V>> iterator() {
            return new EntryItr();
        }
        
        @Override
        public int size() {
            return holders.size();
        }
    }
    
    @Override
    public Collection<RegistryHolder<V>> getEntries() {
        return new Entries();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Key getKey() {
        return key;
    }
    
    @Override
    public Key createKey(String k, V value) {
        return registry.createKey(k, value);
    }
    
    @Override
    public Key createKey(String k) {
        return registry.createKey(k);
    }
}