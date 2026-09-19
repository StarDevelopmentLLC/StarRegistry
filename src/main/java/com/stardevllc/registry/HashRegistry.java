package com.stardevllc.registry;

import com.stardevllc.registry.event.*;
import com.stardevllc.registry.holder.DeferredHolder;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.registry.result.*;
import com.stardevllc.starlib.objects.key.*;

import java.util.*;
import java.util.function.*;

/**
 * This is a full implementation of the {@link IRegistry} class. It is not required to use it though
 *
 * @param <V> The type that is registered
 */
public class HashRegistry<V> implements IRegistry<V> {
    
    protected final Key key;
    protected final String name;
    
    protected final Map<Key, V> backingMap = new HashMap<>();
    protected final Map<Key, RegistryHolder<V>> holders = new HashMap<>();
    
    protected final RegistryDispatcher dispatcher = new RegistryDispatcher();
    
    protected final Set<RegistryFlag> flags = EnumSet.noneOf(RegistryFlag.class);
    
    protected final Map<Key, Child<V>> childRegistries = new HashMap<>();
    
    protected boolean frozen;
    
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
    
    private final class Registerer implements IRegisterer<V> {
        
        private final Key key;
        private final String name;
        private final Map<Key, RegistryHolder<V>> holders = new HashMap<>();
        
        public Registerer(Key key, String name) {
            this.key = key;
            this.name = name;
        }
        
        @Override
        public RegistryHolder<V> register(Key key, V object) {
            if (isFrozen() && !hasFlag(RegistryFlag.ALLOW_REGISTERERS_BYPASS_FROZEN)) {
                throw new UnsupportedOperationException();
            }
            
            V existing = backingMap.get(key);
            
            if (Objects.equals(object, existing)) {
                return holders.get(key);
            } else if (existing != null && !hasFlag(RegistryFlag.REPLACING) && !hasFlag(RegistryFlag.ALLOW_REGISTERERS_BYPASS_REPLACING)) {
                throw new UnsupportedOperationException();
            }
            
            Key fullKey = Keys.of(getKey(), IRegistry.separator(), key);
            
            RegistryHolder<V> holder = new RegistryHolder<>(HashRegistry.this, fullKey);
            backingMap.put(fullKey, object);
            HashRegistry.this.holders.put(fullKey, holder);
            this.holders.put(key, holder);
            
            if (object instanceof Keyable keyable) {
                if (keyable.supportsSettingKey()) {
                    keyable.setKey(key);
                }
            }
            
            return holder;
        }
        
        @Override
        public IRegistry<V> getRegistry() {
            return HashRegistry.this;
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
                return new Registerer.EntryItr();
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
            return HashRegistry.this.createKey(k, value);
        }
        
        @Override
        public Key createKey(String k) {
            return HashRegistry.this.createKey(k);
        }
    }
    
    public IRegisterer<V> createRegisterer(Key key, String name) {
        return new Registerer(key, name);
    }
    
    private final class DeferredRegisterer implements IDeferredRegisterer<V> {
        
        private final Key key;
        private final String name;
        private final Map<Key, DeferredHolder<V>> holders = new HashMap<>();
        private final Map<Key, RegisterResult<V>> results = new HashMap<>();
        
        private boolean hasRegisteredEntries;
        
        public DeferredRegisterer(Key key, String name) {
            this.key = key;
            this.name = name;
        }
        
        @Override
        public DeferredHolder<V> register(Key key, Supplier<V> supplier) {
            if (hasRegisteredEntries) {
                throw new IllegalStateException("Cannot register new entries when a DeferredRegister has already registered entries");
            }
            
            if (holders.containsKey(key)) {
                return holders.get(key);
            }
            
            DeferredHolder<V> holder = new DeferredHolder<>(HashRegistry.this, key, supplier);
            this.holders.put(key, holder);
            return holder;
        }
        
        @Override
        public IRegistry<V> getRegistry() {
            return HashRegistry.this;
        }
        
