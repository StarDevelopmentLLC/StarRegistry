package com.stardevllc.registry.test;

import com.stardevllc.registry.*;
import com.stardevllc.registry.holder.DeferredHolder;
import com.stardevllc.registry.holder.RegistryHolder;
import com.stardevllc.starlib.objects.key.*;

import java.util.Map;

public final class Test {
    
    public static class Product implements Keyable {
        
        public static final IRegistry<Product> REGISTRY = new HashRegistry<>(Keys.of("products"), "Products", RegistryFlag.FREEZING);
        
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
    
    public static class FoodProduct extends Product {
        
        public static void init() {
            FD1.registerEntries();
        }
        
        public static final IRegistry<FoodProduct> REGISTRY = Product.REGISTRY.createChild(Keys.of("food"), "Food");
        
        private static final IRegister<FoodProduct> FR1 = REGISTRY.createRegister(Keys.of("fr1"), "FR1");
        private static final IDeferredRegister<FoodProduct> FD1 = REGISTRY.createDeferredRegister(Keys.of("fd1"), "FD1");
        
        public static final RegistryHolder<FoodProduct> APPLE = FR1.register(Keys.of("apple"), key -> new FoodProduct(key, "Apple"));
        public static final DeferredHolder<FoodProduct> BANANA = FD1.register(Keys.of("banana"), key -> new FoodProduct(key, "Banana"));
        
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
    
    public static class OfficeProduct extends Product {
        
        public static void init() {
            FD1.registerEntries();
        }
        
        public static final IRegistry<OfficeProduct> REGISTRY = Product.REGISTRY.createChild(Keys.of("office"), "Office");
        
        private static final IRegister<OfficeProduct> FR1 = REGISTRY.createRegister(Keys.of("fr1"), "FR1");
        private static final IDeferredRegister<OfficeProduct> FD1 = REGISTRY.createDeferredRegister(Keys.of("fd1"), "FD1");
        
        public static final RegistryHolder<OfficeProduct> PEN = FR1.register(Keys.of("pen"), key -> new OfficeProduct(key, "pen"));
        public static final DeferredHolder<OfficeProduct> SCISSOR = FD1.register(Keys.of("scissor"), key -> new OfficeProduct(key, "scissor"));
        
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
    
    public static void main(String[] args) {
        FoodProduct.init();
        System.out.println(FoodProduct.APPLE.get());
        System.out.println(FoodProduct.BANANA.get());
        System.out.println();
        
        OfficeProduct.init();
        System.out.println(OfficeProduct.PEN.get());
        System.out.println(OfficeProduct.SCISSOR.get());
        System.out.println();
        
        Product.REGISTRY.register(Keys.of("test1"), new Product(Keys.of("test1")));
        
        IRegister<Product> r1 = Product.REGISTRY.createRegister(Keys.of("r1"), "R1");
        r1.register(Keys.of("test2"), Product::new);
        
        IDeferredRegister<Product> d1 = Product.REGISTRY.createDeferredRegister(Keys.of("d1"), "D1");
        d1.register(Keys.of("test3"), Product::new);
        
        for (Product product : Product.REGISTRY) {
            System.out.println(product);
        }
        
        d1.registerEntries();
        
        System.out.println();
        
        for (Product product : Product.REGISTRY) {
            System.out.println(product);
        }
        
        System.out.println();
        
        for (Map.Entry<Key, Product> entry : Product.REGISTRY.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}