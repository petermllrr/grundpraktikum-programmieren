package io.petermueller.petrinetz.models.rgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import io.petermueller.petrinetz.models.petrinet.Place;

/**
 * A marking represents the position and count of all markers currently
 * present in a Petri net.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class Marking {
	/**
	 * Unique id of the marking.
	 */
	public String id;
	/**
	 * True, if this marking is the one currently active in the Petri net.
	 */
	public boolean isActive;
	/**
	 * True, if this marking is the root note of a reachability graph.
	 */
	public boolean isRoot;
	/**
	 * List of {@link Place Places} and their corresponding marker count.
	 */
	public List<Place> places;
	/**
	 * True, if this marking is the start of the detection path of a
	 * boundedness analysis. 
	 */
	public boolean isPathStart;
	/**
	 * True, if this marking is the end of the detection path of a boundedness
	 * analysis.
	 */
	public boolean isPathEnd;
	
	/**
	 * Initializes a marking object from a list of {@link Place Places}.
	 * 
	 * @param inputPlaces list of {@link Place Places} which holds the current
	 *                    position and amount of markers
	 */
	public Marking(List<Place> inputPlaces) {
		this.isActive = false;
		init(inputPlaces);
	}

	/**
	 * Initializes a marking object from a list of {@link Place Places}. This
	 * constructor also offers the option to set the marking as the currently
	 * {@link Marking#isActive active marking}.
	 * 
	 * @param isActive    set to true, if this marking is the currently active
	 *                    marking of a Petri net.
	 * @param inputPlaces list of {@link Place Places} which holds the current
	 *                    position and amount of markers
	 */
	public Marking(boolean isActive, List<Place> inputPlaces) {
		this.isActive = isActive;
		init(inputPlaces);
	}

	/**
	 * Helper method to reduce code repetition in the constructor.
	 * 
	 * @param inputPlaces list of {@link Place Places} which holds the current
	 *                    position and amount of markers
	 */
	private void init(List<Place> inputPlaces) {
		this.isRoot = false;
		this.places = new ArrayList<Place>(inputPlaces.size());
		this.isPathStart = false;
		this.isPathEnd = false;
		this.id = generateMarkingId(inputPlaces);
		loadPlaces(inputPlaces);
	}
	
	/**
	 * Inserts a new marking into the datastructure.
	 * 
	 * @param inputPlaces a list of {@link Place Places}
	 */
	private void loadPlaces(List<Place> inputPlaces) {
		for (Place inputPlace : inputPlaces) {
			Place copy = new Place(inputPlace.id);
			copy.currentTokens = inputPlace.currentTokens;
			this.places.add(copy);
		}
	}

	/**
	 * Generates a marking id from all {@link Place Places} and marker counts
	 * of a {@link PetriNetModel Petri net}.
	 * 
	 * @param places a list of {@link Place Places}
	 * @return the generated id
	 */
	private String generateMarkingId(List<Place> places) {
		sortPlaces(places);
		String id = "(";
		for (Place place : places) {
			id = id + place.currentTokens + "|";
		}
		id = id.substring(0, id.length() - 1);
		id = id + ")";
		return id;
	};

	/**
	 * Sorts a list of {@link Place Places} alphabetically.
	 * 
	 * @param places a list of {@link Place Places}
	 */
	private void sortPlaces(List<Place> places) {
		Collections.sort(places, new Comparator<Place>() {
			@Override
			public int compare(Place o1, Place o2) {
				return o1.id.compareTo(o2.id);
			}
		});
	}
}