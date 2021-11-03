package io.petermueller.petrinetz.controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.graphstream.ui.view.ViewerPipe;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.rgraph.Marking;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;
import io.petermueller.petrinetz.util.ViewerListenerAdapter;
import io.petermueller.petrinetz.views.PetriNetView;
import io.petermueller.petrinetz.views.RGraphView;

public class RGraphController {
	private RGraphView rGraphView;
	private PetriNetView petriNetView;
	private RGraphModel rGraphModel;
	private PetriNetModel petriNetModel;

	public RGraphController(
			RGraphView rGraphView,
			RGraphModel rGraphModel,
			PetriNetModel petriNetModel,
			PetriNetView petriNetView) {
		this.rGraphModel = rGraphModel;
		this.petriNetModel = petriNetModel;
		this.rGraphView = rGraphView;
		this.petriNetView = petriNetView;
		addViewerListener();	
	}

	private void addViewerListener() {
		ViewerPipe viewerPipe = rGraphView.viewer.newViewerPipe();
		rGraphView.view.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent me) {
				viewerPipe.pump();}
		});

		viewerPipe.addViewerListener(new ViewerListenerAdapter() {
			@Override
			public void buttonReleased(String id) {
				Marking marking = rGraphModel.getMarkingById(id);
				if (marking.getClass() == Marking.class) {
					rGraphModel.setToMarking(marking.places);
					rGraphView.updateGraph(
							rGraphModel.markings,
							rGraphModel.arcs);
					petriNetModel.setToMarking(marking.places);
					petriNetView.updatePetriNet(
							petriNetModel.transitions,
							petriNetModel.places,
							petriNetModel.selectedPlace);
				}
			}
		});
	}
}