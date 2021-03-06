package io.petermueller.petrinetz.models.petrinet;

/**
 * A directed arc pointing from one location to another. An {@code Arc} holds
 * references to a source and target {@link Location}.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class Arc {
	/**
	 * Unique id of the arc.
	 */
	public String id;
	/**
	 * Reference to the source {@link Location}.
	 */
	public Location source;
	/**
	 * Reference to the target {@link Location}.
	 */
	public Location target;

	/**
	 * Initializes an arc.
	 * 
	 * @param id a unique id
	 * @param source a reference to the source {@link Location}
	 * @param target a reference to the target {@link Location}
	 */
	public Arc(String id, Location source, Location target) {
		this.id = id;
		this.source = source;
		this.target = target;
	}
}
