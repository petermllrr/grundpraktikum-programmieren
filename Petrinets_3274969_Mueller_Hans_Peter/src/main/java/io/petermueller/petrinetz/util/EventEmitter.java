package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes which extend this abstract class are able to attach event listeners
 * to themselves and notify them by firing events.
 * 
 * @author Hans Peter Müller (3274969)
 * @see    Event
 * @see    EventListener
 */
public abstract class EventEmitter {
	private List<EventListener> listeners = new ArrayList<EventListener>();

	/**
	 * Attaches an {@link EventListener} to this object.
	 * 
	 * @param listener the listener
	 */
	public void addEventListener(EventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Notifies all attached event listeners that an Event has been fired.
	 * 
	 * @param eventType the type of event
	 * @see   Event}
	 * @see   EventListener
	 */
	public void fireEvent(Event eventType) {
		for (EventListener listener : listeners) {
			listener.event(eventType);
		}
	}
}
