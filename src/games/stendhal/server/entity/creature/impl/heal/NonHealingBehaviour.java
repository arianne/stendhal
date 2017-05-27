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
package games.stendhal.server.entity.creature.impl.heal;

import games.stendhal.server.entity.creature.Creature;

final class NonHealingBehaviour implements HealerBehavior {


	@Override
	public void heal(final Creature creature) {
		// does not heal;
	}

	@Override
	public void init(final String healingProfile) {
		// does not need init

	}

}
