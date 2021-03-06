package io.petermueller.petrinetz.controllers;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.util.BatchProcessing;
import io.petermueller.petrinetz.views.InfoDialogView;
import io.petermueller.petrinetz.views.MainFrameView;
import io.petermueller.petrinetz.views.TextAreaView;

/**
 * Controller of the {@link MainFrameView}, handling user events in the menu
 * bar. Keyboard shortcuts and the default close operation are registered as
 * well. Main entry points into the app's functionality are the menu commands
 * defined in the {@link MenuListener}.
 * 
 * @author Hans Peter Müller (3274969)
 * @see MainFrameView
 */
public class MainFrameController {
	private MainFrameView mainFrameView;
	private FileSystemModel fileSystemModel;
	private PetriNetModel petriNetModel;
	private TextAreaView textAreaView;

	/**
	 * Public constructor. Calls further private methods to register event
	 * listeneres and keyboard shortcuts.
	 * 
	 * @param mainFrameView the main frame view
	 * @param fileSystemModel the filesystem model
	 * @param petriNetModel the main petri net model
	 * @param textAreaView the view of the text area at the bottom of the GUI
	 */
	public MainFrameController(
			MainFrameView mainFrameView,
			FileSystemModel fileSystemModel,
			PetriNetModel petriNetModel,
			TextAreaView textAreaView) {
		this.mainFrameView = mainFrameView;
		this.fileSystemModel = fileSystemModel;
		this.petriNetModel = petriNetModel;
		this.textAreaView = textAreaView;
		addCloseOperation();
		addMenuListeners();
		addMenuShortcuts();
	}

	/**
	 * Registers the close operation on the main app frame.
	 */
	private void addCloseOperation() {
		mainFrameView.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Registers the {@link MenuListener} to the main menu.
	 */
	private void addMenuListeners() {
		MenuListener menuListener = new MenuListener();
		mainFrameView.menuOpen.addActionListener(menuListener);
		mainFrameView.menuBatchProcessing.addActionListener(menuListener);
		mainFrameView.menuQuit.addActionListener(menuListener);
		mainFrameView.menuInfo.addActionListener(menuListener);
		mainFrameView.menuReload.addActionListener(menuListener);
	}

	/**
	 * Registers keyboard shortcuts for the different menu interactions. The
	 * default meta key of the current operating system is used.
	 * 
	 * @see Toolkit#getMenuShortcutKeyMaskEx
	 */
	private void addMenuShortcuts() {
		int maskKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
		mainFrameView.menuOpen.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, maskKey));
		mainFrameView.menuBatchProcessing.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_A, maskKey));
		mainFrameView.menuQuit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Q, maskKey));
		mainFrameView.menuInfo.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_I, maskKey));
		mainFrameView.menuReload.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, maskKey));
	}

	/**
	 * {@link ActionListener} to intercept user interactions with the main menu.
	 * The {@code Open…} and {@code Analyse Multiple Files…} commands are the
	 * primary entry points of the app.
	 */
	private class MenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Öffnen…":
				PetriNetModel loadedPetriNet = fileSystemModel.readNewFile(
						mainFrameView.mainFrame);
				if (loadedPetriNet != null) {
					petriNetModel.load(loadedPetriNet);
					fileSystemModel.notifyGui();
				}
				break;
			case "Analyse mehrerer Dateien…":
				List<PetriNetModel> petriNets = fileSystemModel.readNewFiles(
						mainFrameView.mainFrame);
				if (petriNets != null) {
					BatchProcessing batch = new BatchProcessing(petriNets);
					textAreaView.printBatchResults(batch);
				}
				break;
			case "Beenden":
				System.exit(0);
				break;
			case "Info…":
				InfoDialogView infoDialogView = new InfoDialogView(
						mainFrameView.mainFrame);
				new InfoDialogController(infoDialogView);
				break;
			case "Neu laden":
				File currentFile = fileSystemModel.getCurrentFile();
				petriNetModel.load(fileSystemModel.readNewFile(currentFile));
				fileSystemModel.notifyGui();
			}
		}
	}
}