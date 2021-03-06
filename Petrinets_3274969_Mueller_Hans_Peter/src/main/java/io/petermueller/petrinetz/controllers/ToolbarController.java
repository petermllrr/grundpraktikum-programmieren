package io.petermueller.petrinetz.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;
import io.petermueller.petrinetz.util.BoundednessAnalysis;
import io.petermueller.petrinetz.views.RGraphView;
import io.petermueller.petrinetz.views.TextAreaView;
import io.petermueller.petrinetz.views.ToolbarView;
import io.petermueller.petrinetz.views.DialogWindowView;
import io.petermueller.petrinetz.views.MainFrameView;

/**
 * Controller of the {@link ToolbarView}, handling events in the toolbar. Calls
 * the appropriate methods in the models to change the data according to the
 * user's inputs.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class ToolbarController {
	private PetriNetModel petriNetModel;
	private ToolbarView toolbarView;
	private FileSystemModel fileSystemModel;
	private TextAreaView textAreaView;
	private RGraphModel rGraphModel;
	private MainFrameView mainFrameView;

	/**
	 * Registers private attributes for easier reference and calls private
	 * functions.
	 * 
	 * @param toolbarView     the toolbar view
	 * @param fileSystemModel the file system model
	 * @param petriNetModel   the petri net model
	 * @param rGraphModel     the reachability graph model
	 * @param rGraphView      the reachability graph view
	 * @param textAreaView    the text area view
	 * @param mainFrameView   the main frame view
	 */
	public ToolbarController(
			ToolbarView toolbarView,
			FileSystemModel fileSystemModel,
			PetriNetModel petriNetModel,
			RGraphModel rGraphModel,
			RGraphView rGraphView,
			TextAreaView textAreaView,
			MainFrameView mainFrameView) {
		this.petriNetModel = petriNetModel;
		this.toolbarView = toolbarView;
		this.fileSystemModel = fileSystemModel;
		this.textAreaView = textAreaView;
		this.rGraphModel = rGraphModel;
		this.mainFrameView = mainFrameView;
		addButtonListeners();
	}

	/**
	 * Attaches the {@link ButtonListener} at the individual buttons.
	 */
	private void addButtonListeners() {
		ButtonListener buttonListener = new ButtonListener();
		toolbarView.buttonPrevFile.addActionListener(buttonListener);
		toolbarView.buttonNextFile.addActionListener(buttonListener);
		toolbarView.buttonReset.addActionListener(buttonListener);
		toolbarView.buttonAddToken.addActionListener(buttonListener);
		toolbarView.buttonRemoveToken.addActionListener(buttonListener);
		toolbarView.buttonBoundednessAnalysis.addActionListener(buttonListener);
		toolbarView.buttonClearRG.addActionListener(buttonListener);
		toolbarView.buttonClearText.addActionListener(buttonListener);
	}

	/**
	 * An {@link ActionListener} which handles user input for the button in the
	 * toolbar.
	 */
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Open previous file":
				PetriNetModel prevPetrinet = fileSystemModel.readNewFile(
						fileSystemModel.prevFile
						);
				if (prevPetrinet != null) {
					petriNetModel.load(prevPetrinet);
					fileSystemModel.notifyGui();
				}
				break;
			case "Open next file":
				PetriNetModel nextPetriNet = fileSystemModel.readNewFile(
						fileSystemModel.nextFile
						);
				if (nextPetriNet != null) {
					petriNetModel.load(nextPetriNet);
					fileSystemModel.notifyGui();
				}
				break;
			case "Reset Marking":
				petriNetModel.reset();
				break;
			case "Add Token":
				petriNetModel.addUserToken();
				break;
			case "Remove Token":
				petriNetModel.removeUserToken();
				break;
			case "Boundedness Analysis":
				BoundednessAnalysis analysis = new BoundednessAnalysis(
						petriNetModel);
				petriNetModel.load(analysis.petriNet);
				rGraphModel.load(analysis.rGraph);
				textAreaView.printBoundednessResults(analysis);
				String result = (analysis.isBounded)
						? "Das Petrinetz ist beschränkt."
								: "Das Petrinetz ist unbeschränkt.";
				new DialogWindowView(mainFrameView.mainFrame, result);
				break;
			case "Reset Reachability Graph":
				petriNetModel.reset();
				rGraphModel.reset(petriNetModel.places);
				break;
			case "Clear Text Console":
				textAreaView.clear();
				break;
			}
		}
	}
}