package io.petermueller.petrinetz.views;

import javax.swing.*;

public class DialogWindowView {
	public DialogWindowView(JFrame frame, String message) {
		JOptionPane.showMessageDialog(frame, message);
	}
}
