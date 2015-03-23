package io.jaros.events.util;

import io.jaros.events.EventArgs;
import io.jaros.events.generic.IEventHandler;

public class CollectedEvent {
    
    @SuppressWarnings("rawtypes")
    public CollectedEvent(IEventHandler handler, Object sender, EventArgs args) {
        this.handler = handler;
        this.sender = sender;
        this.args = args;
    }
    
    @SuppressWarnings("rawtypes")
    public IEventHandler handler;
    public Object sender;
    public EventArgs args;
}
