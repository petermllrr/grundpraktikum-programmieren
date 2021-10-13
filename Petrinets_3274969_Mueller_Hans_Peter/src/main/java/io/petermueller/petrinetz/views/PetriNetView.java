package io.petermueller.petrinetz.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.spriteManager.*;
import io.petermueller.petrinetz.models.petrinet.*;

public class PetriNetView {
	public JPanel panel;
	private Graph graph;
	private ViewPanel view;
	private SpriteManager sman;
	private List<Sprite> sprites;
	
	public PetriNetView() {
		panel = new JPanel();
		graph = new MultiGraph("Graph");
		SwingViewer viewer = new SwingViewer(
			graph,
			SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD
		);
		view = (ViewPanel) viewer.addDefaultView(false);
		sman = new SpriteManager(graph);
		sprites = new ArrayList<Sprite>();
		
		panel.setBackground(Color.white);	
		panel.setBorder(
			BorderFactory.createLineBorder(new Color(0xDADCE0), 1)
		);
		panel.setLayout(new BorderLayout());
		panel.add((JPanel) view, BorderLayout.CENTER);
	}
	
	private void initGraph() {
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		graph.setAttribute("ui.stylesheet", initStyleSheet());
	}
	
	public void renderPetriNet(
		List<Transition> transitions,
		List<Place> places,
		List<Arc> arcs) {
		graph.clear();
		for (Sprite sprite : sprites) {
			sman.removeSprite(sprite.getId());
		}
		initGraph();
		for (Transition element : transitions) {
			graph.addNode(element.id);
			graph.getNode(element.id).setAttribute("x", element.x);
			graph.getNode(element.id).setAttribute("y", element.y);
			graph.getNode(element.id).setAttribute("ui.class", "transition");
			graph.getNode(element.id).setAttribute(
				"ui.label",
				"[" + element.id + "] " + element.name
			);
		}
		for (Place element : places) {
			graph.addNode(element.id);
			graph.getNode(element.id).setAttribute("x", element.x);
			graph.getNode(element.id).setAttribute("y", element.y);
			graph.getNode(element.id).setAttribute("ui.class", "place");
			graph.getNode(element.id).setAttribute("tokens", element.tokens);
			if (element.tokens > 0) {
				Sprite sprite = sman.addSprite(element.id + "_tokens");
				sprite.attachToNode(element.id);
				sprite.setAttribute("ui.label", element.tokens);
				if (element.tokens > 9) {
					sprite.setAttribute("ui.class", "full");
				}
				sprites.add(sprite);
			}
			if (element.tokens > 0) {
				graph.getNode(element.id).setAttribute(
					"ui.label",
					"[" + element.id + "] " + element.name + " <"
					+ element.tokens +">"
				);
			} else {
				graph.getNode(element.id).setAttribute(
					"ui.label",
					"[" + element.id + "] " + element.name
				);
			}
		}
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
		view.getCamera().resetView();
	}
	
	private String initStyleSheet() {
		String styleSheet;
		styleSheet =
	        "node {" +
	        "  fill-color: #FCC934;" +
	        "  stroke-color: #F9AB00;" +
	        "  text-background-color: #FEEFC3;" +
	        "  text-color: #AE5901;" +
	        "  stroke-width: 2px;" +
	        "  stroke-mode: plain; " +
	        "  size: 32px;" +
	        "  text-size: 21;" +
	        "  text-background-mode: rounded-box;" +
	        "  text-alignment: under;" +
	        "  text-offset: 0px, 18px;" +
	        "  text-padding: 6px, 0px;" +
	        "}" +
	        "node.transition {" +
	        "  fill-color: #669DF6;" +
	        "  stroke-color: #1A73E8;" +
	        "  text-background-color: #D2E3FC;" +
	        "  text-color: #185ABC;" +
	        "  shape: box;" +
	        "}" +
	        "edge {" +
	        "  fill-color: #3C4043;" +
	        "  text-background-color: #F1F3F4;" +
	        "  text-color: #3C4043;" +
	        "  text-size: 21;" +
	        "  arrow-shape: arrow;" +
	        "  arrow-size: 7px, 5px;" +
	        "  text-background-mode: rounded-box;" +
	        "  text-padding: 6px, 0px;" +
	        "}" +
			"sprite {" +
	        "  fill-mode: plain;" +
	        "  shape: circle;" +
	        "  fill-color: #AE5901;" +
	        "  text-color: #FFFFFF;" +
	        "  text-size: 20;" +
	        "  size: 18px;" +
	        "  text-offset: 0px, -4px;" +
	        "  text-style: bold;" +
	        "}" +
			"sprite.full {" +
	        "  fill-mode: none;" +
	        "  text-background-mode: rounded-box;" +
	        "  text-background-color: #AE5901;"  +
	        "  text-color: #FFFFFF;" +
	        "  text-size: 20;" +
	        "  text-offset: 0px, -4px;" +
	        "  text-style: bold;" +
	        "}";
		 return styleSheet;
	}
}
