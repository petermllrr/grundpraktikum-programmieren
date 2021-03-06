package io.petermueller.petrinetz.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.rgraph.Marking;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;
import io.petermueller.petrinetz.models.rgraph.TransitionArc;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventListener;

/**
 * Renders the reachability graph. The visualization represents the app's main
 * {@code RGraphModel} and updates itself when data is changed in the model.
 * 
 * <p>The external library {@code GraphStream} is used for rendering the 
 * reachability graph, managing styles and placing nodes and edges.
 * 
 * @author Hans Peter Müller (3274969)
 * @see    RGraphModel
 * @see    Event
 * @see    org.graphstream.graph.Graph
 */
public class RGraphView {
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
	private Layout layout;
	private PetriNetModel petriNetModel;
	private RGraphModel rGraphModel;
	private FileSystemModel fileSystemModel;

	/**
	 * Initializes {@code GraphStream's} {@code viewer} and {@code view}
	 * objects, creates a graph and attaches event listeners to the filesystem
	 * and the reachabilty graph model.
	 * 
	 * @param petriNetModel   the app's Petri net model
	 * @param rGraphModel     the app's reachabilty graph model
	 * @param fileSystemModel the app's filesystem model
	 */
	public RGraphView(
			PetriNetModel petriNetModel,
			RGraphModel rGraphModel,
			FileSystemModel fileSystemModel) {
		this.petriNetModel = petriNetModel;
		this.rGraphModel = rGraphModel;
		this.fileSystemModel = fileSystemModel;
		panel = new JPanel();
		graph = new MultiGraph("Reachability Graph");
		layout = new SpringBox(false);
		graph.addSink(layout);
		layout.addAttributeSink(graph);
		viewer = new SwingViewer(
				graph,
				SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD
				);
		viewer.enableAutoLayout();
		view = (ViewPanel) viewer.addDefaultView(false);
		sman = new SpriteManager(graph);

		panel.setBackground(Color.white);
		panel.setBorder(
				BorderFactory.createLineBorder(new Color(0xDADCE0), 1));
		panel.setLayout(new BorderLayout());
		panel.add((JPanel) view, BorderLayout.CENTER);
		addPetriNetEventListener();
		addFileSystemEventListener();
		addRGraphEventListener();
	}

	/**
	 * Clears the graph and renders a new visualization of a reachability graph
	 * model.
	 * 
	 * @param markings the markings to render
	 * @param arcs     the transition arcs to render
	 */
	public void renderInitialRGraph(
			List<Marking> markings,
			List<TransitionArc> arcs) {
		initGraph();
		updateGraph(markings, arcs);
		resetView();
	}

