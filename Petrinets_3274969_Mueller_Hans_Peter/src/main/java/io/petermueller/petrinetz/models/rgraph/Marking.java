package io.petermueller.petrinetz.models.rgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.petermueller.petrinetz.models.petrinet.Place;

public class Marking {
	public String id;
	public boolean isActive;
	public boolean isRoot;
	public List<Place> places;
	public boolean isPathStart;
	public boolean isPathEnd;

	public Marking(boolean isActive, List<Place> inputPlaces) {
		this.isActive = isActive;
		this.isRoot = false;
		this.places = new ArrayList<Place>(inputPlaces.size());
		this.isPathStart = false;
		this.isPathEnd = false;
		this.id = generateMarkingId(inputPlaces);
		loadPlaces(inputPlaces);
	}

	public Marking(List<Place> inputPlaces) {
		this.id = generateMarkingId(inputPlaces);
		this.places = new ArrayList<Place>(inputPlaces.size());
		loadPlaces(inputPlaces);
	}

	private void loadPlaces(List<Place> inputPlaces) {
		for (Place inputPlace : inputPlaces) {
			Place copy = new Place(inputPlace.id);
			copy.currentTokens = inputPlace.currentTokens;
			this.places.add(copy);
		}
	}

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

	private void sortPlaces(List<Place> places) {
		Collections.sort(places, new Comparator<Place>() {
			@Override
			public int compare(Place o1, Place o2) {
				return o1.id.compareTo(o2.id);
			}
		});
	}
}