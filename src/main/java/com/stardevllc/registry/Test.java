package com.stardevllc.registry;

import com.stardevllc.starlib.objects.key.Key;
import com.stardevllc.starlib.objects.key.Keys;

import java.util.Iterator;

public final class Test {
    public static void main(String[] args) {
        class Asset {
            String id;
            
            public Asset(String id) {
                this.id = id;
            }
        }
        
        IRegistry<Asset> assetRegistry = new HashRegistry<>(Keys.of("assets"), "Assets");
        
        class TechAsset extends Asset {
            String brand;
            String model;
            
            public TechAsset(String id, String brand, String model) {
                super(id);
                this.brand = brand;
                this.model = model;
            }
        }
        
        IRegistry<TechAsset> techRegistry = assetRegistry.createChild(Keys.of("tech"), "Tech");
        
        class ComputerAsset extends TechAsset {
            
            public ComputerAsset(String id, String brand, String model) {
                super(id, brand, model);
            }
        }
        
        IRegistry<ComputerAsset> computerRegistry = techRegistry.createChild(Keys.of("computers"), "Computers");
        
        class LaptopAsset extends ComputerAsset {
            public LaptopAsset(String id, String brand, String model) {
                super(id, brand, model);
            }
        }
        
        IRegistry<LaptopAsset> laptopAssets = computerRegistry.createChild(Keys.of("laptops"), "Laptops");
        
        class DesktopAsset extends ComputerAsset {
            public DesktopAsset(String id, String brand, String model) {
                super(id, brand, model);
            }
        }
        
        IRegistry<DesktopAsset> desktopAssets = computerRegistry.createChild(Keys.of("desktops"), "Desktops");
        
        class PhoneAsset extends TechAsset {
            
            public PhoneAsset(String id, String brand, String model) {
                super(id, brand, model);
            }
        }
        
        IRegistry<PhoneAsset> phoneRegistry = techRegistry.createChild(Keys.of("phones"), "Phones");
        
        laptopAssets.register("1", new LaptopAsset("1", "Lenovo", "ThinkPad"));
        desktopAssets.register("1", new DesktopAsset("1", "Dell", "123"));
        
        phoneRegistry.register("1", new PhoneAsset("1", "Apple", "iPhone"));
        
        System.out.println("Asset Registry Size: " + assetRegistry.size());
        for (Key key : assetRegistry.keySet()) {
            System.out.println(key);
        }
        
        System.out.println("Tech Registry Size: " + techRegistry.size());
        for (Key key : techRegistry.keySet()) {
            System.out.println(key);
        }
        
        System.out.println("Computer Registry Size: " + computerRegistry.size());
        for (Key key : computerRegistry.keySet()) {
            System.out.println(key);
        }
        
        System.out.println("Laptop Registry Size: " + laptopAssets.size());
        for (Key key : laptopAssets.keySet()) {
            System.out.println(key);
        }
        
        System.out.println("Desktop Registry Size: " + desktopAssets.size());
        for (Key key : desktopAssets.keySet()) {
            System.out.println(key);
        }
        
        System.out.println("Phone Registry Size: " + phoneRegistry.size());
        for (Key key : phoneRegistry.keySet()) {
            System.out.println(key);
        }
    }
}