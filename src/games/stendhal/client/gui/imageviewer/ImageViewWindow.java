/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.imageviewer;

import java.awt.Dimension;

import javax.swing.SwingUtilities;

import games.stendhal.client.stendhal;
import games.stendhal.client.gui.InternalManagedWindow;
import games.stendhal.client.gui.j2DClient;

/**
 * Opens an image resource at a given URL, and displays it in the client.
 *
 * @author timothyb89
 */
public class ImageViewWindow extends InternalManagedWindow {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1890235227090651033L;

	/**
	 * The padding of the window, in pixels, when generating the maximum size.
	 */
	private static final int PADDING = 100;

	/**
	 * creates a new ImageViewWindow
	 *
	 * @param title title of the window
	 * @param viewPanel View Panel
	 */
	public ImageViewWindow(String title, ViewPanel viewPanel) {
		super("examine", title);

		viewPanel.prepareView(genMaxSize());
		setContent(viewPanel);

		/*
		 * Generating the window was likely triggered an event from the network
		 * thread; do the rest of the work in EDT.
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				j2DClient.get().addWindow(ImageViewWindow.this);
				center();
				getParent().validate();
			}
		});
	}

	/**
	 * Calculate maximum size for a window that fits on the game screen.
	 *
	 * @return size
	 */
	private Dimension genMaxSize() {
		Dimension displaySize = stendhal.getDisplaySize();
		final int width = (int) (displaySize.getWidth() - PADDING);
		final int height = (int) (displaySize.getHeight() - PADDING);
		return new Dimension(width, height);
	}
}
