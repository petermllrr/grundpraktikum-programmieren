package io.petermueller.petrinetz.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import io.petermueller.petrinetz.views.InfoDialogView;

public class InfoDialogController {
	public InfoDialogController(JFrame mainFrame) {
		InfoDialogView infoDialogView = new InfoDialogView(mainFrame);
		
		infoDialogView.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		infoDialogView.buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoDialogView.frame.dispose();
			}
		});
	}
}