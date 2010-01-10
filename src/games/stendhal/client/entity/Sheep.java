/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.sound.SoundLayer;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Rand;

/** A Sheep entity. */
public class Sheep extends DomesticAnimal {
	//
	// DomesticAnimal
	//

	@Override
	protected void probableChat(final int chance) {

		final String[][] soundnames = { { "sheep-1.wav", "sheep-3.wav" },
				{ "sheep-2.wav", "sheep-4.wav" } };
		final int which = Rand.rand(2);
		if (Rand.rand(100) < chance) {
			final String token;
			if (getWeight() > 50) {
				token = soundnames[0][which];
			} else {
				token = soundnames[1][which];
			}
			SoundMaster.play(SoundLayer.CREATURE_NOISE, token, x, y); 
		}
	}
}
