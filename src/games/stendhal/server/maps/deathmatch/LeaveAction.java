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
package games.stendhal.server.maps.deathmatch;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * handle the players request to leave the deathmatch
 * (if it is allowed in the current state).
 */
public class LeaveAction implements ChatAction {

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));

		if (deathmatchState.getLifecycleState() == DeathmatchLifecycle.DONE) {
			final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_plains_n");
			player.teleport(zone, 100, 115, null, player);
		} else if (deathmatchState.getLifecycleState() == DeathmatchLifecycle.VICTORY) {
			raiser.say("I don't think you claimed your #victory yet.");
		} else {
			raiser.say("What are you? A coward?");
		}
	}
}
