package io.petermueller.petrinetz.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.spriteManager.*;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.*;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventListener;

/**
 * Renders the Petri net graph. The visualization represents the app's main
 * {@code PetriNetModel} and updates itself when data is changed in the model.
 * 
 * <p>The external library {@code GraphStream} is used for rendering the Petri
 * net graph, managing styles and placing nodes and edges.
 * 
 * @author Hans Peter Müller (3274969)
 * @see    PetriNetModel
 * @see    Event
 * @see    org.graphstream.graph.Graph
 */
public class PetriNetView {
	/**
	 * The panel containing the Petri net graph.
	 */
	public JPanel panel;
	/**
	 * The view contains the graph and controls settings like zooming and
	 * moving the graph around.
	 */
	public ViewPanel view;
	/**
	 * The viewer of the graph. The viewer serves as a wrapper around the view
	 * and manages user interactions as well as threading.
	 */
	public SwingViewer viewer;
	private Graph graph;
	private SpriteManager sman;

	/**
	 * Initializes {@code GraphStream's} {@code viewer} and {@code view}
	 * objects, creates a graph and attaches event listeners to the filesystem
	 * and the Petri net model.
	 * 
	 * @param fileSystemModel the app's file system model
	 * @param petriNetModel   the app's Petri net model
	 */
	public PetriNetView(
			FileSystemModel fileSystemModel,
			PetriNetModel petriNetModel) {
		panel = new JPanel();
		graph = new MultiGraph("Petri Net");
		viewer = new SwingViewer(
				graph,
				SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD
				);
		view = (ViewPanel) viewer.addDefaultView(false);
		sman = new SpriteManager(graph);

		panel.setBackground(Color.white);	
		panel.setBorder(
				BorderFactory.createLineBorder(new Color(0xDADCE0), 1)
				);
		panel.setLayout(new BorderLayout());
		panel.add((JPanel) view, BorderLayout.CENTER);
		addFileSystemListener(fileSystemModel, petriNetModel);
		addPetriNetListener(petriNetModel);
	}

	/**
	 * Clears the graph and renders a new visualization of a Petri net model.
	 * 
	 * @param transitions the transitions to render
	 * @param places      the places to render
	 * @param arcs        the arcs to render
	 */
	public void renderPetriNet(
			List<Transition> transitions,
			List<Place> places,
			List<Arc> arcs) {
		initGraph();
		renderTransition(transitions);
		renderPlaces(places);
		renderArcs(arcs);
		resetView();
	}

	/**
	 * Updates transitions and places of the graph. This method doesn't clear
	 * the graph before rendering but just updates the delta between the 
	 * current state and the new state.
	 * 
	 * @param transitions   a list of transitions representing the new state
	 * @param places        a list of places representing the new state
	 * @param selectedPlace the currently selected place
	 */
	public void updatePetriNet(
			List<Transition> transitions,
			List<Place> places,
			Place selectedPlace) {
		updateTransitions(transitions);
		updatePlaces(places, selectedPlace);
	}

	/**
	 * Puts the CSS class {@code selectedPlace} to the currently selected place.
	 * 
	 * @param places        a list of places representing the current state
	 * @param selectedPlace the currently selected place
	 */
	public void updatetSelectedPlace(
			List<Place> places,
			Location selectedPlace) {
		for (Place place : places) {
			if (selectedPlace == place) {
				graph.getNode(place.id).setAttribute(
						"ui.class",
						"selectedPlace"
						);
			} else {
				graph.getNode(place.id).removeAttribute("ui.class");
			}
		}
	}

	/**
	 * Adds nodes for transitions to the graph and sets attributes like x and y
	 * coordinates, id, name and if a transition is enabled.
	 * 
	 * @param transitions a list of transitions to render
	 */
	private void renderTransition(List<Transition> transitions) {
		for (Transition element : transitions) {
			graph.addNode(element.id);
			graph.getNode(element.id).setAttribute("x", element.x);
			graph.getNode(element.id).setAttribute("y", element.y);
			graph.getNode(element.id).setAttribute(
					"ui.label",
					"[" + element.id + "] " + element.name
					);
			if (element.isEnabled) {
				graph.getNode(element.id).setAttribute(
						"ui.class", 
						"transitionEnabled"
						);
				Sprite sprite = sman.addSprite(element.id + "_enabled");
				sprite.attachToNode(element.id);
				sprite.setAttribute("ui.class", "enabledTransition");
			} else {
				graph.getNode(element.id).setAttribute(
						"ui.class",
						"transition"
						);
			}
		}
	}

