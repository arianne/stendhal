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
package games.stendhal.client.events;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.RPEntity;
import games.stendhal.common.constants.Nature;

/**
 * Client side attack event
 */
public class AttackEvent extends Event<RPEntity> {
	private static final Logger logger = Logger.getLogger(AttackEvent.class);

	@Override
	public void execute() {
		Nature dtype;
		int idx = event.getInt("type");
		try {
			dtype = Nature.values()[idx];
		} catch (ArrayIndexOutOfBoundsException exc) {
			logger.warn("Unknown damage type: " + idx);
			dtype = Nature.CUT;
		}

		RPEntity target = entity.getAttackTarget();
		if (target != null) {
			boolean ranged = event.has("ranged");
			if (event.has("hit")) {
				int damage = event.getInt("damage");
				if (damage != 0) {
					target.onDamaged(entity, damage);
				} else {
					target.onBlocked();
				}
			} else {
				target.onMissed();
			}
			entity.onAttackPerformed(dtype, ranged, event.get("weapon"));
		}
	}
}
