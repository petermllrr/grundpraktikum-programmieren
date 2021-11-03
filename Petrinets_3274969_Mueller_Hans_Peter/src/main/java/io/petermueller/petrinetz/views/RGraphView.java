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

public class RGraphView {
	public JPanel panel;
	public ViewPanel view;
	public SwingViewer viewer;
	private Graph graph;
	private SpriteManager sman;
	private Layout layout;
	private PetriNetModel petriNetModel;
	private RGraphModel rGraphModel;
	private FileSystemModel fileSystemModel;

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

	public void renderInitialRGraph(
			List<Marking> markings,
			List<TransitionArc> arcs) {
		initGraph();
		updateGraph(markings, arcs);
		resetView();
	}

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

	private void resetView() {
		view.getCamera().resetView();
	}

	private void initGraph() {
		graph.clear();
		clearSprites();
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		graph.setAttribute("ui.stylesheet", initStyleSheet());
	}

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

	private void updateEdgeStyling(TransitionArc arc) {
		if (arc.isLatest || arc.isOnDetectionPath) {
			graph.getEdge(getId(arc)).setAttribute("ui.class", "active");
			sman.getSprite(getId(arc)).setAttribute("ui.class", "active");
		} else {
			graph.getEdge(getId(arc)).removeAttribute("ui.class");
			sman.getSprite(getId(arc)).removeAttribute("ui.class");
		}
	}

	private void createNode(Marking marking) {
		String id = marking.id;
		graph.addNode(id);
		graph.getNode(id).setAttribute("ui.label", id);
	}

	private void createEdge(TransitionArc arc) {
		graph.addEdge(getId(arc), arc.source.id, arc.target.id, true);
		Sprite sprite = sman.addSprite(getId(arc));
		sprite.attachToEdge(getId(arc));
		sprite.setPosition(0.35);
		sprite.setAttribute("ui.label", arc.id);
	}

	private String getId(TransitionArc arc) {
		return arc.id + arc.source.id + arc.target.id;
	}

	private boolean markingExists(Marking marking) {
		String id = marking.id;
		if (graph.getNode(id) != null) {
			return true;
		}
		return false;
	}

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

	private void clearSprites() {
		List<Sprite> spriteList = getSprites();
		for (Sprite sprite : spriteList) {
			sman.removeSprite(sprite.getId());
		}
	}

	private List<Sprite> getSprites() {
		Iterator<? extends Sprite> spriteIter = sman.sprites().iterator();
		List<Sprite> spriteList = new ArrayList<Sprite>();
		while (spriteIter.hasNext()) {
			spriteList.add(spriteIter.next());
		}
		return spriteList;
	}

	private void triggerRelayout() {
		while(layout.getStabilization() < 0.9){
			layout.compute();
		}
	}

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