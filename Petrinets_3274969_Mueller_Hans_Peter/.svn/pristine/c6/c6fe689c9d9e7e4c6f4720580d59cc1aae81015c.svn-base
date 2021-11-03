package io.petermueller.petrinetz.models.rgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.petrinet.Place;
import io.petermueller.petrinetz.models.petrinet.Transition;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventEmitter;
import io.petermueller.petrinetz.util.EventListener;

public class RGraphModel extends EventEmitter {
	public List<Marking> markings;
	public List<TransitionArc> arcs;
	private PetriNetModel petriNetModel;
	private FileSystemModel fileSystemModel;

	public RGraphModel(PetriNetModel petriNet) {
		this.markings = new ArrayList<Marking>();
		this.arcs = new ArrayList<TransitionArc>();
		this.petriNetModel = petriNet;
		this.addMarking(petriNetModel.places);
		addPetriNetEventListener();
	}

	public RGraphModel(
			PetriNetModel petriNet,
			FileSystemModel fileSystemModel) {
		this.markings = new ArrayList<Marking>();
		this.arcs = new ArrayList<TransitionArc>();
		this.petriNetModel = petriNet;
		this.fileSystemModel = fileSystemModel;
		this.addMarking(petriNetModel.places);
		addPetriNetEventListener();
		addFileSystemEventListener();
	}

	public void addTransition(
			List<Place> places,
			Transition lastTransition) {
		Marking source = getActiveMarking();
		addMarking(places);
		addArc(lastTransition, source);
	}

	public void addMarking(List<Place> places) {
		String id = generateMarkingIdString(places);
		if (!markingExists(id)) {
			Marking marking = new Marking(true, places);
			markings.add(marking);
			setAsActiveMarking(marking);
		} else {
			setAsActiveMarking(getMarkingById(id));
		}
	}

	public void reset(List<Place> places) {
		markings = new ArrayList<Marking>();
		arcs = new ArrayList<TransitionArc>();
		addMarking(places);
		setAsRoot(places);
		fireEvent(Event.RGRAPH_RESET);
	}

	public void setToMarking(List<Place> places) {
		String id = generateMarkingIdString(places);
		if (markingExists(id)) {
			setAsActiveMarking(getMarkingById(id));
			removeLatestTransition();
		} 
		else {
			Marking marking = new Marking(true, places);
			markings.add(marking);
			setAsActiveMarking(marking);
			removeLatestTransition();
		}
		fireEvent(Event.RGRAPH_SET_TO_MARKING);
	}

	public Marking getMarkingById(String id) {
		for (Marking marking : markings) {
			if (marking.id.equals(id)) {
				return marking;
			}
		}
		return null;
	}

	public void load(RGraphModel inputRGraph) {
		markings = new ArrayList<Marking>();
		arcs = new ArrayList<TransitionArc>();
		for (Marking marking : inputRGraph.markings) {
			this.markings.add(marking);
		}
		for (TransitionArc arc : inputRGraph.arcs) {
			this.arcs.add(arc);
		}
		fireEvent(Event.RGRAPH_NEW_GRAPH_LOADED);
	}

	private Marking getActiveMarking() {
		for (Marking marking : markings) {
			if (marking.isActive) {
				return marking;
			}
		}
		return null;
	}

	private void setAsRoot(List<Place> places) {
		Marking root = getMarkingById(generateMarkingIdString(places));
		root.isRoot = true;
	}

	private void addArc(Transition lastTransition, Marking source) {
		String id = generateTransitionIdString(lastTransition);
		String shortId = lastTransition.id;
		Marking target = getActiveMarking();
		if (!transitionExists(id, source, target)) {
			TransitionArc arc = new TransitionArc(id, shortId, source, target);
			arcs.add(arc);
			setAsLastTransition(arc);
		} else {
			setAsLastTransition(getArcById(id, source, target));
		}

	}

	private String generateMarkingIdString(List<Place> places) {
		sortPlaces(places);
		String id = "(";
		for (Place place : places) {
			id = id + place.currentTokens + "|";
		}
		id = id.substring(0, id.length() - 1);
		id = id + ")";
		return id;
	};

	private String generateTransitionIdString(Transition lastTransition) {
		return "[" + lastTransition.id + "] " + lastTransition.name;
	}

	private void sortPlaces(List<Place> places) {
		Collections.sort(places, new Comparator<Place>() {
			@Override
			public int compare(Place o1, Place o2) {
				return o1.id.compareTo(o2.id);
			}
		});
	}

	private boolean markingExists(String id) {
		if (getMarkingById(id) != null) {
			return true;
		}
		return false;
	}

	private boolean transitionExists(
			String id,
			Marking source,
			Marking target) {
		if (getArcById(id, source, target) != null) {
			return true;
		} else {
			return false;
		}
	}


	private TransitionArc getArcById(
			String id,
			Marking source,
			Marking target) {
		for (TransitionArc arc : arcs) {
			if (arc.id.equals(id) &&
					arc.source == source &&
					arc.target == target) {
				return arc;
			}
		}
		return null;
	}

	private void setAsActiveMarking(Marking activeMarking) {
		for (Marking marking : markings) {
			if (marking.id.equals(activeMarking.id)) {
				marking.isActive = true;
			} else {
				marking.isActive = false;
			}
		}
	}

	private void setAsLastTransition(TransitionArc latestArc) {
		for (TransitionArc arc : arcs) {
			if (arc == latestArc) {
				arc.isLatest = true;
			} else {
				arc.isLatest = false;
			}
		}
	}

	private void removeLatestTransition() {
		for (TransitionArc arc : arcs) {
			arc.isLatest = false;
		} 
	}

	private void addPetriNetEventListener() {
		petriNetModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.PETRINET_TRANSITION_FIRED) {
					addTransition(
							petriNetModel.places,
							petriNetModel.lastTransition);
				}
				if (eventType == Event.PETRINET_MARKING_EDITED) {
					reset(petriNetModel.places);
				}
				if (eventType == Event.PETRINET_MARKING_RESET) {
					setToMarking(petriNetModel.places);
				}
			}
		});
	}

	private void addFileSystemEventListener() {
		fileSystemModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.FILESYSTEM_NEW_FILE_OPENED) {
					reset(petriNetModel.places);
				}
			}
		});
	}

}