package com.stardevllc.registry;

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
     * This flag being present allows creation of child registries
     */
    CHILD_REGISTRIES,
    
    /**
     * This flag being present means that the bulk clear action can be performed
     */
    CLEARING,
    
    /**
     * This flag being present means that the registry will check partial keys in the get method
     */
    CHECK_PARTIAL_IN_GET,
    
    /**
     * This being present means that the current registry's key is added to the full path of a registered item to the parent
     */
    APPEND_KEY_TO_OBJECT_TO_PARENT,
    
    /**
     * This being present means that the registration to this registry will fail on parent registration failure
     */
    FAIL_ON_PARENT_REGISTER_FAILURE
}