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

import games.stendhal.common.Rand;
import marauroa.common.game.RPObject;

/**
 * An NPC entity.
 */
public class NPC extends AudibleEntity {
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
			if (name.equals("Diogenes")) {
				addSoundsToGroup("move", "laugh-1", "laugh-2");
			} else if (name.equals("Carmen")) {
				addSoundsToGroup("move", "giggle-1", "giggle-2");
			} else if (name.equals("Nishiya")) {
				addSoundsToGroup("move", "cough-11", "cough-2", "cough-3");
			} else if (name.equals("Margaret")) {
				addSoundsToGroup("move", "hiccup-1", "hiccup-2", "hiccup-3");
			} else if (name.equals("Sato")) {
				addSoundsToGroup("move", "hiccup-1", "sneeze-1");
			}
		}
	}

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
		playRandomSoundFromGroup("move", 1.0f, 2000);
	}
}
