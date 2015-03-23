package io.jaros.events;

import static org.junit.Assert.*;
import io.jaros.events.generic.IEventHandler;
import io.jaros.events.util.CollectedEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventManagerUnitTests {

    private EventManager<EventArgs> _event;
    
    private ArrayList<CollectedEvent> _eventCollector;
    
    @Before
    public void setUp() {
        _event = new EventManager<>();
        _eventCollector = new ArrayList<>();
    }
    
    @After
    public void tearDown() {
        
    }

    private final IEventHandler<EventArgs> _handler1 = new IEventHandler<EventArgs>() {
        
        @Override
        public void handleEvent(Object sender, EventArgs args) {
            _eventCollector.add(new CollectedEvent(this, sender, args));                
        }
    };
    
    private final ISimpleEventHandler _handler2 = new ISimpleEventHandler() {
        
        @Override
        public void handleEvent(Object sender, EventArgs args) {
            _eventCollector.add(new CollectedEvent(this, sender, args));                
        }
    };
    
    @Test
    public void testSetupAndTeardown() {
    
    }
    
    @Test
    public void testRaiseEvent() {
        _event.addHandler(_handler1);
        
        _event.raise(this, new EventArgs());
        
        assertEquals(1, _eventCollector.size());
        assertEquals(this, _eventCollector.get(0).sender);
        assertEquals(_handler1, _eventCollector.get(0).handler);
    }
    
    @Test
    public void testSubscribeRaiseEvent() {
        _event.getEvent().subscribe(_handler2);
        
        _event.raise(this, new EventArgs());
        
        assertEquals(1, _eventCollector.size());
        assertEquals(this, _eventCollector.get(0).sender);
        assertEquals(_handler2, _eventCollector.get(0).handler);
    }
    
    @Test
    public void testMultipleRaise() {
        _event.getEvent().subscribe(_handler1);
        _event.getEvent().subscribe(_handler2);
        
        _event.raise(this, new EventArgs());
        
        assertEquals(2, _eventCollector.size());
        assertEquals(this, _eventCollector.get(0).sender);
        assertEquals(_handler1, _eventCollector.get(0).handler);
        assertEquals(this, _eventCollector.get(1).sender);
        assertEquals(_handler2, _eventCollector.get(1).handler);
    }
    
    @Test
    public void testRaiseWithException() throws Exception {
        ISimpleEventHandler exceptionHandler = new ISimpleEventHandler() {            
            @Override
            public void handleEvent(Object sender, EventArgs args) {
                throw new RuntimeException("cause an error");                
            }
        };
        
        _event.getEvent().subscribe(exceptionHandler);
        _event.getEvent().subscribe(_handler1);
        _event.getEvent().subscribe(_handler2);
        
        try
        {
            // This should throw an exception because the first handler is built to do so. It will not
            // continue to raise the event on other registered handlers. For this reason, every effort
            // should be taken to prevent exceptions from being thrown in event handlers
            _event.raise(this, new EventArgs());

            throw new Exception("Fail");
        }
        catch(RuntimeException ex)
        {
            assertEquals("cause an error", ex.getMessage());
            // this is expected        
        }
        
        assertEquals(0, _eventCollector.size());
    }
    
    @Test
    public void testSubscribeAndUnsubscribe() {
        _event.getEvent().subscribe(_handler1);
        
        _event.raise(this, new EventArgs());
        
        assertEquals(1, _eventCollector.size());
        assertEquals(this, _eventCollector.get(0).sender);
        assertEquals(_handler1, _eventCollector.get(0).handler);
        
        _event.getEvent().subscribe(_handler2);
        
        _event.raise(_eventCollector, new EventArgs());

        assertEquals(3, _eventCollector.size());
        assertEquals(_eventCollector, _eventCollector.get(1).sender);
        assertEquals(_handler1, _eventCollector.get(1).handler);
        assertEquals(_eventCollector, _eventCollector.get(2).sender);
        assertEquals(_handler2, _eventCollector.get(2).handler);

        _event.getEvent().unsubscribe(_handler1);

        _event.raise(this, new EventArgs());

        assertEquals(4, _eventCollector.size());
        assertEquals(this, _eventCollector.get(3).sender);
        assertEquals(_handler2, _eventCollector.get(3).handler);
        
        _event.getEvent().unsubscribe(_handler2);
        
        _event.raise(this, new EventArgs());

        assertEquals(4, _eventCollector.size());
        
        // Should be a no-op
        _event.getEvent().unsubscribe(_handler1);
    }
    
    @Test
    public void testClearSubscriptions() {
        _event.getEvent().subscribe(_handler1);
        _event.getEvent().subscribe(_handler2);
        
        _event.raise(this, new EventArgs());
        
        assertEquals(2, _eventCollector.size());
        assertEquals(this, _eventCollector.get(0).sender);
        assertEquals(_handler1, _eventCollector.get(0).handler);
        assertEquals(this, _eventCollector.get(1).sender);
        assertEquals(_handler2, _eventCollector.get(1).handler);
        
        _event.clearHandlers();
        
        _event.raise(this, new EventArgs());
        
        assertEquals(2, _eventCollector.size());
        
    }
    
    @Test
    public void testSubscribeWeak() throws InterruptedException {
        
        ISimpleEventHandler seh = new ISimpleEventHandler() {            
            @Override
            public void handleEvent(Object sender, EventArgs args) {
                _eventCollector.add(new CollectedEvent(null, sender, args));
            }
        };
        
        WeakReference<ISimpleEventHandler> wr = new WeakReference<ISimpleEventHandler>(seh); 
        
        _event.getEvent().subscribeWeak(seh);
        
        _event.raise(this, new EventArgs());
        
        assertEquals(1, _eventCollector.size());
        assertEquals(this, _eventCollector.get(0).sender);
        assertEquals(null, _eventCollector.get(0).handler);
        
        seh = null;
        
        // GC, clear out the weak reference
        for(int i = 0; i < 5 && wr.get() != null; i++) {
            System.gc();
            Thread.sleep(100);
        }
        
        _event.raise(this, new EventArgs());
        
        // No event is published, the weak reference has been cleared out
        assertEquals(1, _eventCollector.size());
    }
    
    @Test
    public void testSubscribeStrong() throws InterruptedException {
        
        ISimpleEventHandler seh = new ISimpleEventHandler() {            
            @Override
            public void handleEvent(Object sender, EventArgs args) {
                _eventCollector.add(new CollectedEvent(null, sender, args));
            }
        };
        
        WeakReference<ISimpleEventHandler> wr = new WeakReference<ISimpleEventHandler>(seh); 
        
        _event.getEvent().subscribe(seh);
        
        _event.raise(this, new EventArgs());
        
        assertEquals(1, _eventCollector.size());
        assertEquals(this, _eventCollector.get(0).sender);
        assertEquals(null, _eventCollector.get(0).handler);
        
        seh = null;
        
        // GC, try to clear out references
        for(int i = 0; i < 5 && wr.get() != null; i++) {
            System.gc();
            Thread.sleep(100);
        }
        
        _event.raise(this, new EventArgs());
        
        // The event is published, the strong reference has not been cleaned up
        assertEquals(2, _eventCollector.size());
        assertEquals(this, _eventCollector.get(1).sender);
        assertEquals(null, _eventCollector.get(1).handler);
    }
    
}
