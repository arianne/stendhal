/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature.impl.attack;

import games.stendhal.server.entity.creature.Creature;

/**
 * A target selection strategy that changes the outfit of the creature depending
 * on its attacking status.
 */
public class OutfitChangerTargeter implements TargetSelectionStrategy {
	final TargetSelectionStrategy base;
	final String peaceful;
	final String attacking;
	String currentOutfit;

	OutfitChangerTargeter(String param) {
		String[] args = param.split(";");
		if (args.length != 3) {
			throw new IllegalArgumentException("Invalid outfit changer description: '"
					+ param + "'");
		}
		base = TargetSelectionStrategyFactory.get(args[0], "");
		peaceful = args[1];
		attacking = args[2];
		currentOutfit = peaceful;
	}

	@Override
	public void findNewTarget(Creature creature) {
		base.findNewTarget(creature);
	}

	@Override
	public boolean hasValidTarget(Creature creature) {
		boolean rval = base.hasValidTarget(creature);
		String outfit;
		if (rval) {
			outfit = attacking;
		} else {
			outfit = peaceful;
		}
		// Change the creature representation if the attacking status has
		// changed
		if (currentOutfit != outfit) {
			creature.put("subclass", outfit);
			currentOutfit = outfit;
		}

		return rval;
	}
}
