/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/

package games.stendhal.server.entity.player;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import marauroa.common.Configuration;

/**
 * timesout a player who is AFK for a long time
 *
 * @author hendrik
 */
public class AfkTimeouter implements TurnListener {
	private static Logger logger = Logger.getLogger(AfkTimeouter.class);
	private static int afkCheckInterval;
	private static int afkDisconnect;

	/**
	 * creats a new AfkTimeouter
	 */
	public static void create() {
		try {
			afkCheckInterval = Configuration.getConfiguration().getInt("afk_check_interval", 2 * 60 * 60);
			afkDisconnect = Configuration.getConfiguration().getInt("afk_disconnect", 3 * 60 * 60);
			TurnNotifier.get().notifyInSeconds(afkCheckInterval, new AfkTimeouter());
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

	@Override
	public void onTurnReached(int currentTurn) {
		Collection<Player> players = StendhalRPRuleProcessor.get().getOnlinePlayers().getAllPlayers();
		Set<Player> toDisconnect = new HashSet<Player>();
		for (Player player : players) {
			if (System.currentTimeMillis() - player.getLastClientActionTimestamp() > afkDisconnect * 1000) {
				if (player.getAdminLevel() < 1000) {
					toDisconnect.add(player);
				}
			}
		}

		for (Player player : toDisconnect) {
			new GameEvent(player.getName(), "afkdisconnect", Long.toString(System.currentTimeMillis() - player.getLastClientActionTimestamp())).raise();
			SingletonRepository.getRuleProcessor().getRPManager().disconnectPlayer(player);
		}

		TurnNotifier.get().notifyInSeconds(afkCheckInterval, this);
	}

	@Override
	public String toString() {
		return "AfkTimeouter";
	}
}
