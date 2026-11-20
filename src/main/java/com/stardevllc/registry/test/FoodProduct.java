package com.stardevllc.registry.test;

import com.stardevllc.registry.IRegister;
import com.stardevllc.registry.IRegistry;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.starlib.objects.key.Key;

public class FoodProduct extends Product {
    
    public static void init() {
        
    }
    
    protected static final IRegistry<FoodProduct> REGISTRY = Product.REGISTRY.createChild("Food");
    private static final IRegister<FoodProduct> REGISTER = REGISTRY.createRegister();
    
    public static final RegistryHolder<FoodProduct> APPLE = REGISTER.register("apple", key -> new FoodProduct(key, "Apple"));
    public static final RegistryHolder<FoodProduct> BANANA = REGISTER.register("banana", key -> new FoodProduct(key, "Banana"));
    
    protected final String name;
    
    public FoodProduct(Key key, String name) {
        super(key);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "FoodProduct{" +
                "name='" + name + '\'' +
                ", key=" + key +
                '}';
    }
}