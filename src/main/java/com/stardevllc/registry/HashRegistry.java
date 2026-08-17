package com.stardevllc.registry;

import com.stardevllc.registry.event.*;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.registry.result.*;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keyable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This is a full implementation of the {@link IRegistry} class. It is not required to use it though
 *
 * @param <V> The type that is registered
 */
public class HashRegistry<V> implements IRegistry<V> {
    
    private final Key key;
    private final String name;
    
    private final Map<Key, V> backingMap = new HashMap<>();
    private final Map<Key, RegistryHolder<V>> holders = new HashMap<>();
    
    private final RegistryDispatcher dispatcher = new RegistryDispatcher();
    
    private final Set<RegistryFlag> flags = EnumSet.noneOf(RegistryFlag.class);
    
    private boolean frozen;
    
    public HashRegistry() {
        this(Key.EMPTY, "");
    }
    
    public HashRegistry(Key key, String name) {
        this.key = key;
        this.name = name;
    }
    
    @Override
    public V get(Key key) {
        return backingMap.get(key);
    }
    
    @Override
    public RegisterResult<V> register(Key key, V object) {
        if (isFrozen()) {
            return new RegisterResult.Frozen<>(this, key, object);
        }
        
        V existing = this.backingMap.get(key);
        
        if (Objects.equals(object, existing)) {
            return new RegisterResult.AlreadyRegistered<>(holders.get(key));
        } else if (!hasFlag(RegistryFlag.REPLACING)) {
            return new RegisterResult.ReplaceNotAllowed<>(this, key, object, existing);
        }
        
        RegisterEvent<V> event = this.dispatcher.dispatch(new RegisterEvent<>(this, key, object, existing));
        
        if (event.isCancelled()) {
            return new RegisterResult.EventCancelled<>(this, key, object, existing);
        }
        
        RegistryHolder<V> holder = new RegistryHolder<>(this, key);
        this.backingMap.put(key, object);
        this.holders.put(key, holder);
        
        if (object instanceof Keyable keyable) {
            if (keyable.supportsSettingKey()) {
                keyable.setKey(key);
            }
        }
        
        return new RegisterResult.Success<>(holder);
    }
    
    @Override
    public SetResult<V> set(Key key, V object) {
        if (isFrozen()) {
            return new SetResult.Frozen<>(this, key, object);
        }
        
        V existing = this.backingMap.get(key);
        
        if (Objects.equals(object, existing)) {
            return new SetResult.AlreadyRegistered<>(holders.get(key));
        } else if (!hasFlag(RegistryFlag.REPLACING)) {
            return new SetResult.ReplaceNotAllowed<>(this, key, object, existing);
        }
        
        SetEvent<V> event = this.dispatcher.dispatch(new SetEvent<>(this, key, object, existing));
        
        if (event.isCancelled()) {
            return new SetResult.EventCancelled<>(this, key, object, existing);
        }
        
        RegistryHolder<V> holder = this.holders.computeIfAbsent(key, k -> new RegistryHolder<>(HashRegistry.this, k));
        this.backingMap.put(key, object);
        
        if (object instanceof Keyable keyable) {
            if (keyable.supportsSettingKey()) {
                keyable.setKey(key);
            }
        }
        
        return new SetResult.Success<>(holder);
    }
    
    @Override
    public UnregisterResult<V> unregister(Key key) {
        V value = get(key);
        
        if (value == null) {
            return new UnregisterResult.NotRegistered<>(this, key);
        }
        
        if (isFrozen()) {
            return new UnregisterResult.Frozen<>(this, key, value);
        }
        
        if (!this.hasFlag(RegistryFlag.UNREGISTERING)) {
            return new UnregisterResult.NotAllowed<>(this, key, value);
        }
        
        UnregisterEvent<V> event = this.dispatcher.dispatch(new UnregisterEvent<>(this, key, value));
        if (event.isCancelled()) {
            return new UnregisterResult.EventCancelled<>(this, key, value);
        }
        
        this.backingMap.remove(key);
        this.holders.remove(key);
        
        return new UnregisterResult.Success<>(this, key, value);
    }
    
    @Override
    public int size() {
        return this.backingMap.size();
    }
    
    @Override
    public Set<RegistryFlag> getFlags() {
        return EnumSet.copyOf(this.flags);
    }
    
    @Override
    public boolean hasFlag(RegistryFlag flag) {
        return this.flags.contains(flag);
    }
    
