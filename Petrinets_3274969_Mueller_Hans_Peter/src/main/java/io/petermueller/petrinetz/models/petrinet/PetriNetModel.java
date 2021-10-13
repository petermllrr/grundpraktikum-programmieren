package io.petermueller.petrinetz.models.petrinet;

import java.util.ArrayList;
import java.util.List;

public class PetriNetModel {
	public List<Transition> transitions;
	public List<Place> places;
	public List<Arc> arcs;
	
	public PetriNetModel() {
		transitions = new ArrayList<Transition>();
		places = new ArrayList<Place>();
		arcs = new ArrayList<Arc>();
	}
	
	public void clear() {
		transitions.clear();
		places.clear();
		arcs.clear();
	}
	
	public void addTransition(String id) {
		transitions.add(new Transition(id));
	}
	
	public void addPlace(String id) {
		places.add(new Place(id));
	}
	
	public void addArc(String id, String source, String target) {
		Location sourceObj = findLocation(source);
		Location targetObj = findLocation(target);
		arcs.add(new Arc(id, sourceObj, targetObj));
	}
	
	public void setName(String id, String name) {
		Location location = findLocation(id);
		location.name = name;
	}
	
	public void setTokens(String id, int tokens) {
		Location location = findLocation(id);
		Place place = (Place) location;
		place.tokens = tokens;
	}
	
	public void setPosition(String id, int x, int y) {
		Location location = findLocation(id);
		location.x = x;
		location.y = y;
	}
	
	public Location findLocation(String id) {
		Location foundObject = null;
		try {
			for (Transition transition : transitions) {
				if (transition.id.equals(id)) {
					foundObject = transition;
				}
			}
			for (Place place : places) {
				if (place.id.equals(id)) {
					foundObject = place;
				}
			}
			if (foundObject == null) {
				throw new Exception("Location " + id + " not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return foundObject;
	}
	
	public String getContentsAsString() {
		String text = "Transitions: ";
		for (Transition transition : transitions) {
			text = text + "<" + transition.id + ", "
					          + transition.name + ", x: "
					          + transition.x + ", y: "
					          + transition.y + ">, ";
		}
		text = text + "\n";
		text = text + "Places: ";
		for (Place place : places) {
			text = text + "<" + place.id + ", "
						      + place.name + " "
						      + place.tokens + ", x: "
							  + place.x + ", y: "
							  + place.y + ">, ";
		}
		text = text + "\n";
		text = text + "Arcs: ";
		for (Arc arc : arcs) {
			text = text + "<" + arc.id + ", "
							  + arc.source.id + ", "
							  + arc.target.id + ">, ";
		}
		return text;
	}
}