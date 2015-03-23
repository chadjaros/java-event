package io.jaros.events.util;

import io.jaros.events.Event;
import io.jaros.events.EventManager;
import io.jaros.events.generic.EventArg;


/**
 * This class is an example of how the Event system can be incorporated into an OO
 * style design.
 * 
 * @author cjaros
 *
 */
public class NotifyingInteger {

    private int _value;    
    private EventManager<EventArg<Integer>> _valueChanged;
    
    public NotifyingInteger() {
        _value = 0;
        _valueChanged = new EventManager<>();
    }

    public int getValue() {
        return _value;
    }

    public void setValue(int value) {
        this._value = value;
        onValueChanged(value);
    }
    
    /**
     * Private method wraps raising the event in such a way that if an event handler
     * behaves badly (such as throws an exception), it will not break execution of
     * higher level code and can be logged appropriately.
     * 
     * @param value
     */
    private void onValueChanged(int value) {
        try {
            _valueChanged.raise(this, new EventArg<Integer>(value));
        }
        catch(Exception ex) {
            // You could log something here
        }    
    }
    
    /**
     * This provides outside access to the publish and subscribe members of the _valueChanged event
     * @return the event from the _valueChanged member
     */
    public Event<EventArg<Integer>> eValueChanged() {
        return _valueChanged.getEvent();
    }
}