	/**
	 * Updates markings and transition arcs of the graph. This method doesn't
	 * clear the graph before rendering but adds markings and arcs that aren't
	 * already rendered.
	 * 
	 * @param markings list of markings representing the new state
	 * @param arcs     list of arcs representing the new state
	 */
	public void updateGraph(List<Marking> markings, List<TransitionArc> arcs) {
		for (Marking marking : markings) {
			if (!markingExists(marking)) {
				createNode(marking);
				triggerRelayout();
			}
			updateNodeStyles(marking);
		}
		for (TransitionArc arc : arcs) {
			if (!arcExists(arc)) {
				createEdge(arc);
			}
			updateEdgeStyling(arc);
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
	 * Updates the styling of a node in the graph.
	 * 
	 * @param marking the node to update
	 */
	private void updateNodeStyles(Marking marking) {
		if (marking.isPathEnd) {
			graph.getNode(marking.id).setAttribute("ui.class", "pathEnd");
		} else if (marking.isActive || marking.isPathStart) {
			graph.getNode(marking.id).setAttribute("ui.class", "active");
		} else if (marking.isRoot) {
			graph.getNode(marking.id).setAttribute("ui.class", "root");
		} else {
			graph.getNode(marking.id).removeAttribute("ui.class");
		}
	}

	/**
	 * Updates the styling of an edge in the graph.
	 * 
	 * @param arc the edge to update
	 */
	private void updateEdgeStyling(TransitionArc arc) {
		if (arc.isLatest || arc.isOnDetectionPath) {
			graph.getEdge(getId(arc)).setAttribute("ui.class", "active");
			sman.getSprite(getId(arc)).setAttribute("ui.class", "active");
		} else {
			graph.getEdge(getId(arc)).removeAttribute("ui.class");
			sman.getSprite(getId(arc)).removeAttribute("ui.class");
		}
	}

	/**
	 * Creates a new node in the graph to represent a marking.
	 * 
	 * @param marking a marking to create a node from
	 */
	private void createNode(Marking marking) {
		String id = marking.id;
		graph.addNode(id);
		graph.getNode(id).setAttribute("ui.label", id);
	}

	/**
	 * Creates an edge in the graph representing a transition arc.
	 * 
	 * @param arc the transition arc to create an edge from
	 */
	private void createEdge(TransitionArc arc) {
		graph.addEdge(getId(arc), arc.source.id, arc.target.id, true);
		Sprite sprite = sman.addSprite(getId(arc));
		sprite.attachToEdge(getId(arc));
		sprite.setPosition(0.35);
		sprite.setAttribute("ui.label", arc.id);
	}

	/**
	 * Generates the unique id of a transition arc.
	 * 
	 * @param arc an arc to generate the id from
	 * @return    the unique id
	 */
	private String getId(TransitionArc arc) {
		return arc.id + arc.source.id + arc.target.id;
	}

	/**
	 * Checks if a marking already has a node in the graph.
	 * 
	 * @param marking the marking to check
	 * @return        {@code true} if a node exists, {@code false} otherwise
	 */
	private boolean markingExists(Marking marking) {
		String id = marking.id;
		if (graph.getNode(id) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a transition already has an edge in the graph.
	 * 
	 * @param arc the transition to check
	 * @return    {@code true} if an edge exists, {@code false} otherwise
	 */
	private boolean arcExists(TransitionArc arc) {
		String id = arc.id;
		String source = arc.source.id;
		String target = arc.target.id;
		String edgeId = id + source + target;
		if (graph.getEdge(edgeId) != null) {
			return true;
		}
		return false;
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
	 * Triggers a manual relayout so the graph layouts itself nicely after a
	 * new node has been added.
	 */
	private void triggerRelayout() {
		while(layout.getStabilization() < 0.9){
			layout.compute();
		}
	}

	/**
	 * /**
	 * Attaches an event listener to the app's Petri net model.
	 * 
	 * @see   PetriNetModel
	 * @see   Event
	 */
	private void addPetriNetEventListener() {
		petriNetModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.PETRINET_TRANSITION_FIRED) {
					updateGraph(
							rGraphModel.markings,
							rGraphModel.arcs);
				}
				if (eventType == Event.PETRINET_MARKING_EDITED) {
					renderInitialRGraph(
							rGraphModel.markings,
							rGraphModel.arcs);
				}
			}
		});
	}

	/**
	 * Attaches an event listener to the filesystem model.

	 * @see   FileSystemModel
	 * @see   Event
	 */
	private void addFileSystemEventListener() {
		fileSystemModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.FILESYSTEM_NEW_FILE_OPENED) {
					renderInitialRGraph(
							rGraphModel.markings,
							rGraphModel.arcs);
				}
			}
		});
	}

	/**
	 * Attaches an event listener to the reachability graph model.
	 * 
	 * @see    RGraphModel
	 * @see    Event
	 */
	private void addRGraphEventListener() {
		rGraphModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.RGRAPH_RESET) {
					renderInitialRGraph(
							rGraphModel.markings,
							rGraphModel.arcs);
				}
				if (eventType == Event.RGRAPH_SET_TO_MARKING) {
					updateGraph(
							rGraphModel.markings,
							rGraphModel.arcs);
				}
				if (eventType == Event.RGRAPH_NEW_GRAPH_LOADED) {
					renderInitialRGraph(
							rGraphModel.markings,
							rGraphModel.arcs);
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
				"node {" +
				"  text-color: #3C4043;" + // Grey 800
				"  fill-color: #FFFFFF;" + // White
				"  shape: box;" +
				"  text-size: 11;" +
				"  padding: 4px, 2px;" +
				"  size-mode: fit;" +
				"  stroke-mode: plain;" +
				"  stroke-color: #BDC1C6;" + // Grey 400
				"}" +
				"node.active {" +
				"  text-color: #984f01;" +
				"  fill-color: #FEEFC3;" + // Yellow 100
				"  stroke-mode: plain;" +
				"  stroke-color: #F9AB00;" + // Yellow 600
				"}" +
				"node.root {" +
				"  fill-color: #DADCE0;" + // Grey 300
				"  stroke-color: #9AA0A6;" + // Grey 500
				"}" +
				"node.pathEnd {" +
				"  fill-color: #FAD2CF;" + // Red 100
				"  stroke-color: #D93025;" + // Red 600
				"  text-color: #C5221F;" + // Red 700
				"}" +
				"edge {" +
				"  fill-color: #3C4043;" +
				"  arrow-shape: arrow;" +
				"  arrow-size: 12px, 6px;" +
				"}" +
				"edge.active {" +
				"  fill-color: #1A73E8;" + // Blue 600
				"}" +
				"sprite {" +
				"  text-background-color: #FFFFFF;" +
				"  text-color: #3C4043;" + // Grey 800
				"  text-size: 11;" +
				"  text-background-mode: rounded-box;" +
				"  text-padding: 3px, 0px;" +
				"}" +
				"sprite.active {" +
				"  text-background-color: #D2E3FC;" +
				"  text-color: #185ABC;" +
				"}";

		return styleSheet;
	}
}