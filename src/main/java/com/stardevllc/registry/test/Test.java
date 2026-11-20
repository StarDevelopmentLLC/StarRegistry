package com.stardevllc.registry.test;

import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keys;

import java.util.Map;

public final class Test {
    public static void main(String[] args) {
        Product.REGISTRY.freeze();
        Product.REGISTRY.register("test1", new Product(Keys.of("test1")) {});
        
        FoodProduct.init();
        OfficeProduct.init();
        
        for (Map.Entry<Key, Product> entry : Product.REGISTRY.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        
        System.out.println(FoodProduct.APPLE.get());
        System.out.println(FoodProduct.BANANA.get());
        
        System.out.println(OfficeProduct.PEN.get());
        System.out.println(OfficeProduct.SCISSOR.get());
    }
}