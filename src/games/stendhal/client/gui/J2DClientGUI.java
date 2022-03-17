/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e.V.                  *
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

import java.awt.Component;
import java.util.Collection;

import javax.swing.JFrame;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.User;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPObject;


public interface J2DClientGUI {
	void addDialog(Component dialog);
	void addAchievementBox(String title, String description, String category);
	@Deprecated
	void addGameScreenText(double x, double y, String text, NotificationType type,
		boolean isTalking);
	@Deprecated
	void addGameScreenText(final Entity entity, final String text,
		final NotificationType type, final boolean isTalking);
	void afterPainting();
	void beforePainting();
	void chooseOutfit();
	/**
	 * Get the objects that should be informed about user position changes.
	 *
	 * @return
	 */
	Collection<PositionChangeListener> getPositionChangeListeners();
	JFrame getFrame();
	boolean isOffline();
	/**
	 * Resets the clients width and height to their default values.
	 */
	void resetClientDimensions();
	void requestQuit(StendhalClient client);
	void setChatLine(String text);
	void setOffline(boolean offline);
	void switchToSpellState(RPObject spell);
	/**
	 * Requests repaint at the window areas that are painted according to the
	 * game loop frame rate.
	 */
	void triggerPainting();
	void updateUser(User user);
}
