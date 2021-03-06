package io.petermueller.petrinetz.models.petrinet;

/**
 * Represents a transition in the {@link PetriNetModel}. A transition hols a
 * flag that signals if the transition is active or not.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class Transition extends Location {
	/**
	 * True if the transition is active and can be fired, else false.
	 */
	public boolean isEnabled;

	/**
	 * Initializes the Transition.
	 * 
	 * @param id a unique id
	 */
	public Transition(String id) {
		this.id = id;
	}
}