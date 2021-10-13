package io.petermueller.petrinetz.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import io.petermueller.petrinetz.models.filesystem.FilesystemModel;
import io.petermueller.petrinetz.models.petrinet.*;
import io.petermueller.petrinetz.views.MainFrameView;
import io.petermueller.petrinetz.views.PetriNetView;
import io.petermueller.petrinetz.views.StatusBarView;
import io.petermueller.petrinetz.views.TextAreaView;
import io.petermueller.petrinetz.views.ToolbarView;

public class MainFrameController {
	protected PetriNetModel petriNetModel;
	protected FilesystemModel filesystemModel;
	protected MainFrameView mainFrameView;
	protected ToolbarView toolbarView;
	protected StatusBarView statusBarView;
	protected TextAreaView textAreaView;
	protected PetriNetView petriNetView;
	protected ToolbarController toolbarController;
	
	public MainFrameController() {
		petriNetModel = new PetriNetModel();
		filesystemModel = new FilesystemModel(petriNetModel);
		mainFrameView = new MainFrameView();
		
		mainFrameView.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initializeMenu();
		renderMainLayout();
	}
	
	protected void initializeMenu() {
		mainFrameView.open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				filesystemModel.readNewFile(mainFrameView.mainFrame);
			}
		});
		
		mainFrameView.quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			};
		});
		
		mainFrameView.info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				new InfoDialogController(mainFrameView.mainFrame);
			}
		});
	}
	
	protected void renderMainLayout() {
		statusBarView = new StatusBarView();
		textAreaView = new TextAreaView();
		petriNetView = new PetriNetView();
		toolbarView = new ToolbarView();
		toolbarController = new ToolbarController(
			toolbarView,
			filesystemModel
		);
		
		mainFrameView.renderMainLayout(
			toolbarView.toolbar,
			statusBarView.statusBar,
			textAreaView.textArea,
			petriNetView.panel
		);
		
		filesystemModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.NEW_FILE_OPENED) {
					statusBarView.setText(filesystemModel.getCurrentFile());
					textAreaView.setText(petriNetModel.getContentsAsString());
					petriNetView.renderPetriNet(
						petriNetModel.transitions,
						petriNetModel.places,
						petriNetModel.arcs
					);
				}
			}
		});
	}
	
	protected void renderWelcomeScreen() {
		mainFrameView.showEmptyLayout();
		
		mainFrameView.buttonOpenSingle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderMainLayout();
			}
		});
	}
}