/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

/**
 * Simple entity used in spectacles. Can walk around and say monologues, but cannot speak with players.
 *
 * @author yoriy
 *
 */
public class ActorNPC extends NPC {

	private final boolean attackable;

	/**
	 * Create a new ActorNPC.
	 *
	 * @param attackable <code>true</code> if the entity can be attacked,
	 * 	otherwise <code>false</code>
	 */
	public ActorNPC(boolean attackable) {
		this.attackable=attackable;
	}

	/**
	 *
	 */
	@Override
	public boolean isAttackable() {
		return attackable;
	}

}
