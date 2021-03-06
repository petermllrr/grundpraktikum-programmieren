package io.petermueller.petrinetz.controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import io.petermueller.petrinetz.views.TextAreaView;

/**
 * Controller of the {@link TextAreaView}, handling user events in the text
 * area. Allows copying, cutting and clearing text.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class TextAreaController {
	private TextAreaView.PopupMenu popupMenu;
	private TextAreaView textAreaView;

	/**
	 * Initialized the component by assigning private attributes and calling
	 * further private functions.
	 * 
	 * @param textAreaView the text area view
	 */
	public TextAreaController(
			TextAreaView textAreaView) {
		this.popupMenu = textAreaView.popupMenuView;
		this.textAreaView = textAreaView;
		addMenuitemListener();
		addPopupListener();
	}

	/**
	 * Attaches a {@MenuItemListener} at the individuel context menu items.
	 */
	private void addMenuitemListener() {
		MenuItemListener menuItemListener = new MenuItemListener();
		popupMenu.menuCopy.setActionCommand("Copy");
		popupMenu.menuCut.setActionCommand("Cut");
		popupMenu.menuClear.setActionCommand("Clear");
		popupMenu.menuCopy.addActionListener(menuItemListener);
		popupMenu.menuCut.addActionListener(menuItemListener);
		popupMenu.menuClear.addActionListener(menuItemListener);
	}

	/**
	 * Attached a {@PopupListener} at the text field to listen for mouse events.
	 */
	private void addPopupListener() {
		textAreaView.inputField.addMouseListener(new PopupListener());
	}

	/**
	 * An {@link ActionListener} for the individual menu entries like copying,
	 * cutting and clearing the text.
	 */
	private class MenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Copy":
				copyToClipboard();
				break;
			case "Cut":
				copyToClipboard();
				textAreaView.cutText();
				break;
			case "Clear":
				textAreaView.clear();
			}
		}

		/**
		 * Gets the currently selected text from the
		 * {@link TextAreaView#inputField} and copies it to the user's
		 * clipboard.
		 */
		private void copyToClipboard() {
			String selected = textAreaView.inputField.getSelectedText();
			StringSelection stringSelection = new StringSelection(selected);
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}
	}

	/**
	 * A {@link MouseAdapter} which opens the right-click context menu in the
	 * text area.
	 */
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