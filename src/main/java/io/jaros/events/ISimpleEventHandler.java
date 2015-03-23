package io.jaros.events;

import io.jaros.events.EventArgs;
import io.jaros.events.generic.IEventHandler;

/**
 * Provides a straightforward way to define an event handler that passes no additional data.
 * 
 * @author cjaros
 */
public interface ISimpleEventHandler extends IEventHandler<EventArgs> {

}
