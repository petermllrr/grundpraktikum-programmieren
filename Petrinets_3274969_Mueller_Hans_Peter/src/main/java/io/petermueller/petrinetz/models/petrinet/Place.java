package io.petermueller.petrinetz.models.petrinet;

public class Place extends Location {
	public int tokens;
	
	public Place(String id) {
		this.id = id;
		this.tokens = 0;
	}
}
