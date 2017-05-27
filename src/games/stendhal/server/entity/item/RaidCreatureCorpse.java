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
package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;

/**
 * A faster rotting corpse for raid use with time limited access
 * to the contents only by the player having been last attacked by it.
 */
public class RaidCreatureCorpse extends Corpse {
	// completely rot in 3 minutes
	private static final int DEGRADATION_STEP_TIMEOUT = 3 * 60 / 5;

	/**
	 * Create a corpse.
	 *
	 * @param victim
	 *            The killed entity.
	 * @param killerName
	 *            The killer name.
	 */
	public RaidCreatureCorpse(final RPEntity victim, final String killerName) {
		super(victim, killerName);
	}


	@Override
	protected int getDegradationStepTimeout() {
		return DEGRADATION_STEP_TIMEOUT;
	}
}
