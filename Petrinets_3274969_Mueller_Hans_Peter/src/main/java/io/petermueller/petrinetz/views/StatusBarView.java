package io.petermueller.petrinetz.views;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventListener;

/**
 * Renders the status bar at the bottom of the app's main window. The status
 * bar view also registers event listeners to update itself when a new file is
 * opened or when the Petri net is edited by the user.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class StatusBarView {
	/**
	 * The panel in which the status bar is rendered.
	 */
	public JPanel statusBar;
	private JLabel labelCurrentDocument;
	private JLabel labelEdited;

	/**
	 * Initializes the component, renders the UI and calls methods to attach
	 * event listeners.
	 * 
	 * @param fileSystemModel the app's filesystem model
	 * @param petriNetModel   the app's Petri net model
	 */
	public StatusBarView(
			FileSystemModel fileSystemModel,
			PetriNetModel petriNetModel) {
		renderUI();
		addFileSystemListener(fileSystemModel);
		addPetriNetListener(petriNetModel);
	}

	/**
	 * Sets the status bar's text.
	 * 
	 * @param text the text to show in the status bar
	 */
	public void setText(String text) {
		labelCurrentDocument.setText(text);
	}

	/**
	 * Adds a label "edited" after the status bar's text content.
	 * 
	 * @param edited {@code true} if the label should appear, {@code false}
	 *               otherwise
	 */
	public void setEdited(boolean edited) {
		String text;
		Font font;
		if (edited) {
			text = "(bearbeitet)";
			font = labelEdited.getFont().deriveFont(Font.ITALIC);
		} else {
			text = "";
			font = labelEdited.getFont().deriveFont(Font.ROMAN_BASELINE);
		}
		labelEdited.setText(text);
		labelEdited.setFont(font);
		labelCurrentDocument.setFont(font);
	}

	/**
	 * Renders the UI.
	 */
	private void renderUI() {
		statusBar = new JPanel();
		BufferedImage img1x = null;
		BufferedImage img2x = null;
		try {
			img1x = ImageIO.read(getClass().getResource(
					"/ic_document_16x16.png"));
			img2x = ImageIO.read(getClass().getResource(
					"/ic_document_16x16@2x.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		ImageIcon icon = new ImageIcon(
				new BaseMultiResolutionImage(0, img1x, img2x));
		JLabel labelIcon = new JLabel(icon);
		labelCurrentDocument = new JLabel("Keine Datei geöffnet");
		labelEdited = new JLabel("");

		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.LINE_AXIS));
		statusBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 24));
		statusBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		statusBar.setBorder(null);

		statusBar.add(Box.createHorizontalStrut(8));
		statusBar.add(labelIcon);
		statusBar.add(Box.createHorizontalStrut(4));
		statusBar.add(labelCurrentDocument);
		statusBar.add(Box.createHorizontalStrut(4));
		statusBar.add(labelEdited);
	}

	/**
	 * Attaches an event listener to the app's filesystem model.
	 * 
	 * @param fileSystemModel the app's filesystem model
	 * @see   Event
	 */
	private void addFileSystemListener(FileSystemModel fileSystemModel) {
		fileSystemModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.FILESYSTEM_NEW_FILE_OPENED) {
					setText(fileSystemModel.getCurrentFileName());
					setEdited(false);
				}
			}
		});
	}

	/**
	 * Attaches an event listener to the app's Petri net model.
	 * 
	 * @param petriNetModel the app's Petri net model
	 * @see   Event
	 */
	private void addPetriNetListener(PetriNetModel petriNetModel) {
		petriNetModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.PETRINET_MARKING_EDITED) {
					if (petriNetModel.fileChanged) {
						setEdited(true);
					}
				}
			}
		});
	}
}