package com.stardevllc.registry.test;

import com.stardevllc.registry.IDeferredRegister;
import com.stardevllc.registry.IRegistry;
import com.stardevllc.registry.holder.DeferredHolder;
import com.stardevllc.starlib.objects.key.Key;

public class OfficeProduct extends Product {
    
    protected static final IRegistry<OfficeProduct> REGISTRY = Product.REGISTRY.createChild("Office");
    private static final IDeferredRegister<OfficeProduct> REGISTER = REGISTRY.createDeferredRegister();
    
    public static final DeferredHolder<OfficeProduct> PEN = REGISTER.register("pen", key -> new OfficeProduct(key, "Pen"));
    public static final DeferredHolder<OfficeProduct> SCISSOR = REGISTER.register("scissor", key -> new OfficeProduct(key, "Scissor"));
    
    public static void init() {
        REGISTER.registerEntries();
    }   
    
    protected final String name;
    
    public OfficeProduct(Key key, String name) {
        super(key);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "OfficeProduct{" +
                "name='" + name + '\'' +
                ", key=" + key +
                '}';
    }
}