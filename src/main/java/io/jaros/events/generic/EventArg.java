package io.jaros.events.generic;

import io.jaros.events.EventArgs;

/**
 * This implements a simple event argument that can pass a single type safe value to 
 * listeners of an event.
 * 
 * @author cjaros
 *
 * @param &lt;T> - The data type for the single value being passed by this event arg
 */
public class EventArg<T> extends EventArgs {

    private T _value;
    
    /**
     * Creates an instance of this EventArg class with the supplied value
     * 
     * @param value
     */
    public EventArg(T value) {
        _value = value;
    }
    
    /**
     * @return the value
     */
    public T getValue() {
        return _value;
    }    
}
