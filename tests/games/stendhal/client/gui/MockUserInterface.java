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

import java.util.Collection;

import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.sound.SoundGroup;
import games.stendhal.client.sound.SoundHandle;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.NotificationType;

/**
 * An user interface that does nothing, but provides the things client side
 * code may expect.
 */
public class MockUserInterface implements UserInterface {
	private final SoundSystemFacade sound = new DummySoundSystem();
	
	public void addEventLine(EventLine line) {
		// do nothing
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

		public boolean loadSound(String name, String fileURI, Type fileType,
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
	}
}
