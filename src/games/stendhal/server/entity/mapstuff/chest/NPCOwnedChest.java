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
package games.stendhal.server.entity.mapstuff.chest;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * A chest that is for decoration purpose only. The player cannot open it. If he
 * tries, the nearby NPC will tell him to get away.
 *
 * @author hendrik
 */
public class NPCOwnedChest extends Chest {

	private static Logger logger = Logger.getLogger(NPCOwnedChest.class);

	private final SpeakerNPC npc;

	/**
	 * Creates a new NPCOwnedChest.
	 *
	 * @param npc
	 *            SpeakerNPC
	 */
	public NPCOwnedChest(final SpeakerNPC npc) {
		this.npc = npc;
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user instanceof Player) {
			final Player player = (Player) user;

			if (player.nextTo(this)) {
				npc.say("Hey " + user.getTitle() + ", that is my chest.");
				return true;
			} else {
				return false;
			}
		} else {
			logger.error("user is no instance of Player but: " + user, new Throwable());
			return false;
		}
	}
}
