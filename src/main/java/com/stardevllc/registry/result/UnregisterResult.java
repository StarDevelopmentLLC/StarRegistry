package com.stardevllc.registry.result;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.starlib.objects.key.Key;

public sealed interface UnregisterResult<V> {
    
    IRegistry<V> registry();
    
    Key key();
    
    V value();
    
    record Success<V>(IRegistry<V> registry, Key key, V value) implements UnregisterResult<V> {
        
    }
    
    record NotRegistered<V>(IRegistry<V> registry, Key key) implements UnregisterResult<V> {
        @Override
        public V value() {
            return null;
        }
    }
    
    record EventCancelled<V>(IRegistry<V> registry, Key key, V value) implements UnregisterResult<V> {
        
    }
    
    record Frozen<V>(IRegistry<V> registry, Key key, V value) implements UnregisterResult<V> {
        
    }
    
    record NotAllowed<V>(IRegistry<V> registry, Key key, V value) implements UnregisterResult<V> {
        
    }
}