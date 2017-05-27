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
package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.common.constants.SoundLayer;

/**
 * Plays an ambient sound in a loop.
 *
 * @author hendrik
 */
public class BackgroundMusicSource extends LoopedSoundSource  {

	/**
	 * Create an ambient sound area.
	 *
	 * @param sound sound name
	 * @param radius
	 * @param volume
	 */
	public BackgroundMusicSource(String sound, int radius, int volume) {
		super(sound, radius, volume, SoundLayer.BACKGROUND_MUSIC);
	}

}
