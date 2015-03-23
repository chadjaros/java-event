package io.jaros.events;

import io.jaros.events.generic.IEventHandler;


/**
 * This is a restricted access child of the EventManager object. It provides access
 * for subscribing to and unsubscribing from the owner. It cannot raise the event or
 * clear all the children.
 * 
 * For more information on how to use this system, see README.md
 * 
 * @author cjaros
 *
 * @param &lt;T&gt; extends EventArgs - 
 */
public class Event<T extends EventArgs> {

    private EventManager<T> _owner;
    
    /**
     * Creates a new Event instance that will provide restricted access for the
     * supplied EventManager instance.
     * 
     * @param owner
     */
    protected Event(EventManager<T> owner) {
        _owner = owner;
    }
    
    /**
     * Adds an event handler instance to this Event as a SoftReference. SoftReferences will prevent 
     * the handler object from being garbage collected for the most part, so they are much less likely than a 
     * WeakReference to be gc'd without notification. For more information, see SoftReference in the Java API 
     * documentation.
     * 
     * If you add as a SoftReference, you should take care to remove the handler before the object goes out of
     * scope to avoid leaking memory.
     * 
     * @param handler - the handler to be added as an observer of this event 
     */
    public void subscribe(IEventHandler<T> handler) {
        _owner.addHandler(handler, false);
    }
    
    /**
     * Adds an event handler instance to this Event as a WeakReference. WeakReferences will not prevent the 
     * handler object from being garbage collected, so they may be removed from the handler without any notification 
     * if no other strong references to the handler remain. For more information, see WeakReference 
     * in the Java API documentation.
     * 
     * If you add as a WeakReference, you should make sure to keep a reference to the handler active elsewhere so 
     * that it doesn't get garbage collected.
     * 
     * @param handler - the handler to be added as an observer of this event
     */
    public void subscribeWeak(IEventHandler<T> handler) {
        _owner.addHandler(handler, true);
    }

    /**
     * Removes a handler instance from this Event.
     * 
     * @param handler - the handler to be removed as an observer of this event
     */
    public void unsubscribe(IEventHandler<T> handler) {
        _owner.removeHandler(handler);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
