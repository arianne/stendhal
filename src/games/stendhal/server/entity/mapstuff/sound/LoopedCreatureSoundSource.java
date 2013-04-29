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
 * Plays a creature sound in a loop.
 *
 * @author hendrik
 */
public class LoopedCreatureSoundSource extends LoopedSoundSource {
	/**
	 * Create a creature sound area.
	 * 
	 * @param sound 
	 * @param radius 
	 * @param volume 
	 */
	public LoopedCreatureSoundSource(String sound, int radius, int volume) {
		super(sound, radius, volume, SoundLayer.CREATURE_NOISE);
	}
}
