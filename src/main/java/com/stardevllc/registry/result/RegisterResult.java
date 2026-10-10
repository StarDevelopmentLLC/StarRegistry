package com.stardevllc.registry.result;

import com.stardevllc.registry.IRegistry;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.starlib.objects.key.Key;

public sealed interface RegisterResult<V> {
    
    IRegistry<V> registry();
    
    Key key();
    
    default RegistryHolder<V> getHolder() {
        return switch (this) {
            case RegisterResult.AlreadyRegistered<V> v -> v.holder();
            case RegisterResult.Success<V> v -> v.holder();
            default -> null;
        };
    }
    
    default Success<V> asSuccess() {
        return (Success<V>) this;
    }
    
    default boolean isSuccess() {
        return this instanceof RegisterResult.Success<V> || this instanceof RegisterResult.AlreadyRegistered<V>;
    }
    
    default boolean isFail() {
        return !isSuccess();
    }
    
    default AlreadyRegistered<V> asAlreadyRegistered() {
        return (AlreadyRegistered<V>) this;
    }
    
    default EventCancelled<V> asEventCancelled() {
        return (EventCancelled<V>) this;
    }
    
    default Frozen<V> asFrozen() {
        return (Frozen<V>) this;
    }
    
    default ReplaceNotAllowed<V> asReplaceNotAllowed() {
        return (ReplaceNotAllowed<V>) this;
    }
    
    record Success<V>(RegistryHolder<V> holder) implements RegisterResult<V> {
        public IRegistry<V> registry() {
            return holder.registry();
        }
        
        public Key key() {
            return holder.getKey();
        }
    }
    
    record AlreadyRegistered<V>(RegistryHolder<V> holder) implements RegisterResult<V> {
        @Override
        public IRegistry<V> registry() {
            return holder.registry();
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