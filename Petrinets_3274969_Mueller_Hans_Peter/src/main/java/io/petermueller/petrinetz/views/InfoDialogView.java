package io.petermueller.petrinetz.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;

/**
 * Creates an "About" info dialog frame which presents the App name, the current
 * user's working directory as well as the system's Java version.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class InfoDialogView {
	/**
	 * 
	 */
	public JFrame frame;
	/**
	 * 
	 */
	public JButton buttonClose;
	private JFrame mainFrame;

	/**
	 * Initializes the component and creates the info dialog frame.
	 * 
	 * @param mainFrame a frame on which the info dialog is centered
	 */
	public InfoDialogView(JFrame mainFrame) {
		this.mainFrame = mainFrame;
		renderUI();
	}

	/**
	 * Renders the component's contents.
	 */
	private void renderUI() {
		frame = new JFrame("Info");
		Box panel = Box.createVerticalBox();
		JLabel headline = new JLabel();
		Box rowAuthor = Box.createHorizontalBox();
		JLabel labelAuthor = new JLabel();
		JLabel author = new JLabel();
		Box rowVersion = Box.createHorizontalBox();
		JLabel labelVersion = new JLabel();
		JLabel version = new JLabel();
		Box rowDirectory = Box.createHorizontalBox();
		JLabel labelDirectory = new JLabel();
		JScrollPane directoryScroll = new JScrollPane();
		JTextArea directoryTextArea = new JTextArea();
		buttonClose = new JButton();

		frame.setMinimumSize(new Dimension(450, 280));
		frame.setPreferredSize(new Dimension(620, 250));
		frame.setLocationRelativeTo(frame);

		panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
		panel.setMinimumSize(new Dimension(400, 131));

		Font headlineFont = headline.getFont()
				.deriveFont(Font.BOLD)
				.deriveFont(18F);
		headline.setFont(headlineFont);
		headline.setText("Petrinet App");
		panel.add(headline);

		rowAuthor.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
		rowAuthor.setAlignmentX(0.0F);

		labelAuthor.setText("Autor:");
		labelAuthor.setAlignmentY(0.0F);
		labelAuthor.setMaximumSize(new Dimension(135, 16));
		labelAuthor.setMinimumSize(new Dimension(135, 16));
		labelAuthor.setPreferredSize(new Dimension(135, 16));
		rowAuthor.add(labelAuthor);

		author.setText("Hans Peter Müller (3274969)");
		author.setAlignmentY(0.0F);
		rowAuthor.add(author);

		panel.add(rowAuthor);

		rowVersion.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
		rowVersion.setAlignmentX(0.0F);

		labelVersion.setText("Java-Version:");
		labelVersion.setAlignmentY(0.0F);
		labelVersion.setMaximumSize(new Dimension(135, 16));
		labelVersion.setMinimumSize(new Dimension(135, 16));
		labelVersion.setPreferredSize(new Dimension(135, 16));
		rowVersion.add(labelVersion);

		version.setText(System.getProperty("java.version"));
		version.setAlignmentY(0.0F);
		rowVersion.add(version);

		panel.add(rowVersion);

		rowDirectory.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
		rowDirectory.setAlignmentX(0.0F);
		rowDirectory.setMinimumSize(new Dimension(600, 16));

		labelDirectory.setText("Arbeitsverzeichnis:");
		labelDirectory.setAlignmentY(0.0F);
		labelDirectory.setMaximumSize(new Dimension(135, 16));
		labelDirectory.setMinimumSize(new Dimension(135, 16));
		labelDirectory.setPreferredSize(new Dimension(135, 16));
		rowDirectory.add(labelDirectory);

		directoryScroll.setBorder(null);
		directoryScroll.setAlignmentY(0.0F);

		directoryTextArea.setEditable(false);
		directoryTextArea.setBackground(new Color(238, 238, 238));
		directoryTextArea.setColumns(5);
		directoryTextArea.setLineWrap(true);
		directoryTextArea.setText(System.getProperty("user.dir"));
		directoryTextArea.setAlignmentX(0.0F);
		directoryTextArea.setAlignmentY(0.0F);
		directoryTextArea.setAutoscrolls(false);
		directoryTextArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		directoryTextArea.setDragEnabled(false);
		directoryScroll.setViewportView(directoryTextArea);

		rowDirectory.add(directoryScroll);

		panel.add(rowDirectory);

		buttonClose.setText("Schließen");

		panel.add(buttonClose);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setLocationRelativeTo(mainFrame);
		frame.pack(); 
		frame.setVisible(true);
	}
}
