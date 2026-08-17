package com.stardevllc.registry.result;

import com.stardevllc.registry.IRegistry;

public sealed interface UnfreezeResult<V> {
    IRegistry<V> registry();
    
    record Unsupported<V>(IRegistry<V> registry) implements UnfreezeResult<V> {
        
    }
    
    record NotAllowed<V>(IRegistry<V> registry) implements UnfreezeResult<V> {
        
    }
    
    record NotFrozen<V>(IRegistry<V> registry) implements UnfreezeResult<V> {
        
    }
    
    record EventCancelled<V>(IRegistry<V> registry) implements UnfreezeResult<V> {
        
    }
    
    record Success<V>(IRegistry<V> registry) implements UnfreezeResult<V> {
        
    }
}