    @Override
    public FreezeResult<V> freeze() {
        if (!hasFlag(RegistryFlag.FREEZING)) {
            return new FreezeResult.NotAllowed<>(this);
        }
        
        if (this.frozen) {
            return new FreezeResult.AlreadyFrozen<>(this);
        }
        
        FreezeEvent<V> event = this.dispatcher.dispatch(new FreezeEvent<>(this));
        
        if (event.isCancelled()) {
            return new FreezeResult.EventCancelled<>(this);
        }
        
        this.frozen = true;
        
        return new FreezeResult.Success<>(this);
    }
    
    @Override
    public UnfreezeResult<V> unfreeze() {
        if (!hasFlag(RegistryFlag.UNFREEZING)) {
            return new UnfreezeResult.NotAllowed<>(this);
        }
        
        if (!this.frozen) {
            return new UnfreezeResult.NotFrozen<>(this);
        }
        
        UnfreezeEvent<V> event = this.dispatcher.dispatch(new UnfreezeEvent<>(this));
        
        if (event.isCancelled()) {
            return new UnfreezeResult.EventCancelled<>(this);
        }
        
        this.frozen = false;
        
        return new UnfreezeResult.Success<>(this);
    }
    
    @Override
    public RegistryDispatcher getDispatcher() {
        return dispatcher;
    }
    
    @Override
    public Key getKey() {
        return this.key;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isFrozen() {
        return hasFlag(RegistryFlag.FREEZING) && frozen;
    }
    
    @Override
    public boolean containsKey(Key key) {
        return this.backingMap.containsKey(key);
    }
    
    @Override
    public boolean containsValue(V value) {
        return this.backingMap.containsValue(value);
    }
    
    @Override
    public void forEach(BiConsumer<Key, V> consumer) {
        entrySet().forEach(e -> consumer.accept(e.getKey(), e.getValue()));
    }
    
    @Override
    public void forEachKey(Consumer<Key> consumer) {
        keySet().forEach(consumer);
    }
    
    @Override
    public void forEachValue(Consumer<V> consumer) {
        values().forEach(consumer);
    }
    
    @Override
    public void forEachEntry(Consumer<Map.Entry<Key, V>> consumer) {
        entrySet().forEach(consumer);
    }
    
    private class KeyItr implements Iterator<Key> {
        
        private final Iterator<Key> iterator = backingMap.keySet().iterator();
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public Key next() {
            return iterator.next();
        }
    }
    
    private class KeySet extends AbstractSet<Key> {
        
        @Override
        public Iterator<Key> iterator() {
            return new KeyItr();
        }
        
        @Override
        public int size() {
            return backingMap.size();
        }
    }
    
    @Override
    public Set<Key> keySet() {
        return new KeySet();
    }
    
    private class ValueItr implements Iterator<V> {
        
        private final Iterator<Map.Entry<Key, V>> iterator;
        
        public ValueItr() {
            this.iterator = backingMap.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public V next() {
            return iterator.next().getValue();
        }
    }
    
    private class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueItr();
        }
        
        public int size() {
            return backingMap.size();
        }
    }
    
    @Override
    public Collection<V> values() {
        return new Values();
    }
    
    @Override
    public Iterator<V> iterator() {
        return new ValueItr();
    }
    
    private class EntryItr implements Iterator<Map.Entry<Key, V>> {
        
        private final Iterator<Map.Entry<Key, V>> iterator;
        
        public EntryItr() {
            this.iterator = backingMap.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public Map.Entry<Key, V> next() {
            return iterator.next();
        }
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<Key, V>> {
        @Override
        public Iterator<Map.Entry<Key, V>> iterator() {
            return new EntryItr();
        }
        
        @Override
        public int size() {
            return backingMap.size();
        }
    }
    
    @Override
    public Set<Map.Entry<Key, V>> entrySet() {
        return new EntrySet();
    }
    
    private class HolderItr implements Iterator<RegistryHolder<V>> {
        
        private final Iterator<Map.Entry<Key, RegistryHolder<V>>> iterator;
        
        public HolderItr() {
            this.iterator = holders.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public RegistryHolder<V> next() {
            return iterator.next().getValue();
        }
    }
    
    private class HolderSet extends AbstractSet<RegistryHolder<V>> {
        @Override
        public Iterator<RegistryHolder<V>> iterator() {
            return new HolderItr();
        }
        
        @Override
        public int size() {
            return holders.size();
        }
    }
    
    @Override
    public Set<RegistryHolder<V>> holderSet() {
        return new HolderSet();
    }
}
