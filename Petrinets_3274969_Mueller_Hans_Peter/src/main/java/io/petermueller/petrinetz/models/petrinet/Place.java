package io.petermueller.petrinetz.models.petrinet;

/**
 * Represents a place in the {@link PetriNetModel}. A place hold the current
 * amount of markers that is present as well as the originally set start amount.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class Place extends Location {
	/**
	 * Current amount of markers.
	 */
	public int currentTokens;
	/**
	 * Original amount of markers.
	 */
	public int startTokens;

	/**
	 * Initializes a place with 0 markers.
	 * @param id the unique id of the place.
	 */
	public Place(String id) {
		this.id = id;
		this.currentTokens = 0;
		this.startTokens = 0;
	}
}
