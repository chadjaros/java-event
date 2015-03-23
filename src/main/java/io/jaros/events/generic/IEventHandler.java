package io.jaros.events.generic;

import io.jaros.events.EventArgs;


/**
 * This defines the format of an event handler. It consists of a single method that takes two parameters - 
 * sender and args. The sender is the object who published an event, and the args are the data payload
 * that were published. Instances of IEventHandler may be added to the observer list within an EventManager
 * through the EventManager itself, or via the subscribe methods on the child Event object. When the raise()
 * method is called on the EventManager, all of the IEventHandlers in the observer list of that EventManager
 * will have their handleEvent method called with the specified parameters.
 * 
 * @author cjaros
 *
 * @param &lt;T> extends EventArgs - the object type that this event handler will accept as a data payload
 */
public interface IEventHandler<T extends EventArgs> {

    /**
     * This method is called by an EventManager to which this handler has been added when the event
     * is raised. The sender will be the object who is raising the event, and the args will be the data
     * payload that is being published. 
     * 
     * @param sender - the object who is raised the event
     * @param args - the event arguments containing the data payload
     */
    void handleEvent(Object sender, T args);
}
