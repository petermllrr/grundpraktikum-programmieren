package io.petermueller.petrinetz.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.Border;

public class MainFrameView {
	public JFrame mainFrame;
	public JMenuItem open;
	public JMenuItem quit;
	public JMenuItem info;
	public JButton buttonOpenSingle;
	public JButton buttonOpenMultiple;
	public JPanel petrinetContainer;
	
	public MainFrameView() {
		System.out.format("%-19s %s %n%-19s %s",
			"Java-Version:",
			System.getProperty("java.version"),
			"Working Directory:",
			System.getProperty("user.dir")
		);
		System.out.println();
		
		mainFrame = new JFrame("Peter Müller — 3274969");
		mainFrame.setMinimumSize(new Dimension(800, 600));
		mainFrame.setSize(new Dimension(1024, 768));
		mainFrame.setLocationRelativeTo(null);
		showMenuBar();
		mainFrame.setVisible(true);
	}
	
	public void showMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");	
		JMenu help = new JMenu("Help");
		open = new JMenuItem("Open…");	
		JMenuItem reload = new JMenuItem("Reload");	
		JMenuItem analyzeMultiple = new JMenuItem("Analyze Multiple Files…");	
		quit = new JMenuItem("Quit");	
		info = new JMenuItem("Info…");
		/*
		menuBar.setBorder(BorderFactory.createMatteBorder(
			0, 0, 2, 0,
			new Color(218, 220, 224)
		));
		*/
		
