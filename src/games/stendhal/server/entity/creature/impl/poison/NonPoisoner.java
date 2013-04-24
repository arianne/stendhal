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
package games.stendhal.server.entity.creature.impl.poison;

import games.stendhal.server.entity.RPEntity;

class NonPoisoner implements Attacker {

	public NonPoisoner() {
		super();
	}

	@Override
	public boolean attack(final RPEntity victim) {
		return false;
	}

	@Override
	public void applyAntipoison(double antipoison) {
	}
	
	@Override
	public int getProbability() {
		// Non-poisoner has 0% chance of poisoning
		return 0;
	}
	
	@Override
	public void setProbability(int p) {
	}
}
