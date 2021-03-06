package io.petermueller.petrinetz.views;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import javax.imageio.*;
import javax.swing.*;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventListener;

/**
 * Renders the toolbar of the app's main window.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class ToolbarView{
	/**
	 * The toolbar component.
	 */
	public JToolBar toolbar;
	/**
	 * The "Vorherige Datei öffnen" button.
	 */
	public JButton buttonPrevFile,
	/**
	 * The "Nächste Datei öffnen" button.
	 */
	buttonNextFile,
	/**
	 * The "Petrinetz zurücksetzen" button.
	 */
	buttonReset,
	/**
	 * The "Marke hinzufügen" button.
	 */
	buttonAddToken,
	/**
	 * The "Marke entfernen" button.
	 */
	buttonRemoveToken,
	/**
	 * The "Beschränktheits-Analyse" button.
	 */
	buttonBoundednessAnalysis,
	/**
	 * The "Lösche Erreichbarkeitsgraph" button.
	 */
	buttonClearRG,
	/**
	 * The "Textfeld leeren" button.
	 */
	buttonClearText;
	private FileSystemModel fileSystemModel;
	private PetriNetModel petriNetModel;

	/**
	 * Initializes the component, renders the UI and attaches event listeners.
	 * 
	 * @param fileSystemModel the app's filesystem model
	 * @param petriNetModel   the app's Petri net model
	 */
	public ToolbarView(
			FileSystemModel fileSystemModel,
			PetriNetModel petriNetModel) {
		this.fileSystemModel = fileSystemModel;
		this.petriNetModel = petriNetModel;
		renderToolbar();
		addFileSystemListener();
		addPetriNetListener();
	}

	/**
	 * Creates an icon button.
	 * 
	 * @param tooltip   a tooltip string
	 * @param iconUrl1x an icon in 1x resolution
	 * @param iconUrl2x an icon in 2x resolution
	 * @return          the icon button component
	 */
	private JButton createToolbarButton(	
			String tooltip,
			String iconUrl1x,
			String iconUrl2x) {
		JButton button = new JButton();
		BufferedImage img1x = null;
		BufferedImage img2x = null;
		try {
			img1x = ImageIO.read(getClass().getResource("/" + iconUrl1x));
			img2x = ImageIO.read(getClass().getResource("/" + iconUrl2x));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		ImageIcon icon = new ImageIcon(
				new BaseMultiResolutionImage(0, img1x, img2x)
				);
		button.setIcon(icon);
		button.setToolTipText(tooltip);
		button.setEnabled(false);
		return button;
	}

	/**
	 * Renders the UI.
	 */
	private void renderToolbar() {
		toolbar = new JToolBar();
		buttonPrevFile = createToolbarButton(
				"Vorherige Datei öffnen",
				"ic_prev_file_36x36_default.png",
				"ic_prev_file_36x36_default@2x.png"
				);
		buttonNextFile = createToolbarButton(
				"Nächste Datei öffnen",
				"ic_next_file_36x36_default.png",
				"ic_next_file_36x36_default@2x.png"
				);
		buttonReset = createToolbarButton(
				"Petrinetz zurücksetzen",
				"ic_reset_36x36_default.png",
				"ic_reset_36x36_default@2x.png"
				);
		buttonAddToken = createToolbarButton(
				"Marke hinzufügen",
				"ic_add_marker_36x36_default.png",
				"ic_add_marker_36x36_default@2x.png"
				);
		buttonRemoveToken = createToolbarButton(
				"Marke entfernen",
				"ic_remove_marker_36x36_default.png",
				"ic_remove_marker_36x36_default@2x.png"
				);
		buttonBoundednessAnalysis = createToolbarButton(
				"Beschränktheits-Analyse durchführen",
				"ic_boundedness_36x36_default.png",
				"ic_boundedness_36x36_default@2x.png"
				);
		buttonClearRG = createToolbarButton(
				"Erreichbarkeitsgraph löschen",
				"ic_clear_rg_36x36_default.png",
				"ic_clear_rg_36x36_default@2x.png");

		buttonClearText = createToolbarButton(
				"Textbereich löschen",
				"ic_clear_text_36x36_default.png",
				"ic_clear_text_36x36_default@2x.png");

		toolbar.setFloatable(false);
		toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
		buttonClearText.setEnabled(true);

		buttonPrevFile.setActionCommand("Open previous file");
		buttonNextFile.setActionCommand("Open next file");
		buttonReset.setActionCommand("Reset Marking");
		buttonAddToken.setActionCommand("Add Token");
		buttonRemoveToken.setActionCommand("Remove Token");
		buttonBoundednessAnalysis.setActionCommand("Boundedness Analysis");
		buttonClearRG.setActionCommand("Reset Reachability Graph");
		buttonClearText.setActionCommand("Clear Text Console");

		toolbar.add(buttonPrevFile);
		toolbar.add(buttonNextFile);
		toolbar.addSeparator();
		toolbar.add(buttonReset);
		toolbar.addSeparator();
		toolbar.add(buttonAddToken);
		toolbar.add(buttonRemoveToken);
		toolbar.addSeparator();
		toolbar.add(buttonBoundednessAnalysis);
		toolbar.addSeparator();
		toolbar.add(buttonClearRG);
		toolbar.add(buttonClearText);
	}

	/**
	 * Enables or disables the "Vorherige Datei öffnen" and "Nächste Datei
	 * öffnen" buttons in case these files actually exist or not.
	 */
	private void togglePrevNextFileButtons() {
		if (fileSystemModel.prevFile != null) {
			buttonPrevFile.setEnabled(true);
		} else {
			buttonPrevFile.setEnabled(false);
		}
		if (fileSystemModel.nextFile != null) {
			buttonNextFile.setEnabled(true);
		} else {
			buttonNextFile.setEnabled(false);
		}
	}

	/**
	 * Enables or disables the "Marke hinzufügen" and "Marke entferne" buttons
	 * in case the a place has been selected by the user.
	 */
	private void toggleTokenButtons() {
		if (petriNetModel.selectedPlace == null) {
			buttonAddToken.setEnabled(false);
			buttonRemoveToken.setEnabled(false);
		} else {
			buttonAddToken.setEnabled(true);
			if (petriNetModel.selectedPlace.currentTokens > 0) {
				buttonRemoveToken.setEnabled(true);
			} else {
				buttonRemoveToken.setEnabled(false);
			}
		}
	}

	/**
	 * Enables or disables the "Petrinetz zurücksetzen" button in case the
	 * Petri net has a custom marking that is different than the start marking.
	 */
	private void toggleResetButton() {
		if (petriNetModel.isAtCustomMarking) {
			buttonReset.setEnabled(true);
		} else {
			buttonReset.setEnabled(false);
		}
	}

	/**
	 * Enables the "Lösche Erreichbarkeitsgraph" button.
	 */
	private void toggleClearRGButton() {
		buttonClearRG.setEnabled(true);
	}

	/**
	 * Enables the "Textfeld leeren" button.
	 */
	private void toggleClearTextButton() {
		buttonClearText.setEnabled(true);
	}

	/**
	 * Enables the "Beschränktheits-Analyse" button.
	 */
	private void toggleBoundednessAnalysisButton() {
		buttonBoundednessAnalysis.setEnabled(true);
	}

	/**
	 * Attaches an event listener to the filesystem model.
	 * 
	 * @see    Event
	 */
	private void addFileSystemListener() {
		fileSystemModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.FILESYSTEM_NEW_FILE_OPENED) {
					togglePrevNextFileButtons();
					toggleResetButton();
					toggleTokenButtons();
					toggleClearRGButton();
					toggleClearTextButton();
					toggleBoundednessAnalysisButton();
				}
			}
		});
	}

	/**
	 * Attaches an event listener to the Petri net model.
	 * 
	 * @see    Event
	 */
	private void addPetriNetListener() {
		petriNetModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {			
				if (eventType == Event.PETRINET_USER_SELECTED_A_PLACE) {
					toggleTokenButtons();
				}
				if (eventType == Event.PETRINET_TRANSITION_FIRED ||
						eventType == Event.PETRINET_MARKING_EDITED ||
						eventType == Event.PETRINET_NEW_NET_LOADED) {
					toggleTokenButtons();
					toggleResetButton();
				}
			}
		});
	}
}
