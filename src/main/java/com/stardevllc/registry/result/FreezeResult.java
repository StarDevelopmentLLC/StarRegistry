package com.stardevllc.registry.result;

import com.stardevllc.registry.IRegistry;

public sealed interface FreezeResult<V> {
    IRegistry<V> registry();
    
    record Unsupported<V>(IRegistry<V> registry) implements FreezeResult<V> {
        
    }
    
    record NotAllowed<V>(IRegistry<V> registry) implements FreezeResult<V> {
        
    }
    
    record AlreadyFrozen<V>(IRegistry<V> registry) implements FreezeResult<V> {
        
    }
    
    record EventCancelled<V>(IRegistry<V> registry) implements FreezeResult<V> {
        
    }
    
    record Success<V>(IRegistry<V> registry) implements FreezeResult<V> {
        
    }
}