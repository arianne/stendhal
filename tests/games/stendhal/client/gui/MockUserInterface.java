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

import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.sound.facade.AudibleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundHandle;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.common.NotificationType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An user interface that does nothing, but provides the things client side
 * code may expect.
 */
public class MockUserInterface implements UserInterface {
	private final SoundSystemFacade sound = new DummySoundSystem();
	/** Stored last message */
	private EventLine previousEventLine;

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

	public void addGameScreenText(double x, double y, String text,
			NotificationType type, boolean isTalking) {
		// do nothing
	}

	public SoundSystemFacade getSoundSystemFacade() {
		return sound;
	}

	/**
	 * A sound system that does nothing.
	 */
	private static class DummySoundSystem implements SoundSystemFacade {
		private final SoundGroup group = new DummySoundGroup();

		public void changeVolume(float volume) {
			// do nothing
		}

		public void exit() {
			// do nothing
		}

		public SoundGroup getGroup(String groupName) {
			// Return the same dummy group for everything
			return group;
		}

		public Collection<String> getGroupNames() {
			return null;
		}

		public float getVolume() {
			return 0;
		}

		public void mute(boolean turnOffSound, boolean useFading, Time delay) {
			// do nothing
		}

		public void playerMoved() {
			// do nothing
		}

		public void stop(SoundHandle sound, Time fadingDuration) {
			// do nothing
		}

		public void update() {
			// do nothing
		}

		public void zoneEntered(String zoneName) {
			// do nothing
		}

		public void zoneLeft(String zoneName) {
			// do nothing
		}

		public List<String> getDeviceNames() {
			return new LinkedList<String>();
		}
	}

	/**
	 * A SoundGroup that does nothing, but can be passed where a valid
	 * SoundGroup is expected.
	 */
	private static class DummySoundGroup implements SoundGroup {
		public void changeVolume(float intToFloat) {
			// do nothing
		}

		public float getVolume() {
			return 0;
		}

		public boolean loadSound(String name, String fileURI, SoundFileType fileType,
				boolean enableStreaming) {
			// do nothing
			return false;
		}

		public SoundHandle play(String soundName, int layerLevel,
				AudibleArea area, Time fadeInDuration, boolean autoRepeat,
				boolean clone) {
			// do nothing
			return null;
		}

		public SoundHandle play(String soundName, float volume, int layerLevel,
				AudibleArea area, Time fadeInDuration, boolean autoRepeat,
				boolean clone) {
			// do nothing
			return null;
		}

		public void enableStreaming() {
			// do nothing
		}
	}

	public void addAchievementBox(String title, String description,
			String category) {
		// do nothing
	}
}
