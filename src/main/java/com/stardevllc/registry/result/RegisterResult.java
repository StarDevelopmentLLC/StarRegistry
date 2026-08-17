package com.stardevllc.registry.result;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.starlib.objects.key.Key;

public sealed interface RegisterResult<V> {
    
    IRegistry<V> registry();
    
    Key key();
    
    record Success<V>(RegistryHolder<V> holder) implements RegisterResult<V> {
        public IRegistry<V> registry() {
            return holder.getRegistry();
        }
        
        public Key key() {
            return holder.getKey();
        }
    }
    
    record AlreadyRegistered<V>(RegistryHolder<V> holder) implements RegisterResult<V> {
        @Override
        public IRegistry<V> registry() {
            return holder.getRegistry();
        }
        
        @Override
        public Key key() {
            return holder.getKey();
        }
    }
    
    record EventCancelled<V>(IRegistry<V> registry, Key key, V object, V existing) implements RegisterResult<V> {
        
    }
    
    record Frozen<V>(IRegistry<V> registry, Key key, V object) implements RegisterResult<V> {
        
    }
    
    record ReplaceNotAllowed<V>(IRegistry<V> registry, Key key, V object, V existing) implements RegisterResult<V> {
        
    }
}