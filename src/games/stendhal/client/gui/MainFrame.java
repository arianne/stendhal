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
package games.stendhal.client.gui;

import java.awt.GraphicsConfiguration;
import java.awt.MouseInfo;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import games.stendhal.client.stendhal;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.common.Debug;

/**
 * Utility class for preparing the main game window.
 */
final class MainFrame {
	/** Never called. */
	private MainFrame() {
	}

	/**
	 * Set the window icon.
	 *
	 * @param frame window
	 */
	private static void setIcon(JFrame frame) {
		final URL url = DataLoader.getResource(
				ClientGameConfiguration.get("GAME_ICON"));
		frame.setIconImage(new ImageIcon(url).getImage());
	}

	/**
	 * Set the window title.
	 *
	 * @param frame window
	 */
	private static void setTitle(JFrame frame) {
		String preRelease = "";
		if (Debug.PRE_RELEASE_VERSION != null) {
			preRelease = " - " + Debug.PRE_RELEASE_VERSION;
		}
		frame.setTitle(ClientGameConfiguration.get("GAME_NAME") + " "
				+ stendhal.VERSION + preRelease
				+ " - a multiplayer online game using Arianne");
	}

	/**
	 * Prepare a frame for use as the main window, or create a new one if
	 * needed.
	 *
	 * @param frame frame to be used as the main game window. If
	 * 	<code>null</code>, then a new frame will be created
	 * @return frame suitable for use as the main game window
	 */
	static JFrame prepare(JFrame frame) {
		if (frame == null) {
			// Open on the screen where the mouse cursor is
			GraphicsConfiguration gc = MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration();
			frame =  new JFrame(gc);
			frame.setLocationByPlatform(true);
		}
		setTitle(frame);
		setIcon(frame);
		// Splash screen uses the same identifier on purpose. It is the same
		// window.
		WindowUtils.trackLocation(frame, "main", true);
		/*
		 * When the user tries to close the window, don't close immediately,
		 * but let it show a confirmation dialog.
		 */
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		WindowUtils.closeOnEscape(frame);

		return frame;
	}
}
