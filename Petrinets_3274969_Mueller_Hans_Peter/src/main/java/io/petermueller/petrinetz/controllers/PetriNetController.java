package io.petermueller.petrinetz.controllers;

import io.petermueller.petrinetz.models.petrinet.*;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.util.ViewerListenerAdapter;
import io.petermueller.petrinetz.views.PetriNetView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import org.graphstream.ui.view.ViewerPipe;

public class PetriNetController {
	private PetriNetModel petriNetModel;
	private PetriNetView petriNetView;

	public PetriNetController(
			PetriNetModel petriNetModel,
			PetriNetView petriNetView) {
		this.petriNetModel = petriNetModel;
		this.petriNetView = petriNetView;
		addViewerListener();
	}

	private void addViewerListener() {
		ViewerPipe viewerPipe = petriNetView.viewer.newViewerPipe();

		petriNetView.view.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent me) {
				viewerPipe.pump();
			}
		});

		viewerPipe.addViewerListener(new ViewerListenerAdapter() {
			@Override
			public void buttonReleased(String id) {
				Location element = petriNetModel.getLocation(id);
				if (element.getClass() == Transition.class) {
					petriNetModel.removeActivePlace();
					petriNetModel.fire((Transition) element);
				} else if (element.getClass() == Place.class) {
					petriNetModel.setActivePlace(id);
					petriNetView.updatetSelectedPlace(
							petriNetModel.places,
							petriNetModel.selectedPlace);
				}
			}
		});
	}
}