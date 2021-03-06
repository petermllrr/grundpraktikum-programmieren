package io.petermueller.petrinetz;

import io.petermueller.petrinetz.controllers.MainFrameController;
import io.petermueller.petrinetz.controllers.PetriNetController;
import io.petermueller.petrinetz.controllers.RGraphController;
import io.petermueller.petrinetz.controllers.TextAreaController;
import io.petermueller.petrinetz.controllers.ToolbarController;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;
import io.petermueller.petrinetz.views.MainFrameView;
import io.petermueller.petrinetz.views.PetriNetView;
import io.petermueller.petrinetz.views.RGraphView;
import io.petermueller.petrinetz.views.StatusBarView;
import io.petermueller.petrinetz.views.TextAreaView;
import io.petermueller.petrinetz.views.ToolbarView;

/**
 * Main class of the application. All neccessary models, views and controllers
 * are initiated here to keep the object hirarchy as flat as possible.
 * <p>
 * To explore further, the {@link MainFrameController} and the
 * {@link MainFrameView} are probably the most relavent classes, since they're
 * responsible for rendering the main application frame host most of the central
 * interactions.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class Petrinets_3274969_Mueller_Hans_Peter {	

	/**
	 * Public constructor or the main class. Inititated all main objects of
	 * the MVP architecture.
	 */
	public Petrinets_3274969_Mueller_Hans_Peter() {
		FileSystemModel fileSystemModel = new FileSystemModel();
		PetriNetModel petriNetModel = new PetriNetModel();
		RGraphModel rGraphModel = new RGraphModel(
				petriNetModel,
				fileSystemModel);

		TextAreaView textAreaView = new TextAreaView();
		StatusBarView statusBarView = new StatusBarView(
				fileSystemModel,
				petriNetModel);
		PetriNetView petriNetView = new PetriNetView(
				fileSystemModel,
				petriNetModel);
		ToolbarView toolbarView = new ToolbarView(
				fileSystemModel,
				petriNetModel);
		RGraphView rGraphView = new RGraphView(
				petriNetModel,
				rGraphModel,
				fileSystemModel);
		MainFrameView mainFrameView = new MainFrameView(
				fileSystemModel,
				toolbarView,
				statusBarView,
				textAreaView,
				petriNetView,
				rGraphView);

		new PetriNetController(
				petriNetModel,
				petriNetView);
		new ToolbarController(
				toolbarView,
				fileSystemModel,
				petriNetModel,
				rGraphModel,
				rGraphView,
				textAreaView,
				mainFrameView);
		new RGraphController(
				rGraphView,
				rGraphModel,
				petriNetModel,
				petriNetView);
		new MainFrameController(
				mainFrameView,
				fileSystemModel,
				petriNetModel,
				textAreaView);
		new TextAreaController(
				textAreaView);
	}

	/**
	 * Main entry point of the app. Method executes the main application code 
	 * asynchronously on an AWT event dispatching thread.
	 * 
	 * @param args default main arguments
	 * @see javax.swing.SwingUtilities#invokeLater
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Petrinets_3274969_Mueller_Hans_Peter();
			}
		});
	}
}