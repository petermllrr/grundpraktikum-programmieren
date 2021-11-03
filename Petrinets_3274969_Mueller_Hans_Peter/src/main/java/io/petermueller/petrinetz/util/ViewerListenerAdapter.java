package io.petermueller.petrinetz.util;

import org.graphstream.ui.view.ViewerListener;

public abstract class ViewerListenerAdapter implements ViewerListener {
	@Override
	public void viewClosed(String viewName) {
	}

	@Override
	public void buttonPushed(String id) {
	}

	@Override
	public void buttonReleased(String id) {
	}

	@Override
	public void mouseOver(String id) {
	}

	@Override
	public void mouseLeft(String id) {
	}
}