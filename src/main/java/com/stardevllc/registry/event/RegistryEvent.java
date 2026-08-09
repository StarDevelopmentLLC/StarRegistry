package com.stardevllc.registry.event;

import com.stardevllc.registry.IRegistry;

public abstract class RegistryEvent<V> {
    
    protected final IRegistry<V> registry;
    protected boolean cancelled;
    
    public RegistryEvent(IRegistry<V> registry) {
        this.registry = registry;
    }
    
    /**
     * The registry associated with the event
     *
     * @return The registry
     */
    public IRegistry<V> getRegistry() {
        return registry;
    }
    
    /**
     * Checks to see if the event has been cancelled. <br>
     * Not all events support this
     *
     * @return If the event is cancelled, or false if it is not supported
     */
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    /**
     * Sets the cancelled flag of the event
     *
     * @param cancelled The status to set the cancellation flag to
     * @throws UnsupportedOperationException If the event doesn't support cancellation
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}