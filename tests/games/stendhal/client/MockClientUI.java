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
package games.stendhal.client;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JFrame;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.J2DClientGUI;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.common.ErrorBuffer;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPObject;

/**
 * Mock client as replacement for j2DClient.
 *
 * @author Martin Fuchs
 */
public class MockClientUI extends j2DClient {
	private final ErrorBuffer buffer = new ErrorBuffer();

	public MockClientUI() {
		super(new J2DClientGUI() {
			@Override
			public void addDialog(Component dialog) {}

			@Override
			public void addAchievementBox(String title, String description,
					String category) {}

			@Override
			public void addGameScreenText(double x, double y, String text,
					NotificationType type, boolean isTalking) {}

			@Override
			public void addGameScreenText(final Entity entity, final String text,
					final NotificationType type, final boolean isTalking) {}

			@Override
			public void afterPainting() {}

			@Override
			public void beforePainting() {}

			@Override
			public void chooseOutfit() {}

			@Override
			public Collection<PositionChangeListener> getPositionChangeListeners() {
				return Collections.emptyList();
			}

			@Override
			public JFrame getFrame() {
				return null;
			}

			@Override
			public boolean isOffline() {
				return false;
			}

			@Override
			public void resetClientDimensions() {}

			@Override
			public void requestQuit(StendhalClient client) {}

			@Override
			public void setChatLine(String text) {}

			@Override
			public void setOffline(boolean offline) {}

			@Override
			public void switchToSpellState(RPObject spell) {}

			@Override
			public void triggerPainting() {}

			@Override
			public void updateUser(User user) {}
		});
	}

	@Override
	public void addEventLine(EventLine line) {
		buffer.setError(line.getText());
	}

	public String getEventBuffer() {
		if (buffer.hasError()) {
			return buffer.getErrorString();
		} else {
			return "";
		}
	}
}
