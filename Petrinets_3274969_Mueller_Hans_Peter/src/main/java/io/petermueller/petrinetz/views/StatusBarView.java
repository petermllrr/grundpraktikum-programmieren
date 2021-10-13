package io.petermueller.petrinetz.views;

import java.awt.Dimension;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class StatusBarView {
	public JPanel statusBar;
	private JLabel labelCurrentDocument;
	private JLabel labelEdited;
	
	public StatusBarView() {
		statusBar = new JPanel();
		BufferedImage img1x = null;
		BufferedImage img2x = null;
		try {
			img1x = ImageIO.read(getClass().getResource("/ic_document_16x16.png"));
			img2x = ImageIO.read(getClass().getResource("/ic_document_16x16@2x.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		ImageIcon icon = new ImageIcon(
			new BaseMultiResolutionImage(0, img1x, img2x)
		);
		JLabel labelIcon = new JLabel(icon);
		labelCurrentDocument = new JLabel("No file opened");
		labelEdited = new JLabel("");
		
		//statusBar.setBackground(Color.white);
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
	
	public void setText(String text) {
		labelCurrentDocument.setText(text);
	}
	
	public void setEdited(Boolean edited) {
		String text = (edited) ? "(edited)" : "";
		labelEdited.setText(text);
	}
}