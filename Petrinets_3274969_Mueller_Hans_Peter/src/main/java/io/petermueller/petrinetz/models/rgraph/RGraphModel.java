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

/**
 * A datastructure of a single reachability graph. It consists of one or
 * multiple {@link Marking Markings} and {@link TransitionArc Arcs}. A marking
 * represents all markers in a Petri net while an arc represents the transition
 * between one marking into another by firing a transition in a Petri net. Each
 * reachability graph is always connected to a {@link PetriNetModel}.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class RGraphModel extends EventEmitter {
	/**
	 * List of all {@link Marking markings} in the reachability graph.
	 */
	public List<Marking> markings;
	/**
	 * List of all {@link TransitionArc arcs} in the reachability graph.
	 */
	public List<TransitionArc> arcs;
	private PetriNetModel petriNetModel;
	private FileSystemModel fileSystemModel;

	/**
	 * Initializes a reachability graph from a {@link PetriNetModel Petri net}.
	 * 
	 * @param petriNet a {@link PetriNetModel Petri net}
	 */
	public RGraphModel(PetriNetModel petriNet) {
		this.markings = new ArrayList<Marking>();
		this.arcs = new ArrayList<TransitionArc>();
		this.petriNetModel = petriNet;
		this.addMarking(petriNetModel.places);
		addPetriNetEventListener();
	}

	/**
	 * Initializes a reachability graph from a {@link PetriNetModel Petri net} 
	 * and a {@link FileSystemModel}. {@code RGraphModels} initialized with this
	 * constructor load a new reachability graph each time a new file is opened.
	 * 
	 * @param petriNet        a {@link PetriNetModel petri net}
	 * @param fileSystemModel a {@link FileSystemModel}
	 */
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

	/**
	 * Adds a new transition and a new {@link Marking} to the reachability 
	 * graph. In other words, this is what happens if a user fires a transition
	 * in a Petri net.
	 * 
	 * @param places         a list of {@link Place Places} representing the
	 *                       new {@link Marking}
	 * @param lastTransition reference to the last fired transition
	 */
	public void addTransition(
			List<Place> places,
			Transition lastTransition) {
		Marking source = getActiveMarking();
		addMarking(places);
		addArc(lastTransition, source);
	}

	/**
	 * Adds a new {@link Marking} to the reachability graph.
	 * 
	 * @param places a list of places representing the new {@link Marking}
	 */
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

	/**
	 * Clears the content of the reachability graph and adds the given
	 * {@link Marking} as the root of the new graph.
	 * 
	 * @param places the root of the new reachability graph
	 */
	public void reset(List<Place> places) {
		markings = new ArrayList<Marking>();
		arcs = new ArrayList<TransitionArc>();
		addMarking(places);
		setAsRoot(places);
		fireEvent(Event.RGRAPH_RESET);
	}

	/**
	 * Sets the given {@link Marking} as the currently active marking. If the
	 * marking doesn't exist, it will be created.
	 * 
	 * @param places the marking that should be set as active
	 */
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

	/**
	 * Returns a reference to the marking with the specified id.
	 * 
	 * @param id a marking id to look for
	 * @return a reference to the marking with the specified id, {@code null} if
	 *         no marking with this id was found
	 */
	public Marking getMarkingById(String id) {
		for (Marking marking : markings) {
			if (marking.id.equals(id)) {
				return marking;
			}
		}
		return null;
	}

	/**
	 * Clears the reachability graph and loads a new graph into the model.
	 * 
	 * @param inputRGraph the new graph to replace the old one
	 */
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
	
	/**
	 * Removes the active marking tag.
	 */
	public void removeActiveMarking() {
		for (Marking marking : markings) {
			marking.isActive = false;
		}
	}

	/**
	 * Returns the currently active marking of the reachability graph.
	 * 
	 * @return a reference to the currently active marking
	 */
	private Marking getActiveMarking() {
		for (Marking marking : markings) {
			if (marking.isActive) {
				return marking;
			}
		}
		return null;
	}

	/**
	 * Sets a marking as the root marking.
	 * 
	 * @param places a list of {@link Place places} that represents the marking
	 *               that should be set as the root marking
	 */
	private void setAsRoot(List<Place> places) {
		Marking root = getMarkingById(generateMarkingIdString(places));
		root.isRoot = true;
	}

	/**
	 * Inserts an {@link TransitionArc arc} into the model. The arc will be
	 * drawn from the specified {@link Marking marking} to the currently active
	 * marking.
	 * 
	 * @param lastTransition the last fired transition of the
	 *                       {@link PetriNetModel Petri net}
	 * @param source         the marking from which to draw the arc from
	 */
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

	/**
	 * Generates the marking name as specified in the requirements document. For
	 * example: "(0|3|0|0|2)"
	 * 
	 * @param places a list of places representing the marking
	 * @return       a string in the form (0|3|0|0|2)
	 */
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

	/**
	 * Generates the name of a transition as specified in the requirements
	 * document.
	 * 
	 * @param transition a reference to the transition
	 * @return           a string in the format "[t2] mailbox"
	 */
	private String generateTransitionIdString(Transition transition) {
		return "[" + transition.id + "] " + transition.name;
	}

	/**
	 * Sorts a list of {@link Place places} alphabetically.
	 * 
	 * @param places a list of {@link Place places}
	 */
	private void sortPlaces(List<Place> places) {
		Collections.sort(places, new Comparator<Place>() {
			@Override
			public int compare(Place o1, Place o2) {
				return o1.id.compareTo(o2.id);
			}
		});
	}

	/**
	 * Checks if a marking with the specified id exists in this model.
	 * 
	 * @param id the id of the marking
	 * @return   true, if such a marking exists, else false
	 */
	private boolean markingExists(String id) {
		if (getMarkingById(id) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a {@link Transition} with the specified id, source
	 * {@link Marking} and target Marking already exists in this model.
	 * 
	 * @param id     the id of the {@link Marking}
	 * @param source a reference to the source {@link Marking}
	 * @param target a reference to the target {@link Marking}
	 * @return       true if such a {@link Transition} exists, else false
	 */
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

	/**
	 * Returns a reference to the {@link Arc} with the specified id.
	 * 
	 * @param id     the id of the {@link Arc}
	 * @param source a reference to the source {@link Marking}
	 * @param target a reference to the target {@link Marking}
	 * @return       the {@link Arc} with the specified id or {@code null} if
	 *               doesn't exist within this model
	 */
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

	/**
	 * Sets a {@link Marking} as the currently active one.
	 * 
	 * @param activeMarking the {@link Marking} to set as currently active
	 */
	private void setAsActiveMarking(Marking activeMarking) {
		for (Marking marking : markings) {
			if (marking.id.equals(activeMarking.id)) {
				marking.isActive = true;
			} else {
				marking.isActive = false;
			}
		}
	}

	/**
	 * Sets a {@link TransitionArc transition} as the last fired transition.
	 * 
	 * @param latestArc a reference to the {@link TransitionArc}
	 */
	private void setAsLastTransition(TransitionArc latestArc) {
		for (TransitionArc arc : arcs) {
			if (arc == latestArc) {
				arc.isLatest = true;
			} else {
				arc.isLatest = false;
			}
		}
	}

	/**
	 * Removes the {@link TransitionArc#isLatest} flag from all transitions in
	 * the model.
	 */
	private void removeLatestTransition() {
		for (TransitionArc arc : arcs) {
			arc.isLatest = false;
		} 
	}

	/**
	 * Attaches event listeners to the {@link PetriNetModel} this reachability
	 * graph model is attached to.
	 */
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

	/**
	 * Attaches event listeners to the {@link FileSystemModel}.
	 */
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