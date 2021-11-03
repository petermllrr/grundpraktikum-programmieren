package io.petermueller.petrinetz.controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import io.petermueller.petrinetz.views.TextAreaView;

public class TextAreaController {
	private TextAreaView.PopupMenu popupMenu;
	private TextAreaView textAreaView;

	public TextAreaController(
			TextAreaView textAreaView) {
		this.popupMenu = textAreaView.popupMenuView;
		this.textAreaView = textAreaView;
		addMenuitemListener();
		addPopupListener();
	}

	private void addMenuitemListener() {
		MenuItemListener popupListener = new MenuItemListener();
		popupMenu.menuCopy.setActionCommand("Copy");
		popupMenu.menuCut.setActionCommand("Cut");
		popupMenu.menuCopy.addActionListener(popupListener);
		popupMenu.menuCut.addActionListener(popupListener);
	}

	private void addPopupListener() {
		textAreaView.inputField.addMouseListener(new PopupListener());
	}

	private class MenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Copy":
				copyToClipboard();
				break;
			case "Cut":
				copyToClipboard();
				removeSelectionFromText();
				break;
			}
		}

		private void copyToClipboard() {
			String selected = textAreaView.inputField.getSelectedText();
			StringSelection stringSelection = new StringSelection(selected);
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}

		private void removeSelectionFromText() {
			String selected = textAreaView.inputField.getSelectedText();
			textAreaView.cutText(selected);
		}
	}

	private class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu.popup.show(
						e.getComponent(),
						e.getX(),
						e.getY());
			}
		}
	}
}