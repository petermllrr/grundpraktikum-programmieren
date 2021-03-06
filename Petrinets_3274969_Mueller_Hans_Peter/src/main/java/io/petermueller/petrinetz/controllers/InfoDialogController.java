package io.petermueller.petrinetz.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import io.petermueller.petrinetz.views.InfoDialogView;

/**
 * Controller of the {@link InfoDialogView}, handling user events happening in
 * the info dialog.
 * 
 * @author Hans Peter Müller (3274969)
 * @see InfoDialogView
 */
public class InfoDialogController {
	/**
	 * Public constructor which registers event listeners and the default close
	 * interaction in the {@link InfoDialogView}.
	 * 
	 * @param infoDialogView the info dialog view component
	 */
	public InfoDialogController(InfoDialogView infoDialogView) {
		infoDialogView.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		infoDialogView.buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoDialogView.frame.dispose();
			}
		});
	}
}