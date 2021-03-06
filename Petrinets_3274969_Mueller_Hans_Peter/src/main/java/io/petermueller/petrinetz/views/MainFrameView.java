package io.petermueller.petrinetz.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventListener;

/**
 * Creates the app's main frame window. This is the main view which is
 * responsible for placing other sub-views into their place. More specifially,
 * the following actions are performed:
 * 
 * <ul><li>Outputs the Java version and user's working directory to the
 * console.</li>
 * <li>Creates the main window and adjusts the frame's size relative to the
 * user's screen resolution.</li>
 * <li>Initializes the app menu.</li>
 * <li>Registers an event listener to the filesystem model</li>
 * <li>Renders the main layout and places other views in their layout
 * containers.</li>
 * </ul>
 * 
 * @author Hans Peter Müller (3274969)
 * @see    FileSystemModel
 */
public class MainFrameView {
	/**
	 * The app's main frame window.
	 * 
	 * @see JFrame
	 */
	public JFrame mainFrame;
	/**
	 * The menu option "Öffnen…".
	 */
	public JMenuItem menuOpen;
	/**
	 * The menu option "Beenden".
	 */
	public JMenuItem menuQuit;
	/**
	 * The menu option "Info…".
	 */
	public JMenuItem menuInfo;
	/**
	 * The menu option "Neu laden".
	 */
	public JMenuItem menuReload;
	/**
	 * The menu option "Analyse mehrerer Dateien…".
	 */
	public JMenuItem menuBatchProcessing;

	/**
	 * Initializes the component and calls further methods to perform sub-tasks.
	 * 
	 * @param fileSystemModel the main file system model of the app
	 * @param toolbarView     the toolbar view
	 * @param statusBarView   the status bar view
	 * @param textAreaView    the text area view
	 * @param petriNetView    the Petri net view
	 * @param rGraphView      the reachability graph view
	 */
	public MainFrameView(
			FileSystemModel fileSystemModel,
			ToolbarView toolbarView,
			StatusBarView statusBarView,
			TextAreaView textAreaView,
			PetriNetView petriNetView,
			RGraphView rGraphView) {
		printConsoleOutput();
		renderMainFrame();
		renderMenuBar();
		addFileSystemListener(fileSystemModel);
		renderMainLayout(
				toolbarView.toolbar,
				statusBarView.statusBar,
				textAreaView.textArea,
				petriNetView.panel,
				rGraphView.panel);
		setMainFrameToVisible();
	}

	/**
	 * Renders the app's menu bar.
	 */
	private void renderMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("Datei");	
		JMenu help = new JMenu("Hilfe");
		menuOpen = new JMenuItem("Öffnen…");	
		menuReload = new JMenuItem("Neu laden");	
		menuBatchProcessing = new JMenuItem("Analyse mehrerer Dateien…");	
		menuQuit = new JMenuItem("Beenden");	
		menuInfo = new JMenuItem("Info…");

		menuReload.setEnabled(false);

		menuBar.add(file);
		file.add(menuOpen);
		file.add(menuBatchProcessing);
		file.addSeparator();
		file.add(menuReload);
		file.addSeparator();
		file.add(menuQuit);
		menuBar.add(help);
		help.add(menuInfo);
		mainFrame.setJMenuBar(menuBar); 
	}

	/**
	 * Creates the main layout and places sub views into their layout
	 * containers.
	 * 
	 * @param toolbar   the toolbar view
	 * @param statusBar the statusbar view
	 * @param textArea  the text area view
	 * @param petriNet  the Petri net view
	 * @param rGraph    the reachability graph view
	 */
	private void renderMainLayout(
			JToolBar toolbar,
			JPanel statusBar,
			JScrollPane textArea,
			JPanel petriNet,
			JPanel rGraph) {
		JPanel panelBg = new JPanel();
		JSplitPane splitPaneHorizontal = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				petriNet,
				rGraph);
		JSplitPane splitPaneVertical = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				splitPaneHorizontal,
				textArea);

		panelBg.setBackground(Color.white);
		panelBg.setLayout(new BoxLayout(
				panelBg,
				BoxLayout.PAGE_AXIS));
		splitPaneHorizontal.setDividerLocation(0.5);
		splitPaneHorizontal.setResizeWeight(0.5);
		splitPaneHorizontal.setBorder(null);
		splitPaneVertical.setDividerLocation(0.66);
		splitPaneVertical.setResizeWeight(0.66);
		splitPaneVertical.setAlignmentX(Component.LEFT_ALIGNMENT);
		splitPaneVertical.setBorder(null);
		toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
		statusBar.setAlignmentX(Component.LEFT_ALIGNMENT);

		panelBg.add(toolbar);
		panelBg.add(splitPaneVertical);
		panelBg.add(statusBar);

		mainFrame.getContentPane().add(panelBg);
	}

	/**
	 * Attaches an event listener at the file system model.
	 * 
	 * @param fileSystemModel
	 * @see   FileSystemModel
	 * @see   Event
	 */
	private void addFileSystemListener(FileSystemModel fileSystemModel) {
		fileSystemModel.addEventListener(new EventListener() {
			@SuppressWarnings("incomplete-switch")
			@Override
			public void event(Event eventType) {
				switch (eventType) {
				case FILESYSTEM_NEW_FILE_OPENED:
					menuReload.setEnabled(true);
					break;
				case FILESYSTEM_INVALID_FILE:
					new DialogWindowView(mainFrame,
							"Es können nur PNML Dateien geöffnet werden.");
					break;
				}
			}
		});
	}

	/**
	 * Prints the java version and the working directory to the console.
	 */
	private void printConsoleOutput() {
		System.out.format("%-19s %s %n%-19s %s",
				"Java-Version:",
				System.getProperty("java.version"),
				"Working Directory:",
				System.getProperty("user.dir"));
		System.out.println();
	}

	/**
	 * Creates the app's mainframe and sets the size relative the the user's
	 * display resolution.
	 */
	private void renderMainFrame() {
		mainFrame = new JFrame("Hans Peter Müller — 3274969");
		double heightPerc = 0.8; 
		double aspectRatio = 3.0 / 2.0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * heightPerc);
		int w = (int) (h * aspectRatio);
		mainFrame.setBounds(
				(screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);
		mainFrame.setLocationRelativeTo(null);
	}

	/**
	 * Sets the main frame to visible.
	 */
	private void setMainFrameToVisible() {
		mainFrame.setVisible(true);
	}
}