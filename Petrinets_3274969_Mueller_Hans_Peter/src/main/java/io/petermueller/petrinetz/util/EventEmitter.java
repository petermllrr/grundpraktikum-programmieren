package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;

public abstract class EventEmitter {
	List<EventListener> listeners = new ArrayList<EventListener>();

	public void addEventListener(EventListener listener) {
		listeners.add(listener);
	}

	public void fireEvent(Event eventType) {
		for (EventListener listener : listeners) {
			listener.event(eventType);
		}
	}
}