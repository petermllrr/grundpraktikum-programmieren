package io.petermueller.petrinetz.models.petrinet;

/**
 * A location represents a node in a Petri net. It's used as a base class for
 * both {@link Place Places} and {@link Transition Transitions} and hold basic
 * attributes common to both types.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class Location {
	/**
	 * A unique id.
	 */
	public String id;
	/**
	 * A human-readable name. Doesn't have to be unique.
	 */
	public String name;
	/**
	 * The x-coordinate of the location.
	 */
	public int x;
	/**
	 * The y-coordinate of the location.
	 */
	public int y;
}
