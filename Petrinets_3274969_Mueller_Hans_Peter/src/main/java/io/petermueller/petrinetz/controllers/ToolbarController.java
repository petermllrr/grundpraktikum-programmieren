package io.petermueller.petrinetz.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import io.petermueller.petrinetz.models.filesystem.FilesystemModel;
import io.petermueller.petrinetz.views.ToolbarView;

public class ToolbarController {
	public ToolbarController(
		ToolbarView view,
		FilesystemModel fileSystemModel) {
		view.buttonPrevFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileSystemModel.readNewFile(fileSystemModel.prevFile);
			}
		});
		
		view.buttonNextFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileSystemModel.readNewFile(fileSystemModel.nextFile);
			}
		});
		
		fileSystemModel.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.NEW_FILE_OPENED) {
					if (fileSystemModel.prevFile != null) {
						view.buttonPrevFile.setEnabled(true);
					} else {
						view.buttonPrevFile.setEnabled(false);
					}
					if (fileSystemModel.nextFile != null) {
						view.buttonNextFile.setEnabled(true);
					} else {
						view.buttonNextFile.setEnabled(false);
					}
				}
			}
		});
	}
}