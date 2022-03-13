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

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.common.NotificationType;

/**
 * An user interface that does nothing, but provides the things client side
 * code may expect.
 */
public class MockUserInterface implements UserInterface {
	private final SoundSystemFacade sound = new NoSoundFacade();
	/** Stored last message */
	private EventLine previousEventLine;

	@Override
	public void addEventLine(EventLine line) {
		previousEventLine = line;
	}

	/**
	 * Get the previous message. Wipes it from memory.
	 *
	 * @return last message
	 */
	public String getLastEventLine() {
		EventLine tmp = previousEventLine;
		previousEventLine = null;
		if (tmp != null) {
			return tmp.getText();
		}
		return null;
	}

	@Override
	public void addGameScreenText(double x, double y, String text,
			NotificationType type, boolean isTalking) {
		// do nothing
	}

	@Override
	public void addGameScreenText(final Entity entity, final String text,
		final NotificationType type, final boolean isTalking) {}

	@Override
	public SoundSystemFacade getSoundSystemFacade() {
		return sound;
	}

	@Override
	public void addAchievementBox(String title, String description,
			String category) {
		// do nothing
	}
}