        @Override
        public void registerEntries() {
            if (this.hasRegisteredEntries) {
                throw new IllegalStateException("Already Registered the entries");
            }
            
            for (Map.Entry<Key, DeferredHolder<V>> entry : this.holders.entrySet()) {
                Key key = entry.getKey();
                DeferredHolder<V> holder = entry.getValue();
                V object = holder.getSupplier().get();
                if (isFrozen() && !hasFlag(RegistryFlag.ALLOW_REGISTERERS_BYPASS_FROZEN)) {
                    results.put(key, new RegisterResult.Frozen<>(HashRegistry.this, key, object));
                    continue;
                }
                
                V existing = backingMap.get(key);
                
                if (Objects.equals(object, existing)) {
                    results.put(key, new RegisterResult.AlreadyRegistered<>(holder));
                    continue;
                } else if (existing != null && !hasFlag(RegistryFlag.REPLACING) && !hasFlag(RegistryFlag.ALLOW_REGISTERERS_BYPASS_REPLACING)) {
                    results.put(key, new RegisterResult.ReplaceNotAllowed<>(HashRegistry.this, key, object, existing));
                    continue;
                }
                
                Key fullKey = Keys.of(getKey(), IRegistry.separator(), key);
                
                backingMap.put(fullKey, object);
                HashRegistry.this.holders.put(fullKey, holder);
                
                if (object instanceof Keyable keyable) {
                    if (keyable.supportsSettingKey()) {
                        keyable.setKey(fullKey);
                    }
                }
                
                results.put(key, new RegisterResult.Success<>(holder));
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
                return new DeferredRegisterer.EntryItr();
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
            return HashRegistry.this.createKey(k);
        }
        
        public Key getKey() {
            return key;
        }
        
        public String getName() {
            return name;
        }
    }
    
    @Override
    public IDeferredRegisterer<V> createDeferredRegisterer(Key key, String name) {
        return new DeferredRegisterer(key, name);
    }
    
