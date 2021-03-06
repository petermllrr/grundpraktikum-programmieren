package io.petermueller.petrinetz.models.petrinet;

import java.util.ArrayList;
import java.util.List;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventEmitter;

/**
 * A datastructure representing a single Petri net. The structure of the Petri
 * net is stored in three different classes: {@link Arc Arcs}, {@link Place
 * Places} and {@link Transition Transitions}. Arcs connect places and
 * transitions. Places remember how many Markers are present on them.
 * Essentially, the sum of all places is a marking.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class PetriNetModel extends EventEmitter {
	/**
	 * A list of all {@link Transition Transitions} in the Petri net.
	 */
	public List<Transition> transitions;
	/**
	 * A list of all {@link Place Places} in the Petri net.
	 */
	public List<Place> places;
	/**
	 * A list of all {@link Arc Arcs} in the Petri net.
	 */
	public List<Arc> arcs;
	/**
	 * A reference to the {@link Place} selected by the user.
	 */
	public Place selectedPlace;
	/**
	 * True, if the marking of the Petri net has been changed by firing a
	 * transition, false, if it's unchanged.
	 */
	public boolean isAtCustomMarking;
	/**
	 * True, if the Petri net has a user defined marking that is different from
	 * the original marking, else false.
	 */
	public boolean fileChanged;
	/**
	 * A reference to the last {@link Transition} that was fired.
	 */
	public Transition lastTransition;
	/**
	 * The file name of the Petri net.
	 */
	public String fileName;

	/**
	 * Initiates an empty Petri net.
	 */
	public PetriNetModel() {
		init();
	}

	/**
	 * Adds a {@link Transition} to the Petri net.
	 * 
	 * @param id the unique id of the {@link Transition}
	 */
	public void addTransition(String id) {
		transitions.add(new Transition(id));
		updateTransitions();
	}

	/**
	 * Adds a {@link Place} to the Petri net.
	 * 
	 * @param id the unique id of the {@link Place}
	 */
	public void addPlace(String id) {
		places.add(new Place(id));
		updateTransitions();
	}

	/**
	 * Adds an {@link Arc} to the Petri net, connecting two {@link Location
	 * locations}.
	 * 
	 * @param id     the unique id of the {@link Arc}
	 * @param source a reference to the source {@link Location}
	 * @param target a reference to the target {@link Location}
	 */
	public void addArc(String id, String source, String target) {
		Location sourceObj = getLocation(source);
		Location targetObj = getLocation(target);
		arcs.add(new Arc(id, sourceObj, targetObj));
		updateTransitions();
	}

	/**
	 * Sets a name for a {@link Location}.
	 * 
	 * @param id   the id of the {@link Location} to name
	 * @param name a name
	 */
	public void setName(String id, String name) {
		Location location = getLocation(id);
		location.name = name;
	}

	/**
	 * Sets the amount of markers on a given {@link Place}. By setting the
	 * markers via this method, they are saved as the Petri net's start marking.
	 * 
	 * @param id     the id of the {@link Place}
	 * @param tokens the amount of markers on this {@link Place}
	 */
	public void setTokens(String id, int tokens) {
		Location location = getLocation(id);
		Place place = (Place) location;
		try {
			if (tokens < 0) {
				throw new PetriNetException("The lowest amount of tokens you "
						+ "can set on place " + id + " is 0.");
			}
			place.currentTokens = tokens;
			place.startTokens = tokens;
			updateTransitions();
		} catch (PetriNetException e) {
			System.err.println(e);
			place.currentTokens = 0;
			place.startTokens = 0;
		}
		updateTransitions();
	}
	
	/**
	 * Sets the position of a given {@link Location}.
	 * 
	 * @param id the id of the {@link Location}
	 * @param x  the x-coodinate of the {@link Location}
	 * @param y  the y-coordinate of the {@link Location}
	 */
	public void setPosition(String id, int x, int y) {
		Location location = getLocation(id);
		location.x = x;
		location.y = y * (-1);
	}

	/**
	 * Returns a reference to the {@link Location} with the given id.
	 * 
	 * @param query the id of the {@link Location}.
	 * @return a reference to the {@link Location} or null of no Location with
	 * 		   this id exists.
	 */
	public Location getLocation(String query) {
		for (Transition transition : transitions) {
			if (transition.id.equals(query)) {
				return transition;
			}
		}
		for (Place place : places) {
			if (place.id.equals(query)) {
				return place;
			}
		}
		return null;
	}

	/**
	 * Fires a {@link Transition} in the Petri net, moving markers from input
	 * {@link Place Places} to output {@link Place Places}.
	 * 
	 * @param transition the id of the {@link Transition} to fire
	 */
	public void fire(Transition transition) {
		if (hasTruePreconditions(transition)) {
			List<Place> inputs = getInputs(transition);
			List<Place> outputs = getOutputs(transition);
			for (Place place : inputs) {
				place.currentTokens --;
			}
			for (Place place : outputs) {
				place.currentTokens ++;
			}
			updateTransitions();
			isAtCustomMarking = true;
			lastTransition = transition;
			fireEvent(Event.PETRINET_TRANSITION_FIRED);
		}
	}

	/**
	 * Defines the {@link Place} that is currently selected by the user. It's
	 * stored for easy reference as {@link PetriNetModel#selectedPlace}.
	 * 
	 * @param id the id of the {@link Place}
	 */
	public void setActivePlace(String id) {
		selectedPlace = (Place) getLocation(id);
		fireEvent(Event.PETRINET_USER_SELECTED_A_PLACE);
	}

	/**
	 * Unsets the currently user-selected {@link Place} in case the user
	 * de-selects it.
	 */
	public void removeActivePlace() {
		selectedPlace = null;
		fireEvent(Event.PETRINET_USER_SELECTED_A_PLACE);
	}

	/**
	 * Adds a marker to the currently {@link PetriNetModel#selectedPlace 
	 * selected Place}.
	 */
	public void addUserToken() {
		selectedPlace.currentTokens ++;
		setCurrentTokensAsStartTokens();
	}

	/**
	 * Removes a marker from the currently {@link PetriNetModel#selectedPlace 
	 * selected Place}.
	 */
	public void removeUserToken() {
		try {
			if (selectedPlace.currentTokens == 0) {
				throw new PetriNetException("The lowest amount of tokens you "
						+ "can set on place " + selectedPlace.id + " is 0.");
			}
			selectedPlace.currentTokens --;
		} catch (Exception e) {
			System.err.println(e);
		}
		setCurrentTokensAsStartTokens();
	}

	/**
	 * Sets the marking of the Petri net to the last defined start marking. This
	 * can either be the original marking from the parsed file or a custom
	 * user-defined marking.
	 */
	public void reset() {
		for (Place place : places) {
			place.currentTokens = place.startTokens;
		}
		isAtCustomMarking = false;
		updateTransitions();
		fireEvent(Event.PETRINET_MARKING_RESET);
	}

	/**
	 * Sets the Petri net to a specified marking. The currently saved marking
	 * will be overridden with the newly specified one.
	 * 
	 * @param newPlaces a list of places with markers
	 */
	public void setToMarking(List<Place> newPlaces) {
		for (Place newPlace : newPlaces) {
			for (Place place : this.places) {
				if (newPlace.id.equals(place.id)) {
					place.currentTokens = newPlace.currentTokens;
				}
			}
		}
		updateTransitions();
		fireEvent(Event.PETRINET_MARKING_RESET);
	}

	/**
	 * Clears the Petri net models and loads new Petri data into the model.
	 * 
	 * @param petriNet a Petri net model to load
	 */
	public void load(PetriNetModel petriNet) {
		init();
		for (Transition transition : petriNet.transitions) {
			this.transitions.add(transition);
		}
		for (Place place : petriNet.places) {
			this.places.add(place);
		}
		for (Arc arc : petriNet.arcs) {
			this.arcs.add(arc);
		}
		this.selectedPlace = petriNet.selectedPlace;
		this.isAtCustomMarking = petriNet.isAtCustomMarking;
		this.fileChanged = petriNet.fileChanged;
		this.lastTransition = petriNet.lastTransition;
		this.fileName = petriNet.fileName;
		fireEvent(Event.PETRINET_NEW_NET_LOADED);
	}

	/**
	 * Initializes an empty Petri net model.
	 */
	private void init() {
		transitions = new ArrayList<Transition>();
		places = new ArrayList<Place>();
		arcs = new ArrayList<Arc>();
		selectedPlace = null;
		isAtCustomMarking = false;
		fileChanged = false;
		lastTransition = null;
		fileName = "";
	}

	/**
	 * Checks which transitions can be fired and sets the 
	 * {@link Transition#isEnabled} flag if all preconditions of the Transition
	 * are met.
	 */
	private void updateTransitions() {
		for (Transition transition : transitions) {
			if (hasTruePreconditions(transition)) {
				transition.isEnabled = true;
			} else {
				transition.isEnabled = false;
			}
		}
	}

	/**
	 * Takes the current amount and position of markers in the Petri net and
	 * sets it as the new start marking.
	 */
	private void setCurrentTokensAsStartTokens() {
		for (Place place : places) {
			place.startTokens = place.currentTokens;
		}
		isAtCustomMarking = false;
		fileChanged = true;
		updateTransitions();
		fireEvent(Event.PETRINET_MARKING_EDITED);
	}

	/**
	 * Returns all input places of a {@link Transition}.
	 * 
	 * @param transition the id of the {@link Transition} to get the input
	 *                   {@link Place Places} from
	 * @return a list of {@link Place Places}
	 */
	private List<Place> getInputs(Transition transition) {
		List<Place> inputs = new ArrayList<Place>();
		for (Arc arc : arcs) {
			if (arc.target == transition) {
				inputs.add((Place) arc.source);
			}
		}
		return inputs;
	}

	/**
	 * Returns all output places of a {@link Transition}.
	 * 
	 * @param transition the id of the {@link Transition} to get the output
	 *                   {@link Place Places} from
	 * @return a list of {@link Place Places}
	 */
	private List<Place> getOutputs(Transition transition) {
		List<Place> outputs = new ArrayList<Place>();
		for (Arc arc : arcs) {
			if (arc.source == transition) {
				outputs.add((Place) arc.target);
			}
		}
		return outputs;
	}

	/**
	 * Checks if a {@link Transition} has all preconditions fullfilled and can
	 * be fired. Essentially this method checks if there are any input places
	 * which have zero markers in them.
	 * 
	 * @param transition the id of the transiton to check
	 * @return true, if all preconditions are met, else false
	 */
	private boolean hasTruePreconditions(Transition transition) {
		List<Place> preconditions = getInputs(transition);
		boolean result = true;
		for (Place place : preconditions) {
			if (place.currentTokens < 1) {
				result = false;
			}
		}
		return result;
	}
}