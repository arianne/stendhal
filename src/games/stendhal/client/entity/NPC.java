/* $Id$ */
/***************************************************************************
 *						(C) Copyright 2003 - Marauroa					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.sound.SoundLayer;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Rand;
import marauroa.common.game.RPObject;

/**
 * An NPC entity.
 */
public class NPC extends RPEntity {
	//
	// Entity
	//

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *			  The object.
	 * 
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		final String type = getType();

		if (type.startsWith("npc")) {
			setAudibleRange(3);
			if (name.equals("Diogenes")) {
				moveSounds = new String[2];
				moveSounds[0] = "laugh-1.wav";
				moveSounds[1] = "laugh-2.wav";
				// SoundSystem.startSoundCycle(this, "Diogenes-patrol", 10000,
				// 20, 50, 100);
			} else if (name.equals("Carmen")) {
				moveSounds = new String[2];
				moveSounds[0] = "giggle-1.wav";
				moveSounds[1] = "giggle-2.wav";

				// SoundSystem.startSoundCycle(this, "Carmen-patrol", 60000, 20,
				// 50, 75);
			} else if (name.equals("Nishiya")) {
				moveSounds = new String[3];
				moveSounds[0] = "cough-11.wav";
				moveSounds[1] = "cough-2.wav";
				moveSounds[2] = "cough-3.wav";
				// SoundSystem.startSoundCycle(this, "Nishiya-patrol", 40000,
				// 20, 50, 80);
			} else if (name.equals("Margaret")) {
				moveSounds = new String[3];
				moveSounds[0] = "hiccup-1.aiff";
				moveSounds[1] = "hiccup-2.wav";
				moveSounds[2] = "hiccup-3.wav";

				// SoundSystem.startSoundCycle(this, "Margaret-patrol", 30000,
				// 10, 30, 70);
			} else if (name.equals("Sato")) {
				moveSounds = new String[1];
				moveSounds[0] = "sneeze-1.wav";
				// SoundSystem.startSoundCycle(this, "Sato-patrol", 60000, 30,
				// 50, 70);
			}
		}
	}

	private long soundWait;

	/**
	 * When the entity's position changed.
	 * 
	 * @param x
	 *			  The new X coordinate.
	 * @param y
	 *			  The new Y coordinate.
	 */
	@Override
	protected void onPosition(final double x, final double y) {
		super.onPosition(x, y);

		if ((soundWait < System.currentTimeMillis()) && (Rand.rand(1000) < 5)) {

			if (moveSounds != null) {
				SoundMaster.play(SoundLayer.CREATURE_NOISE, moveSounds[Rand.rand(moveSounds.length)], x, y);
			}
			soundWait = System.currentTimeMillis() + 2000L;
		}
	}
}
