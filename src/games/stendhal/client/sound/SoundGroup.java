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
package games.stendhal.client.sound;

import games.stendhal.client.sound.manager.AudibleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.system.Time;

public interface SoundGroup {

	public boolean loadSound(String name, String fileURI, Type fileType, boolean enableStreaming);

	public SoundHandle play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone);

	public SoundHandle play(String soundName, float volume, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone);

	/**
	 * gets the current volumne
	 *
	 * @return volumne
	 */
	public float getVolume();

	/**
	 * changes the volumne
	 * 
	 * @param volume volume
	 */
	public void changeVolume(float volume);

	/**
	 * enables streaming for this group.
	 */
	public void enableStreaming();

}