	/**
	 * Adds nodes for places to the graph and sets attributes like x and y
	 * coordinates, id and token count.
	 * 
	 * @param places a list of places to render
	 */
	private void renderPlaces(List<Place> places) {
		for (Place element : places) {
			graph.addNode(element.id);
			graph.getNode(element.id).setAttribute("x", element.x);
			graph.getNode(element.id).setAttribute("y", element.y);
			graph.getNode(element.id).setAttribute("ui.class", "place");
			if (element.currentTokens > 0) {
				Sprite sprite = sman.addSprite(element.id + "_tokens");
				sprite.attachToNode(element.id);
				sprite.setAttribute("ui.label", element.currentTokens);
				if (element.currentTokens > 9) {
					sprite.setAttribute("ui.class", "full");
				}
			}
			if (element.currentTokens > 0) {
				graph.getNode(element.id).setAttribute(
						"ui.label",
						"[" + element.id + "] " + element.name + " <"
								+ element.currentTokens +">"
						);
			} else {
				graph.getNode(element.id).setAttribute(
						"ui.label",
						"[" + element.id + "] " + element.name
						);
			}
		}
	}

	/**
	 * Adds an edge for each arc to the graph.
	 * 
	 * @param arcs a list of arcs to render
	 */
	private void renderArcs(List<Arc> arcs) {
		for (Arc element : arcs) {
			graph.addEdge(element.id,
					element.source.id,
					element.target.id,
					true);
			graph.getEdge(element.id).setAttribute("ui.class", "arc");
			graph.getEdge(element.id).setAttribute(
					"ui.label",
					"[" + element.id + "]"
					);
		}
	}

	/**
	 * Updates already rendered transitions without removing them from the
	 * graph.
	 * 
	 * @param transitions a list of transitions representing the new state
	 */
	private void updateTransitions(List<Transition> transitions) {
		for (Transition element : transitions) {
			if (element.isEnabled) {
				graph.getNode(element.id).setAttribute(
						"ui.class", 
						"transitionEnabled"
						);
				if (!hasSprite(element)) {
					Sprite sprite = sman.addSprite(element.id + "_enabled");
					sprite.attachToNode(element.id);
					sprite.setAttribute("ui.class", "enabledTransition");
				}
			} else {
				graph.getNode(element.id).setAttribute(
						"ui.class",
						"transition"
						);
				removeSprite(element.id + "_enabled");
			}
		}
	}

	/**
	 * Updates already rendered places without removing them from the graph.
	 * 
	 * @param places        a list of places representing the new state
	 * @param selectedPlace the currently selected place
	 */
	private void updatePlaces(List<Place> places, Place selectedPlace) {
		for (Place element : places) {
			if (element.currentTokens > 0) {
				graph.getNode(element.id).setAttribute(
						"ui.label",
						"[" + element.id + "] " + element.name + " <"
								+ element.currentTokens +">"
						);
				if(hasSprite(element)) {
					updateTokenSprite(element);
				}
				if(!hasSprite(element)) {
					createTokenSprite(element);
				}
			} else {
				graph.getNode(element.id).setAttribute(
						"ui.label",
						"[" + element.id + "] " + element.name
						);
				removeSprite(element.id + "_tokens");
			}
			if (selectedPlace == element) {
				graph.getNode(element.id).setAttribute(
						"ui.class",
						"selectedPlace"
						);
			} else {
				graph.getNode(element.id).removeAttribute("ui.class");
			}
		}
	}

	/**
	 * Resets the zoom level and camera position of the graph.
	 */
	private void resetView() {
		view.getCamera().resetView();
	}

	/**
	 * Clears the graph, sets basic rendering preferences and attaches the
	 * stylesheet.
	 */
	private void initGraph() {
		graph.clear();
		clearSprites();
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		graph.setAttribute("ui.stylesheet", initStyleSheet());
	}