		menuBar.add(file);
		file.add(open);
		file.add(analyzeMultiple);
		file.addSeparator();
		file.add(reload);
		file.addSeparator();
		file.add(quit);
		menuBar.add(help);
		help.add(info);
		mainFrame.setJMenuBar(menuBar); 
	}
	
	public void showEmptyLayout() {
		JPanel panelBg = new JPanel();
		JPanel centerPanel = new JPanel();
		JLabel headline = new JLabel("Petri Net Visualizer");
		JPanel panelOpenSingle = new JPanel();
		JPanel panelOpenSingleText = new JPanel();
		JPanel panelOpenSingleButton = new JPanel();
		JPanel panelOpenMultiple = new JPanel();
		JPanel panelOpenMultipleText = new JPanel();
		JPanel panelOpenMultipleButton = new JPanel();
		JLabel labelOpenSingleHeadline = new JLabel(
			"Open And View a Petri Net"
		);
		JTextArea textOpenSingleText = new JTextArea(
			"Toggle transitions, inspect partial reachability graphs and "
			+ "analyze the boundedness of a Petri net."
		);
		buttonOpenSingle = new JButton("Open File…");
		JLabel labelOpenMultipleHeadline = new JLabel(
			"Batch-Analyze Multiple Files"
		);
		JTextArea textOpenMultipleText = new JTextArea(
			"Conduct a boundedness analysis of all PNML files in a "
			+ "directory at once."
		);
		buttonOpenMultiple = new JButton("Select Directory…");
		
		panelOpenSingle.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelOpenSingle.setLayout(new BoxLayout(
			panelOpenSingle,
			BoxLayout.LINE_AXIS)
		);
		panelOpenSingleText.setLayout(new BoxLayout(
			panelOpenSingleText,
			BoxLayout.PAGE_AXIS)
		);
		panelOpenSingleButton.setLayout(new BoxLayout(
			panelOpenSingleButton,
			BoxLayout.LINE_AXIS)
		);
		panelOpenMultiple.setLayout(new BoxLayout(
			panelOpenMultiple,
			BoxLayout.LINE_AXIS)
		);
		panelOpenMultipleText.setLayout(new BoxLayout(
			panelOpenMultipleText,
			BoxLayout.PAGE_AXIS)
		);
		panelOpenMultipleButton.setLayout(new BoxLayout(
			panelOpenMultipleButton,
			BoxLayout.LINE_AXIS)
		);
		
		panelBg.setBackground(Color.white);
		panelBg.setLayout(new BoxLayout(
			panelBg,
			BoxLayout.PAGE_AXIS
		));
		centerPanel.setMaximumSize(new Dimension(500, 220));
		centerPanel.setMinimumSize(new Dimension(500, 220));
		centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
		centerPanel.setBackground(Color.white);
		panelOpenSingle.setBackground(Color.white);
		panelOpenSingleText.setBackground(Color.white);
		panelOpenSingleButton.setBackground(Color.white);
		panelOpenMultiple.setBackground(Color.white);
		panelOpenMultipleText.setBackground(Color.white);
		panelOpenMultipleButton.setBackground(Color.white);
		headline.setFont(new Font("Lucida Grande", 1, 24));
		headline.setMaximumSize(new Dimension(5000, 5000));
		headline.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelOpenSingleHeadline.setFont(new Font("Lucida Grande", 1, 13));
		textOpenSingleText.setEditable(false);
		textOpenSingleText.setColumns(10);
		textOpenSingleText.setLineWrap(true);
		textOpenSingleText.setRows(2);
		textOpenSingleText.setWrapStyleWord(true);
		textOpenSingleText.setAlignmentX(Component.LEFT_ALIGNMENT);
		textOpenSingleText.setDragEnabled(false);
		textOpenSingleText.setFocusable(false);
		textOpenSingleText.setMaximumSize(null);
		textOpenSingleText.setMinimumSize(null);
		textOpenSingleText.setPreferredSize(null);
		textOpenSingleText.setSize(new java.awt.Dimension(50, 0));
		labelOpenMultipleHeadline.setFont(new Font("Lucida Grande", 1, 13));
		textOpenMultipleText.setEditable(false);
		textOpenMultipleText.setColumns(10);
		textOpenMultipleText.setLineWrap(true);
		textOpenMultipleText.setRows(2);
		textOpenMultipleText.setWrapStyleWord(true);
		textOpenMultipleText.setAlignmentX(Component.LEFT_ALIGNMENT);
		textOpenMultipleText.setDragEnabled(false);
		textOpenMultipleText.setFocusable(false);
		textOpenMultipleText.setMaximumSize(null);
		textOpenMultipleText.setMinimumSize(null);
		textOpenMultipleText.setPreferredSize(null);
		textOpenMultipleText.setSize(new java.awt.Dimension(50, 0));
		
		panelBg.add(Box.createVerticalGlue());
		panelBg.add(centerPanel);
		panelBg.add(Box.createVerticalGlue());
		
		centerPanel.add(headline);
		
		centerPanel.add(Box.createRigidArea(new Dimension(0, 32)));
		centerPanel.add(panelOpenSingle);
		panelOpenSingle.add(panelOpenSingleText);
		panelOpenSingle.add(Box.createRigidArea(new Dimension(12, 0)));
		panelOpenSingle.add(panelOpenSingleButton);
		panelOpenSingleText.add(labelOpenSingleHeadline);
		panelOpenSingleText.add(Box.createRigidArea(new Dimension(0, 4)));
		panelOpenSingleText.add(textOpenSingleText);
		panelOpenSingleButton.add(buttonOpenSingle);
		
		centerPanel.add(Box.createRigidArea(new Dimension(0, 24)));
		centerPanel.add(panelOpenMultiple);
		panelOpenMultiple.add(panelOpenMultipleText);
		panelOpenMultiple.add(Box.createRigidArea(new Dimension(12, 0)));
		panelOpenMultiple.add(panelOpenMultipleButton);
		panelOpenMultipleText.add(labelOpenMultipleHeadline);
		panelOpenSingleText.add(Box.createRigidArea(new Dimension(0, 4)));
		panelOpenMultipleText.add(textOpenMultipleText);
		panelOpenMultipleButton.add(buttonOpenMultiple);
		
		mainFrame.add(panelBg);
	}
	
	public void renderMainLayout(
			JToolBar toolbar,
			JPanel statusBar,
			JScrollPane textArea,
			JPanel petriNet) {
		mainFrame.getContentPane().removeAll();
		
		JPanel panelBg = new JPanel();
		JPanel EGPlaceholder = new JPanel();
		JSplitPane splitPaneHorizontal = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT,
			petriNet,
			EGPlaceholder);
		JSplitPane splitPaneVertical = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT,
			splitPaneHorizontal,
			textArea);
		
		panelBg.setBackground(Color.white);
		panelBg.setLayout(new BoxLayout(
			panelBg,
			BoxLayout.PAGE_AXIS
		));
		splitPaneHorizontal.setDividerLocation(0.5);
		splitPaneHorizontal.setResizeWeight(0.5);
		//splitPaneHorizontal.setBackground(new Color(218, 220, 224));
		splitPaneHorizontal.setBorder(null);
		splitPaneVertical.setDividerLocation(0.66);
		splitPaneVertical.setResizeWeight(0.66);
		splitPaneVertical.setAlignmentX(Component.LEFT_ALIGNMENT);
		//splitPaneVertical.setBackground(new Color(218, 220, 224));
		splitPaneVertical.setBorder(null);
		toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
		statusBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		EGPlaceholder.setBackground(Color.white);
		Border outline = BorderFactory.createLineBorder(new Color(0xDADCE0), 1);
		EGPlaceholder.setBorder(outline);
		
		panelBg.add(toolbar);
		panelBg.add(splitPaneVertical);
		panelBg.add(statusBar);
		EGPlaceholder.add(new JLabel("EG placeholder"));
		
		mainFrame.add(panelBg);
		mainFrame.revalidate();
		mainFrame.repaint();
	}
}