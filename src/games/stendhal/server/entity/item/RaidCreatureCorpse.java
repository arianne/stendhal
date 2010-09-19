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
import games.stendhal.server.entity.player.Player;

/**
 * A faster rotting corpse for raid use with time limited access 
 * to the contents only by the player having been last attacked by it.
 */
public class RaidCreatureCorpse extends Corpse {
	// completely rot in 3 minutes
	private static final int DEGRADATION_STEP_TIMEOUT = 3 * 60 / 5;
	private String corpseOwner = null;  
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
		
		if (victim.isAttacking()) {
			corpseOwner = victim.getAttackTarget().getName();
		}
	}
	
	/**
	 * Check if a player may access the slots of this corpse
	 * 
	 * @param player the player trying to use the items in the corpse
	 * @return true iff the player may access the items in the slots 
	 */
	public boolean mayUse(Player player) {
		return (corpseOwner == null || corpseOwner.equals(player.getName()));
	}
	
	/**
	 * Get the name of the owner of this corpse (the player who's 
	 * deemend worthy to access the items within).
	 *  
	 * @return the name of the owner or <code>null</code> if anyone
	 * may use the items
	 */
	public String getCorpseOwner() {
		return corpseOwner;
	}
	
	@Override
	protected int getDegradationStepTimeout() {
		return DEGRADATION_STEP_TIMEOUT;
	}
	
	@Override
	public void onTurnReached(final int currentTurn) {
		// clear the owner so that all players can access the items
		corpseOwner = null;
		super.onTurnReached(currentTurn);
	}
}
