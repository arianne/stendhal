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

import games.stendhal.client.sound.facade.AudibleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundHandle;
import games.stendhal.client.sound.facade.Time;

public class NoSoundGroup implements SoundGroup {

	@Override
	public void changeVolume(float intToFloat) {
		// do nothing
	}

	@Override
	public float getVolume() {
		// do nothing
		return 0;
	}

	@Override
	public boolean loadSound(String name, String fileURI, SoundFileType fileType, boolean enableStreaming) {
		// do nothing
		return false;
	}

	@Override
	public SoundHandle play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
		// do nothing
		return new NoSoundHandle();
	}

	@Override
	public SoundHandle play(String soundName, float volume, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
		// do nothing
		return new NoSoundHandle();
	}

	@Override
	public void enableStreaming() {
		// do nothing
	}

}
