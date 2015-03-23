package io.jaros.events.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

import io.jaros.events.EventArgs;
import io.jaros.events.generic.IEventHandler;

/**
 * This class is a helper class to gather and wait for future events emitted from
 * an object. This is primarily used for unit testing
 * 
 * @author cjaros
 *
 * @param <T>
 */
public class EventCollector<T extends EventArgs> {

    /**
     * Simple class used for capturing an emitted event
     * 
     * @author cjaros
     */
    public class Event {
        Event(Object o, T e) {
            sender = o;
            args = e;
        }
        
        public Object sender;
        public T args;
    }
    
    public ArrayList<Event> _events;

    /**
     * Create a new instance
     */
    public EventCollector()
    {
        _events = new ArrayList<>();
    }

    /**
     * Add an event to the collection
     * 
     * @param sender
     * @param args
     */
    public void collect(Object sender, T args)
    {
        _events.add(new Event(sender, args));
    }

    /**
     * @return a simple event handler that adds the emitted events to the collection
     */
    public IEventHandler<T> handler() {
        return collector;
    }
    
    private final IEventHandler<T> collector = new IEventHandler<T>() {

        @Override
        public void handleEvent(Object sender, T args) {
            collect(sender, args);
        }        
    };
    
    /**
     * Clear the events 
     */
    public void clear()
    {
        _events.clear();
    }

    /**
     * @return the last event in the list, null if the list is empty
     */
    public Event last()
    {
        if(_events.isEmpty())
            return null;
        
        return _events.get(_events.size() - 1);
    }

    /**
     * @return the size of the collection
     */
    public int size()
    {
        return _events.size();
    }

    /**
     * @param index
     * @return the event at the specified instance
     */
    public Event get(int index)
    {
        return _events.get(index);        
    }

    /**
     * Waits for the nth event to be collected. Returns after the nth event is collected. Throws a timeout
     * exception if the timeout elapses before the nth event is collected.
     * 
     * @param n
     * @param timeout
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public void waitForNthEvent(int n, int timeout) throws TimeoutException, InterruptedException
    {        
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MILLISECOND, timeout);
        while (n > _events.size())
        {
            if (Calendar.getInstance().after(end))
                throw new TimeoutException("timed out waiting for event");
            Thread.sleep(50);
        }
    }
}
