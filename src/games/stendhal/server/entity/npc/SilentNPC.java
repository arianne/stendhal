/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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
 * A stripped down SpeakerNPC that does not interact with players
 */
public class SilentNPC extends PassiveNPC {

	/**
	 * Creates a new SilentNPC.
	 */
	public SilentNPC() {
		super();

		// Entity name is not drawn because of "unnamed" attribute
        setName("SilentNPC");
		put("unnamed", "");

		// Health bar drawing is supressed
		put("no_hpbar", "");

		// Remove "attack" option from menus
		put("no_attack", "");

		updateModifiedAttributes();
	}

	@Override
	public void logic() {
		if (this.getZone().getPlayerAndFriends().isEmpty()) {
			// don't do anything if no players in area
			return;
		}

		super.logic();
	}
}
