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
package games.stendhal.client.sound.nosound;

import games.stendhal.client.sound.SoundGroup;
import games.stendhal.client.sound.SoundHandle;
import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.system.Time;

public class NoSoundGroup implements SoundGroup {

	public void changeVolume(float intToFloat) {
		// do nothing
	}

	public float getVolume() {
		// do nothing
		return 0;
	}

	public boolean loadSound(String name, String fileURI, Type fileType, boolean enableStreaming) {
		// do nothing
		return false;
	}

	public SoundHandle play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
		// do nothing
		return new NoSoundHandle();
	}

	public SoundHandle play(String soundName, float volume, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
		// do nothing
		return new NoSoundHandle();
	}

}
