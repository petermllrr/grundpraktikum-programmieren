package io.petermueller.petrinetz.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventListener;

public class MainFrameView {
	public JFrame mainFrame;
	public JMenuItem menuOpen;
	public JMenuItem menuQuit;
	public JMenuItem menuInfo;
	public JMenuItem menuReload;
	public JMenuItem menuBatchProcessing;
	public JButton buttonOpenSingle;
	public JButton buttonOpenMultiple;
	public JPanel petrinetContainer;

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
							"Nur Petrinetz Dateien (PNML) auswählen." +
							" Andere Dateitypen werden nicht unterstützt.");
					break;
				}
			}
		});
	}

	private void printConsoleOutput() {
		System.out.format("%-19s %s %n%-19s %s",
				"Java-Version:",
				System.getProperty("java.version"),
				"Working Directory:",
				System.getProperty("user.dir"));
		System.out.println();
	}

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

	private void setMainFrameToVisible() {
		mainFrame.setVisible(true);
	}
}