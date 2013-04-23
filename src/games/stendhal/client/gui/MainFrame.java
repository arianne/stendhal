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

import games.stendhal.client.stendhal;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.common.Debug;

import java.awt.GraphicsConfiguration;
import java.awt.MouseInfo;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

class MainFrame {
	private final JFrame mainFrame;

	/**
	 * Create a new MainFrame.
	 */
	public MainFrame() {
		// Open on the screen where the mouse cursor is
		GraphicsConfiguration gc = MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration();
		mainFrame =  new JFrame(gc);
		initialize();
	}

	private void initialize() {
		setTitle();
		setIcon();
		setDefaultCloseBehaviour();
		WindowUtils.closeOnEscape(mainFrame);
	}

	private void setDefaultCloseBehaviour() {
		// When the user tries to close the window, don't close immediately,
		// but show a confirmation dialog.
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	private void setIcon() {
		final URL url = DataLoader.getResource(
				ClientGameConfiguration.get("GAME_ICON"));
		getMainFrame().setIconImage(new ImageIcon(url).getImage());
	}

	private void setTitle() {
		String preRelease = "";
		if (Debug.PRE_RELEASE_VERSION != null) {
			preRelease = " - " + Debug.PRE_RELEASE_VERSION;
		}
		mainFrame.setTitle(ClientGameConfiguration.get("GAME_NAME") + " "
				+ stendhal.VERSION + preRelease
				+ " - a multiplayer online game using Arianne");
	}

	 JFrame getMainFrame() {
		return mainFrame;
	}

}
