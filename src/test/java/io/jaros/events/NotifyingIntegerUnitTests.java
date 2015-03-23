package io.jaros.events;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.jaros.events.generic.EventArg;
import io.jaros.events.generic.IEventHandler;
import io.jaros.events.util.CollectedEvent;
import io.jaros.events.util.NotifyingInteger;

public class NotifyingIntegerUnitTests {

    private NotifyingInteger _value;
    private ArrayList<CollectedEvent> _eventCollector;
    
    @Before
    public void setUp() {
        _value = new NotifyingInteger();
        _eventCollector = new ArrayList<>();
    }
    
    @After
    public void tearDown() {
        
    }
    
    /**
     * This defines an event handler for the ValueChanged event of _value. This can be treated as a 
     * private member method of a sort. 
     */
    private final IEventHandler<EventArg<Integer>> _value_ValueChanged = new IEventHandler<EventArg<Integer>>() {        
        @Override
        public void handleEvent(Object sender, EventArg<Integer> args) {
            _eventCollector.add(new CollectedEvent(this, sender, args));
        }
    }; 
    
    @Test
    public void testSetupAndTeardown() {
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testSubscribeAndModify() {
        
        _value.setValue(4);
        assertEquals(4, _value.getValue());

        assertEquals(0, _eventCollector.size());
        
        _value.eValueChanged().subscribe(_value_ValueChanged);
        
        _value.setValue(5);
        assertEquals(5, _value.getValue());
        
        assertEquals(1, _eventCollector.size());
        assertEquals(_value, _eventCollector.get(0).sender);
        assertEquals(5, (int)((EventArg<Integer>)_eventCollector.get(0).args).getValue());
        assertEquals(_value_ValueChanged, _eventCollector.get(0).handler);

        _value.eValueChanged().unsubscribe(_value_ValueChanged);
        
        _value.setValue(6);
        assertEquals(6, _value.getValue());

        assertEquals(1, _eventCollector.size());
    }
    
    @Test
    public void testModify() {
        _value.setValue(4);
        assertEquals(4, _value.getValue());

        _value.setValue(5);
        assertEquals(5, _value.getValue());
    }
}
