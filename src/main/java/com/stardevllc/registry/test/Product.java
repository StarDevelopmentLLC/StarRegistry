package com.stardevllc.registry.test;

import com.stardevllc.registry.*;
import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keyable;

import java.util.Collection;

public abstract class Product implements Keyable {
    
    protected static final IRegistry<Product> REGISTRY = new HashRegistry<>(RegistryFlag.FREEZING, RegistryFlag.UNFREEZING, RegistryFlag.ALLOW_REGISTERS_BYPASS_FROZEN, RegistryFlag.ALLOW_REGISTERS_BYPASS_REPLACING);
    
    public static Collection<Product> getProducts() {
        return REGISTRY.values();
    }
    
    protected final Key key;
    
    public Product(Key key) {
        this.key = key;
    }
    
    @Override
    public Key getKey() {
        return key;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "key=" + key +
                '}';
    }
}