    @Override
    public RegisterResult<V> register(Key key, V object) {
        if (isFrozen()) {
            return new RegisterResult.Frozen<>(this, key, object);
        }
        
        V existing = this.backingMap.get(key);
        
        if (Objects.equals(object, existing)) {
            return new RegisterResult.AlreadyRegistered<>(holders.get(key));
        } else if (existing != null && !hasFlag(RegistryFlag.REPLACING)) {
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
        } else if (existing != null && !hasFlag(RegistryFlag.REPLACING)) {
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
        int size = this.backingMap.size();
        for (IRegistry<V> value : this.childRegistries.values()) {
            size += value.size();
        }
        return size;
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
    
    @Override
    public <CV extends V> IRegistry<CV> createChild(Key key, String name) {
        Child<CV> child = new Child<CV>(this, key, name);
        this.childRegistries.put(key, (Child<V>) child);
        return child;
    }
    
    protected final class Child<CV extends V> extends HashRegistry<CV> {
        
        private final IRegistry<? super CV> parent;
        
        public Child(IRegistry<? super CV> parent, Key key, String name) {
            super(key, name);
            this.parent = parent;
        }
        
        @Override
        public IRegistry<? super CV> getParent() {
            return parent;
        }
        
//        @Override
//        public Key getKey() {
//            if (parent != null) {
//                return Keys.of(parent.getKey(), IRegistry.separator(), key);
//            }
//            
//            return key;
//        }
        
        @Override
        public boolean hasFlag(RegistryFlag flag) {
            return super.hasFlag(flag) || HashRegistry.this.hasFlag(flag);
        }
        
        @Override
        public Set<RegistryFlag> getFlags() {
            Set<RegistryFlag> flags = EnumSet.noneOf(RegistryFlag.class);
            flags.addAll(super.getFlags());
            flags.addAll(HashRegistry.this.getFlags());
            return flags;
        }
        
        @Override
        public String toString() {
            return "Child{" +
                    "key=" + key +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
    
    private class KeyItr implements Iterator<Key> {
        private final Deque<HashRegistry<V>> registryStack = new ArrayDeque<>(List.of(HashRegistry.this));
        private Iterator<Key> currentKeyIterator;
        private final Deque<Key> keyStack = new ArrayDeque<>();
        
        private void advance() {
            if (registryStack.isEmpty()) {
                return;
            }
            
            if (currentKeyIterator != null && !currentKeyIterator.hasNext()) {
                keyStack.pop();
            }
            
            while ((currentKeyIterator == null || !currentKeyIterator.hasNext()) && !registryStack.isEmpty()) {
                HashRegistry<V> currentRegistry = registryStack.pop();
                keyStack.push(currentRegistry.getKey());
                this.currentKeyIterator = currentRegistry.backingMap.keySet().iterator();
                
                for (Child<V> child : currentRegistry.childRegistries.values()) {
                    registryStack.push(child);
                }
            }
        }
        
        { advance(); }
        
        @Override
        public boolean hasNext() {
            return currentKeyIterator != null && currentKeyIterator.hasNext();
        }
        
        @Override
        public Key next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            StringBuilder sb = new StringBuilder();
            Iterator<Key> iter = keyStack.descendingIterator();
            while (iter.hasNext()) {
                Key key = iter.next();
                sb.append(key).append(IRegistry.separator());
            }
            
            Key key = Keys.of(sb.toString(), currentKeyIterator.next());
            advance();
            return key;
        }
    }
    
    private class KeySet extends AbstractSet<Key> {
        @Override
        public Iterator<Key> iterator() {
            return new KeyItr();
        }
        
        @Override
        public int size() {
            return HashRegistry.this.size();
        }
    }
    
    @Override
    public Set<Key> keySet() {
        return new KeySet();
    }
    
    private class ValueItr implements Iterator<V> {
        
        private Iterator<Map.Entry<Key, V>> iterator = backingMap.entrySet().iterator();
        private final Iterator<Map.Entry<Key, Child<V>>> childIterator = childRegistries.entrySet().iterator();
        
        @Override
        public boolean hasNext() {
            if (iterator.hasNext()) {
                return true;
            }
            
            if (childIterator.hasNext()) {
                iterator = childIterator.next().getValue().entrySet().iterator();
                return hasNext();
            }
            
            return false;
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
            return HashRegistry.this.size();
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
    
    private record ItrEntry<V>(Key newKey, Map.Entry<Key, V> backingEntry) implements Map.Entry<Key, V> {
        @Override
        public Key getKey() {
            return newKey;
        }
        
        @Override
        public V getValue() {
            return backingEntry.getValue();
        }
        
        @Override
        public V setValue(V value) {
            return backingEntry.setValue(value);
        }
    }
    
    private class EntryItr implements Iterator<Map.Entry<Key, V>> {
        
        private Iterator<Map.Entry<Key, V>> iterator = backingMap.entrySet().iterator();
        private final Iterator<Map.Entry<Key, Child<V>>> childIterator = childRegistries.entrySet().iterator();
        private Map.Entry<Key, Child<V>> childEntry;
        
        @Override
        public boolean hasNext() {
            if (iterator.hasNext()) {
                return true;
            }
            
            if (childIterator.hasNext()) {
                childEntry = childIterator.next();
                iterator = childEntry.getValue().entrySet().iterator();
                return hasNext();
            }
            
            return false;
        }
        
        @Override
        public Map.Entry<Key, V> next() {
            if (childEntry == null) {
                return iterator.next();
            }
            
            Map.Entry<Key, V> entry = iterator.next();
            return new ItrEntry<>(Keys.of(childEntry.getValue().getKey(), IRegistry.separator(), entry.getKey()), entry);
        }
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<Key, V>> {
        @Override
        public Iterator<Map.Entry<Key, V>> iterator() {
            return new EntryItr();
        }
        
        @Override
        public int size() {
            return HashRegistry.this.size();
        }
    }
    
    @Override
    public Set<Map.Entry<Key, V>> entrySet() {
        return new EntrySet();
    }
    
    private class HolderItr implements Iterator<RegistryHolder<V>> {
        
        private Iterator<RegistryHolder<V>> iterator = holders.values().iterator();
        private final Iterator<Map.Entry<Key, Child<V>>> childIterator = childRegistries.entrySet().iterator();
        
        @Override
        public boolean hasNext() {
            if (iterator.hasNext()) {
                return true;
            }
            
            if (childIterator.hasNext()) {
                iterator = childIterator.next().getValue().holderSet().iterator();
                return hasNext();
            }
            
            return false;
        }
        
        @Override
        public RegistryHolder<V> next() {
            return iterator.next();
        }
    }
    
    private class HolderSet extends AbstractSet<RegistryHolder<V>> {
        @Override
        public Iterator<RegistryHolder<V>> iterator() {
            return new HolderItr();
        }
        
        @Override
        public int size() {
            return HashRegistry.this.size();
        }
    }
    
    @Override
    public Set<RegistryHolder<V>> holderSet() {
        return new HolderSet();
    }
    
    @Override
    public String toString() {
        return "HashRegistry{" +
                "key=" + key +
                ", name='" + name + '\'' +
                '}';
    }
}
