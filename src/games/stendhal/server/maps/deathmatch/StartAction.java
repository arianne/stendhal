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

import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.NotificationType;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.player.Player;

/**
 * Action to start a new deathmatch session for the player.
 *
 * @author hendrik
 */
public class StartAction implements ChatAction {

	private final DeathmatchInfo deathmatchInfo;

	// elapsed time in minutes required for repeated announcement
	private static final int ANNOUNCE_COOLDOWN_MINS = 15;
	private static final Map<String, Long> announceTimes = new HashMap<String, Long>();


	/**
	 * Creates a new StartAction for the specified deathmatch.
	 *
	 * @param deathmatchInfo deathmatch to start
	 */
	public StartAction(final DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		raiser.say("Have fun!");
		// Track starts. The three first numbers are reserved for level,
		// time stamp and points (first is the state)
		new IncrementQuestAction("deathmatch", 5, 1).fire(player, sentence, raiser);
		deathmatchInfo.startSession(player, raiser);


		final long dmStart = System.currentTimeMillis();
		final String pName = player.getName();

		// don't make announcement if previous was within 15 minutes
		if (!announceTimes.containsKey(pName) ||
				(dmStart - announceTimes.get(pName)) / 60000 >= ANNOUNCE_COOLDOWN_MINS) {
			final String msg = raiser.getName() + " shouts: A deathmatch has begun! Will "
				+ pName + " survive? Come and satisfy your thirst for violence.";

			final StendhalRPRuleProcessor rp = SingletonRepository.getRuleProcessor();

			// tell all players in game
			rp.tellAllPlayers(NotificationType.PRIVMSG, msg);

			// notify IRC via postman
			final Player postman = rp.getPlayer("postman");
			if (postman != null) {
				postman.sendPrivateText(msg);
			}

			// store announcement time
			// TODO: Clean up expired announcementTimes entries
			announceTimes.put(pName, dmStart);
		}
	}
}