	/**
	 * Creates a sprite to represent the markers on a place.
	 * 
	 * @param element the place the sprite should be placed on
	 */
	private void createTokenSprite(Place element) {
		Sprite sprite = sman.addSprite(element.id + "_tokens");
		sprite.attachToNode(element.id);
		styleTokenSprite(sprite, element);
	}

	/**
	 * Fetches the reference to the sprite attached to a place and hands it to
	 * another method for styling.
	 * 
	 * @param element the element to which the sprite is attached
	 */
	private void updateTokenSprite(Place element) {
		Sprite sprite = getSprite(element);
		styleTokenSprite(sprite, element);
	}

	/**
	 * Updates the styling of a token sprite, representing the current count of
	 * markers on a place.
	 * 
	 * @param sprite  the sprite
	 * @param element the element the sprite is attached to
	 */
	private void styleTokenSprite(Sprite sprite, Place element) {
		if (element.currentTokens > 9) {
			sprite.setAttribute("ui.class", "full");
			sprite.setAttribute("ui.label", ">9");
		} else {
			sprite.removeAttribute("ui.class");
			sprite.setAttribute("ui.label", element.currentTokens);
		}
	}

	/**
	 * Checks if a node in the graph has a sprite attached to it.
	 * 
	 * @param element the element to check
	 * @return        {@code true} if the element has a sprite attached to it,
	 *                {@code false} otherwise
	 */
	private boolean hasSprite(Location element) {
		boolean result = false;
		List<Sprite> spriteList = getSprites();
		for (Sprite sprite : spriteList) {
			if (sprite.getId().equals(element.id + "_tokens") ||
					sprite.getId().equals(element.id + "_enabled")) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Returns a reference to a sprite of an element.
	 * 
	 * @param element the element to which the sprite is attached to
	 * @return        the sprite
	 */
	private Sprite getSprite(Location element) {
		Sprite result = null;
		List<Sprite> spriteList = getSprites();
		for (Sprite sprite : spriteList) {
			if (sprite.getId().equals(element.id + "_tokens") ||
					sprite.getId().equals(element.id + "_enabled")) {
				result = sprite;
			}
		}
		return result;
	}

	/**
	 * Removes all sprites from the graph.
	 */
	private void clearSprites() {
		List<Sprite> spriteList = getSprites();
		for (Sprite sprite : spriteList) {
			sman.removeSprite(sprite.getId());
		}
	}

	/**
	 * Creates a list of sprites from the sprite manager's iterator.
	 * 
	 * @return a list of sprites
	 */
	private List<Sprite> getSprites() {
		Iterator<? extends Sprite> spriteIter = sman.sprites().iterator();
		List<Sprite> spriteList = new ArrayList<Sprite>();
		while (spriteIter.hasNext()) {
			spriteList.add(spriteIter.next());
		}
		return spriteList;
	}

	/**
	 * Removes a specific sprite.
	 * 
	 * @param spriteId the id of the sprite to remove
	 */
	private void removeSprite(String spriteId) {
		List<Sprite> spriteList = getSprites();
		for (Sprite sprite : spriteList) {
			if (sprite.getId().equals(spriteId)) {
				sman.removeSprite(spriteId);
			}
		}
	}

	/**
	 * Attaches an event listener to the filesystem model.
	 * 
	 * @param fileSystemModel the app's filesystem model
	 * @param petriNetModel   the app's Petri net model
	 * 
	 * @see   FileSystemModel
	 * @see   Event
	 */
	private void addFileSystemListener(
			FileSystemModel fileSystemModel,
			PetriNetModel petriNetModel) {
		fileSystemModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.FILESYSTEM_NEW_FILE_OPENED) {
					renderPetriNet(
							petriNetModel.transitions,
							petriNetModel.places,
							petriNetModel.arcs
							);
				}
			}
		});
	}

	/**
	 * Attaches an event listener to the app's Petri net model.
	 * 
	 * @param petriNetModel the app's Petri net model
	 * 
	 * @see   PetriNetModel
	 * @see   Event
	 */
	private void addPetriNetListener(PetriNetModel petriNetModel) {
		petriNetModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.PETRINET_TRANSITION_FIRED ||
						eventType == Event.PETRINET_MARKING_RESET ||
						eventType == Event.PETRINET_MARKING_EDITED) {
					updatePetriNet(
							petriNetModel.transitions,
							petriNetModel.places,
							petriNetModel.selectedPlace);
				}
				if (eventType == Event.PETRINET_NEW_NET_LOADED) {
					renderPetriNet(
							petriNetModel.transitions,
							petriNetModel.places,
							petriNetModel.arcs
							);
				}
			}
		});
	}

	/**
	 * The graph's stylesheet.
	 * 
	 * @return the stylesheet
	 */
	private String initStyleSheet() {
		String styleSheet;
		styleSheet =
				"graph {" +
				"  padding: 80px, 80px;" +
				"}" +
				"node {" +
				"  fill-color: #FCC934;" + // Yellow400
				"  stroke-color: #F9AB00;" + // Yellow600
				"  text-background-color: #FEEFC3;" +
				"  text-color: #984f01;" +
				"  stroke-width: 1px;" +
				"  stroke-mode: plain; " +
				"  size: 32px;" +
				"  text-size: 11;" +
				"  text-background-mode: rounded-box;" +
				"  text-alignment: under;" +
				"  text-offset: 0px, 9px;" +
				"  text-padding: 3px, 0px;" +
				"}" +
				"node.selectedPlace {" +
				"  fill-color: #F9AB00;" + // Yellow600
				"  stroke-color: #EA8600;" + // Yellow800
				"  text-background-color: #FEEFC3;" +
				"  text-color: #AE5901;" +
				"  stroke-width: 2px;" +
				"  stroke-mode: plain; " +
				"  size: 32px;" +
				"  text-size: 11;" +
				"  text-background-mode: rounded-box;" +
				"  text-alignment: under;" +
				"  text-offset: 0px, 9px;" +
				"  text-padding: 3px, 0px;" +
				"  shadow-mode: plain;" +
				"  shadow-color: #F9AB003F;" +
				"  shadow-width: 5px;" +
				"  shadow-offset: 0px;" +
				"}" +
				"node.transition {" +
				"  fill-color: #AECBFA;" + // Blue200
				"  stroke-color: #669DF6;" + //Blue400
				"  text-background-color: #D2E3FC;" +
				"  text-color: #185ABC;" +
				"  shape: box;" +
				"}" +
				"node.transitionEnabled {" +
				"  fill-color: #669DF6;" + // Blue400
				"  stroke-color: #1A73E8;" + //Blue600
				"  text-background-color: #669DF6;" + //D2E3FC
				"  stroke-width: 1px;" +
				"  text-color: #FFFFFF;" +
				"  shadow-mode: plain;" +
				"  shadow-color: #669DF63F;" +
				"  shadow-width: 5px;" +
				"  shadow-offset: 0px;" +
				"  shape: box;" +
				"}" +
				"edge {" +
				"  fill-color: #3C4043;" +
				"  text-background-color: #F1F3F4;" +
				"  text-color: #3C4043;" +
				"  text-size: 11;" +
				"  arrow-shape: arrow;" +
				"  arrow-size: 4px, 3px;" +
				"  text-background-mode: rounded-box;" +
				"  text-padding: 3px, 0px;" +
				"}" +
				"sprite {" +
				"  fill-mode: plain;" +
				"  shape: circle;" +
				"  fill-color: #AE5901;" +
				"  text-color: #FFFFFF;" +
				"  text-size: 10;" +
				"  size: 18px;" +
				"  text-offset: 0px, -2px;" +
				"  text-style: bold;" +
				"}" +
				"sprite.full {" +
				"  fill-mode: none;" +
				"  text-background-mode: rounded-box;" +
				"  text-background-color: #AE5901;"  +
				"  text-color: #FFFFFF;" +
				"  text-size: 10;" +
				"  text-offset: 0px, -2px;" +
				"  text-style: bold;" +
				"  text-padding: 3px, 2px;" +
				"}" +
				"sprite.enabledTransition {" +
				"  fill-mode: image-scaled;" +
				"  fill-image: url('ic_enabled_transition_20x20@2x.png');" +
				"  size: 20px;" +
				"}";

		return styleSheet;
	}
}
