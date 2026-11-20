package com.stardevllc.registry;

import com.stardevllc.registry.holder.DeferredHolder;
import com.stardevllc.registry.result.RegisterResult;
import com.stardevllc.starlib.objects.key.Key;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("DuplicatedCode")
class SimpleDeferredRegister<V> implements IDeferredRegister<V> {
    
    private final IRegistry<V> registry;
    private final Key key;
    private final String name;
    private final Map<Key, DeferredHolder<V>> holders = new HashMap<>();
    private final Map<Key, RegisterResult<V>> results = new HashMap<>();
    
    private boolean hasRegisteredEntries;
    
    public SimpleDeferredRegister(IRegistry<V> registry, Key key, String name) {
        this.registry = registry;
        this.key = key != null ? key : Key.EMPTY;
        this.name = name != null ? name : "";
    }
    
    @Override
    public DeferredHolder<V> register(Key key, Supplier<V> supplier) {
        if (hasRegisteredEntries) {
            throw new IllegalStateException("Cannot register new entries when a DeferredRegister has already registered entries");
        }
        
        if (holders.containsKey(key)) {
            return holders.get(key);
        }
        
        DeferredHolder<V> holder = new DeferredHolder<>(registry, key, supplier);
        this.holders.put(key, holder);
        return holder;
    }
    
    @Override
    public DeferredHolder<V> register(Key key, Function<Key, V> supplierFunction) {
        if (hasRegisteredEntries) {
            throw new IllegalStateException("Cannot register new entries when a DeferredRegister has already registered entries");
        }
        
        if (holders.containsKey(key)) {
            return holders.get(key);
        }
        
        DeferredHolder<V> holder = new DeferredHolder<>(registry, key, supplierFunction);
        this.holders.put(key, holder);
        return holder;
    }
    
    @Override
    public IRegistry<V> getRegistry() {
        return registry;
    }
    
    @Override
    public void registerEntries() {
        if (this.hasRegisteredEntries) {
            throw new IllegalStateException("Already Registered the entries");
        }
        
        for (Map.Entry<Key, DeferredHolder<V>> entry : this.holders.entrySet()) {
            Key key = entry.getKey();
            DeferredHolder<V> holder = entry.getValue();
            
            if (holder.hasBeenRegistered()) {
                continue;
            }
            
            RegisterResult<V> result = registry.register(holder);
            holder.markRegistered();
            results.put(key, result);
        }
        
        this.hasRegisteredEntries = true;
    }
    
    private final class EntryItr implements Iterator<DeferredHolder<V>> {
        
        private final Iterator<DeferredHolder<V>> backingIterator;
        
        public EntryItr() {
            this.backingIterator = holders.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }
        
        @Override
        public DeferredHolder<V> next() {
            return backingIterator.next();
        }
    }
    
    private final class Entries extends AbstractCollection<DeferredHolder<V>> {
        
        @Override
        public Iterator<DeferredHolder<V>> iterator() {
            return new EntryItr();
        }
        
        @Override
        public int size() {
            return holders.size();
        }
    }
    
    @Override
    public Collection<DeferredHolder<V>> getEntries() {
        return new Entries();
    }
    
    @Override
    public RegisterResult<V> getResult(Key key) {
        return results.get(key);
    }
    
    @Override
    public Collection<RegisterResult<V>> getResults() {
        return List.of();
    }
    
    @Override
    public Key createKey(String k) {
        return registry.createKey(k);
    }
    
    public Key getKey() {
        return key;
    }
    
    public String getName() {
        return name;
    }
}