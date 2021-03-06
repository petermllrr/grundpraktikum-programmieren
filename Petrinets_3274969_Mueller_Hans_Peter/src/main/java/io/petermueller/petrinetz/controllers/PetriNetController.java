package io.petermueller.petrinetz.controllers;

import io.petermueller.petrinetz.models.petrinet.*;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.util.ViewerListenerAdapter;
import io.petermueller.petrinetz.views.PetriNetView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.graphstream.ui.view.ViewerPipe;

/**
 * Controller of the {@link PetriNetView}, handling user interactions with the
 * petri net graph. Calls apropriate methods in the {@link PetriNetModel} and
 * {@link PetriNetView}.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class PetriNetController {
	private PetriNetModel petriNetModel;
	private PetriNetView petriNetView;

	/**
	 * Public constructor. Registers private attributes and calls further
	 * private functions.
	 * 
	 * @param petriNetModel the petri net model
	 * @param petriNetView the petri net view
	 */
	public PetriNetController(
			PetriNetModel petriNetModel,
			PetriNetView petriNetView) {
		this.petriNetModel = petriNetModel;
		this.petriNetView = petriNetView;
		addViewerListener();
	}

	/**
	 * Registers the viwer pipe, which is required by GraphStream to map user
	 * interactions to clicked objects in the graph.
	 * 
	 * @see ViewerPipe
	 */
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
