package io.jaros.events;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import io.jaros.events.generic.IEventHandler;


/**
 * This class is an implementation of a generic observer pattern for java objects. 
 * It is modeled after the way native C# events function. The EventManager maintains
 * the list of observers, and allows others to publish events to those observers 
 * via the <code>raise(Object sender, T args)</code> method. The EventManager is
 * generic, with the generic parameter being a payload of information that will
 * be sent to the observers when an event is raised.
 * 
 * For more information on how to use this system, go to: 
 * https://share.amientertainment.com/share/page/site/music/wiki-page?title=Java_EventManager_Guide
 * and check out io.jaros.events.test.util.NotifyingInteger
 * 
 * @author cjaros
 *
 * @param &lt;T&gt; extends EventArgs - the object type that will carry data to observers of this event 
 */
public class EventManager<T extends EventArgs> {

    private ArrayList<Reference<IEventHandler<T>>> _handlers;
    private Event<T> _event;
    
    /**
     * Creates a new instance of EventManager
     */
    public EventManager() {
        _handlers = new ArrayList<>();
        _event = new Event<>(this);    
    }

    /**
     * Adds an event handler instance to this EventManager as a SoftReference. SoftReferences will prevent 
     * the handler object from being garbage collected for the most part, so they are much less likely than a 
     * WeakReference to be gc'd without notification. For more information, see SoftReference in the Java API 
     * documentation.
     * 
     * If you add as a SoftReference, you should take care to remove the handler before the object goes out of
     * scope to avoid leaking memory.
     * 
     * @param handler - the handler to be added as an observer of this event 
     */
    public void addHandler(IEventHandler<T> handler) {
        addHandler(handler, false);
    }
        
    /**
     * Adds an event handler instance to this EventManager as either a WeakReference or a SoftReference. 
     * WeakReferences will not prevent the handler object from being garbage collected, so they may be 
     * removed from the handler without any notification if no other strong references to the handler 
     * remain. SoftReferences will prevent the handler object from being garbage collected for the most 
     * part, so they are much less likely to be gc'd without notification. For more information, see SoftReference 
     * in the Java API documentation.
     * 
     * If you add as a SoftReference, you should take care to remove the handler before the object goes out of
     * scope to avoid leaking memory. If you add as a WeakReference, you should make sure to keep a reference
     * to the handler active elsewhere so that it doesn't get garbage collected.
     * 
     * @param handler - the handler to be added as an observer of this event
     * @param weakref - If true, adds handler as a WeakReference. Otherwise, adds handler as a SoftReference
     */
    public void addHandler(IEventHandler<T> handler, boolean weakref) {
        // Add handlers in a synchronized block so we don't
        // stomp on other operations that could be happening
        // concurrently
        synchronized(_event) {
            if(weakref)
                _handlers.add(new WeakReference<IEventHandler<T>>(handler));
            else
                _handlers.add(new SoftReference<IEventHandler<T>>(handler));
        }
    }

    /**
     * Removes a handler instance from this EventManager.
     * 
     * @param handler - the handler to be removed as an observer of this event
     */
    public void removeHandler(IEventHandler<T> handler) {        
        // Remove handlers in a synchronized block so we don't
        // stomp on other operations that could be happening
        // concurrently
        synchronized(_event) {            
            for(Iterator<Reference<IEventHandler<T>>> it = _handlers.iterator(); it.hasNext(); ) {
                IEventHandler<T> itemRef = it.next().get();
                
                if(itemRef == null || handler == itemRef) {
                    it.remove();
                }
            }
        }
    }

    
    /**
     * This publishes the event, calling each of the event handlers in order with
     * the supplied parameters.
     * 
     * @param sender - the object who is raising the event
     * @param args - the event arguments containing the data payload
     */
    public void raise(Object sender, T args) {
        ArrayList<IEventHandler<T>> targets = new ArrayList<>();
        
        // Do a synchronized iteration through the handlers list to find
        // the active handlers who are subscribed
        synchronized(_event) {
            for(Iterator<Reference<IEventHandler<T>>> it = _handlers.iterator(); it.hasNext(); ) {
                IEventHandler<T> itemRef = it.next().get();
                
                if(itemRef == null)
                    it.remove();
                else
                    targets.add(itemRef);
            }
        }
        
        // Publish the event to each handler outside of the
        // synchronized block so we don't block other threads
        // during event handling
        for(IEventHandler<T> handler: targets) {
            handler.handleEvent(sender, args);
        }
    }
    
    /**
     * Clears all the handlers from the list.
     */
    public void clearHandlers() {
        synchronized(_event) {
            _handlers.clear();
        }
    }
    
    /**
     * This provides access to a restricted event object that can only subscribe to and
     * unsubscribe from this EventManager instance.
     * 
     * @return an Event&lt;T&gt; object with restricted access to the event.
     */
    public Event<T> getEvent() {
        return _event;
    }    
        
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
