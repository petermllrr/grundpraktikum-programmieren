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
	public void event(Event eventType);
}
