package io.petermueller.petrinetz.util;

/**
 * Interface for event listeners, allowing them to register callbacks once an
 * event has been fired.
 * 
 * @author Hans Peter Müller (3274969)
 * @see    EventEmitter
 * @see    Event
 */
public interface EventListener {
	/**
	 * Is called when an event is fired. The type of the event is passed on as
	 * a parameter.
	 * 
	 * @param eventType the type of the fired event
	 */
	public void event(Event eventType);
}
