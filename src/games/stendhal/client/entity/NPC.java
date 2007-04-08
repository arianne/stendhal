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

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.sound.SoundSystem;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class NPC extends RPEntity {

	private static final Logger logger = Log4J.getLogger(NPC.class);


	public void init(final RPObject object){
		super.init(object);
		String type = getType();

		String name = null;
		if (object.has("name")) {
			name = object.get("name");
		} else {
			name = object.get("type");
		}

		if (type.startsWith("npc")) {
			setAudibleRange(3);
			if (name.equals("Diogenes")) {
				SoundSystem.startSoundCycle(this, "Diogenes-patrol", 10000, 20, 50, 100);
			} else if (name.equals("Carmen")) {
				SoundSystem.startSoundCycle(this, "Carmen-patrol", 60000, 20, 50, 75);
			} else if (name.equals("Nishiya")) {
				SoundSystem.startSoundCycle(this, "Nishiya-patrol", 40000, 20, 50, 80);
			} else if (name.equals("Margaret")) {
				SoundSystem.startSoundCycle(this, "Margaret-patrol", 30000, 10, 30, 70);
			} else if (name.equals("Sato")) {
				SoundSystem.startSoundCycle(this, "Sato-patrol", 60000, 30, 50, 70);
			}
		}
	}


	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);


	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y + 1, 1, 1);
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new NPC2DView(this);
	}
}
