package com.stardevllc.registry;

import com.stardevllc.starlib.objects.key.Key;

public enum RegistryFlag {
    
    /**
     * Freezing means that the registry does not accept new values <br>
     * This flag being present doesn't mean that the registry is frozen, just that the freeze() method will work
     */
    FREEZING,
    
    /**
     * Unfreezing means that the registry can be unfrozen by calling the method
     */
    UNFREEZING,
    
    /**
     * This flag being present means that values can be replaced within the registry
     */
    REPLACING,
    
    /**
     * This flag being present means that values can be unregistered from the registry
     */
    UNREGISTERING,
    
    /**
     * This flag being present means that the bulk clear action can be performed
     */
    CLEARING,
    
    /**
     * This flag being present means that the registry will check partial keys in the get method
     */
    CHECK_PARTIAL_IN_GET,
    
    /**
     * This flag being present means that {@link IRegisterer}s created by the {@link IRegistry#createRegisterer(Key, String)} methods can bypass the frozen flag
     */
    ALLOW_REGISTERERS_BYPASS_FROZEN,
    
    /**
     * This flag being present means that {@link IRegisterer}s created by the {@link IRegistry#createRegisterer(Key, String)} methods can bypass the replacing flag
     */
    ALLOW_REGISTERERS_BYPASS_REPLACING
}