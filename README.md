# java-event
C# like events for Java

This java-event system is an implementation of a generic observer pattern for java objects. It is modeled after the way C# events function. The EventManager maintains the list of observers, and allows others to publish events to those observers via the raise(Object sender, T args) method. The EventManager is generic, with the generic parameter being a payload of information that will be sent to the observers when an event is raised.

### Background on C# events
- [http://msdn.microsoft.com/en-us/library/8627sbea.aspx](http://msdn.microsoft.com/en-us/library/8627sbea.aspx)
- [http://msdn.microsoft.com/en-us/library/aa645739.aspx](http://msdn.microsoft.com/en-us/library/aa645739.aspx)
- [http://msdn.microsoft.com/en-us/library/awbftdfh.aspx](http://msdn.microsoft.com/en-us/library/awbftdfh.aspx)

### EventArgs

EventArgs is the base class to be used for information that is passed via events. You should create subclasses of EventArgs if you want to pass information through an event to an event handler

### IEventHandler<T>

The IEventHandler interface defines objects that can handle events. The <T> in this case is a type that extends EventArgs, representing the payload passed from the event publisher to the subscriber. IEventHandler defines a single method: handleEvent(Object sender, T args) where sender is the object that emitted the event and args are the event arguments.

### EventManager<T>

The EventManager is the meat an potatoes of the design. It provides a way to subscribe, unsubscribe, raise, and clear handlers for an event. This is typically instantiated as a private member of a class that will be event-driven. EventManager also has an Event<T> member that provides a restricted interface suitable for public access.

###Event<T>

The Event class defines a restricted interface that can be used to publicly provide subscription functionality while hiding the abillity to raise or clear handlers for an event. This is typically what peers of an event-driven class will use to manage subscriptions.

##Code Explanation and Examples

Typical implementation of a class that uses an event:

	// This event zaps when you call the zapNow() method;
	public class Zapper {

        // The EventManager instance should typically be a private member, you won't usually
        // want outsiders raising your events.
        private EventManager<EventArgs> _zapEvent;

        public Zapper() {
                _zapEvent = new EventManager<>();
        }

        // This provides public access to the restricted interface of the Zap event
        // Events can use the convention of using a lowercase e before the event name
        // to indicate it is an event.
        public Event<EventArgs> eZap() {
                return _zapEvent.getEvent();
        }
        
        // An internal method such as this is generally used to raise an event
        private void onZap() {
                try {
                        _zapEvent.raise(this, new EventArgs());
                }
                catch (Exception ex) {
                        // Do something here. Event handlers should not throw exceptions, but if they do
                        // you will probably want to catch and log it rather than have it throw off the execution order
                        // of your program
                }
        } 

        public void zapNow() {
                onZap();
        } 
	}

Typical implementation of a class that could interact with the Zapper:
	
	// This class is designed to log a statement when it receives a zap from the Zapper class
	public class GetsZapped {

        private static final Logger _log = LoggerFactory.getLogger(GetsZapped.class);

        // This defines an event handler for the zap event. Notice that the generic type of the
        // handler matches the generic type of the event. Marking the member as final makes 
        // it act as sort of a member delegate function
        public final IEventHandler<EventArgs> zapHandler = new IEventHandler<EventArgs() {

                @Override
                public void handleEvent(Object sender, EventArgs args) {
                        _log.info("I've been zapped!");
                }
        };
	}
	 
Connect the two objects and let them talk:

    Zapper zapper = new Zapper();

    GetsZapped getZap = new GetsZapped();

    // This will cause nothing to be logged, no subscription has been set up
    zapper.zapNow();

    // Subscribe getZap to the zapper's zap event
    zapper.eZap().subscribe(getZap.zapHandler);

    // Now the message "I've been zapped" will be logged
    zapper.zapNow();

    // Unsubscribe getZap to the zapper's zap event
    zapper.eZap().unsubscribe(getZap.zapHandler);

    // This will not cause the message to be logged, the handler has been unsubscribed
    zapper.zapNow();

This will output:

	I've been Zapped
	
##Subscriptions - Weak and Strong

The event system has been set up with Weak and Strong subscriptions. Typically strong subscriptions should be used, but there are sometimes cases where weak subscriptions may be necessary to avoid memory leaks. You will typically only need to use weak subscriptions if you cannot ensure that an object will unsubscribe from an event reliably.

Event handler instances can be subscribed to through the EventManager instances as either a WeakReference or a SoftReference. WeakReferences will not prevent the handler object from being garbage collected, so they may be removed from the handler without any notification if no other strong references to the handler remain. SoftReferences will prevent the handler object from being garbage collected for the most part, so they are much less likely to be gc'd without notification. For more information, see WeakReference and SoftReference in the Java API documentation.  

If you add as a SoftReference, you should take care to remove the handler before the object goes out of scope to avoid leaking memory. If you add as a WeakReference, you should make sure to keep a reference to the handler active elsewhere so that it doesn't get garbage collected.