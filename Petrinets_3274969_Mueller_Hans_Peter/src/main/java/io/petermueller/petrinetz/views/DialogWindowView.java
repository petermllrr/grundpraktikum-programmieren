package io.petermueller.petrinetz.views;

import javax.swing.*;

/**
 * Creates a simple Swing Option Pane.
 * 
 * @author Hans Peter Müller (3274969)
 * @see    JOptionPane
 */
public class DialogWindowView {
	/**
	 * Initializes the Swing option pane with a custom message.
	 * 
	 * @param frame   a frame from which to spawn the option pane
	 * @param message a message to show in the option pane
	 */
	public DialogWindowView(JFrame frame, String message) {
		JOptionPane.showMessageDialog(frame, message);
	}
}
