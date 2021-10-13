package io.petermueller.petrinetz.views;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TextAreaView {
	public JScrollPane textArea;
	private JTextArea inputField;
	
	public TextAreaView() {
		inputField = new JTextArea();
		textArea = new JScrollPane(inputField);
		textArea.setBorder(BorderFactory.createLineBorder(new Color(0xDADCE0), 1));
		inputField.setText("Text area placeholder");
		inputField.setBackground(Color.white);
		inputField.setBorder(new EmptyBorder(8, 8, 8, 8));
		inputField.setFont(new Font("monospaced", Font.PLAIN, 13));
		inputField.setWrapStyleWord(true);
		inputField.setLineWrap(true);
	}
	
	public void setText(String text) {
		inputField.setText(text);
		inputField.setCaretPosition(inputField.getDocument().getLength());
	}
}
