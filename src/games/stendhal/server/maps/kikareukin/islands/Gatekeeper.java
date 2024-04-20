/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.kikareukin.islands;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.Level;
import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.GenericEvent;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.util.TimeUtil;


public class Gatekeeper {

	/** Slot name used for tracking requests. */
	private static final String SLOT = "heaven_gatekeeper";
	/** Name of entity that messages player. */
	private static final String ENTITY_NAME = "Kikareukin's Gatekeeper";
	/** Number of requests allowed before being punished. */
	private static final short REQUEST_LIMIT = 3;
	/** Time period in which player requests are tracked (4 days). */
	private static final long TIME_BUFFER = TimeUtil.MILLISECONDS_IN_DAY * 4;
	/** XP modifier for trying to requesting too often (15% of excess). */
	private static final float XP_MODIFIER = 0.15f;

	/** Punishment queue to prevent multiple uses of balloon & multiple punishments. */
	private static final List<String> punishmentQueue = new ArrayList<>();

	public static enum RequestState {
		ALLOWED,
		DENIED,
		PUNISH_QUEUED;
	}


	/**
	 * Increments number of requests player has made.
	 *
	 * @param player
	 *   Player requesting entrance.
	 */
	private static void addRequest(final Player player) {
		final long timeFromRequestStart = System.currentTimeMillis() - Gatekeeper.getRequestTime(player);
		if (timeFromRequestStart > Gatekeeper.TIME_BUFFER) {
			// first request or time limit expired so reset so player isn't unnecessarily punished
			player.setQuest(Gatekeeper.SLOT, System.currentTimeMillis() + ";1");
		} else {
			new IncrementQuestAction(Gatekeeper.SLOT, 1, 1).fire(player, null, null);
		}
	}

	/**
	 * Retrieves the time period beginning when player made initial request.
	 *
	 * @param player
	 *   Player requesting entrance.
	 * @return
	 *   Time of initial request.
	 */
	private static long getRequestTime(final Player player) {
		return MathHelper.parseLongDefault(player.getQuest(Gatekeeper.SLOT, 0), 0);
	}

	/**
	 * Retrieves number of request player has made in the current time period.
	 *
	 * @param player
	 *   Player requesting entrance.
	 * @return
	 *   Request count.
	 */
	private static int getRequestCount(final Player player) {
		return MathHelper.parseIntDefault(player.getQuest(Gatekeeper.SLOT, 1), 0);
	}

	/**
	 * Punishment from heaven.
	 *
	 * - sent to afterlife
	 * - loses all but 1 HP
	 * - loses 15% of excess XP
	 * - negative karma
	 *
	 * TODO:
	 *   - add lightning (spell effect)
	 *   - play thunder sound
	 *
	 * @param player
	 *   Player being punished.
	 */
	private static void punish(final Player player) {
		// send to afterlife
		final StendhalRPZone afterlife = SingletonRepository.getRPWorld().getZone("int_afterlife");
		if (afterlife != null) {
			player.teleport(afterlife, 31, 23, player.getDirection(), null);
		}
		// set HP to 1 & subtract XP
		final int xpStart = player.getXP();
		// a percentage of XP based on difference requirement to next level (levels can be lost)
		//final int xpDiff = (int) Math.floor(Level.getXPDiff(player.getLevel()-1) * HeavenGateKeeper.XP_MODIFIER);
		final int xpBuffer = Math.max(xpStart - Level.getXP(player.getLevel()), 0);
		// a percentage of player's gained XP relative to the current level (levels cannot be lost)
		final int xpDiff = (int) Math.floor(xpBuffer * Gatekeeper.XP_MODIFIER);
		final int hpStart = player.getHP();
		player.addXP(-xpDiff);
		player.setHP(1);
		player.addKarma(-50);
		final int xpLoss = xpStart - player.getXP();
		final int hpLoss = hpStart - player.getHP();

		NotificationType ntype = NotificationType.INFORMATION;
		String msg = "It appears you you are not welcome in the clouds at this time.";
		if (xpLoss > 0 || hpLoss > 0) {
			ntype = NotificationType.NEGATIVE;
			msg += " You lost ";
			if (hpLoss > 0) {
				msg += hpLoss + " health";
				if (xpLoss > 0) {
					msg += " and ";
				}
			}
			if (xpLoss > 0) {
				msg += xpLoss + " experience";
			}
			msg += ".";
		}
		player.sendPrivateText(ntype, msg);
		// update queue
		final String name = player.getName();
		if (Gatekeeper.punishmentQueue.contains(name)) {
			Gatekeeper.punishmentQueue.remove(name);
		}
	}

	/**
	 * Checks if player violates visit limit.
	 *
	 * @param player
	 *   Player requesting entrance.
	 * @return
	 *   `true` if player has made more than 3 requests within 4 days.
	 */
	private static boolean inViolation(final Player player) {
		return Gatekeeper.getRequestCount(player) > Gatekeeper.REQUEST_LIMIT;
	}

	/**
	 * Requests entrance into heaven and punishes if necessary.
	 *
	 * @param player
	 *   Player requesting entrance.
	 * @return
	 *   Whether player can enter.
	 */
	public static RequestState requestEntrance(final Player player) {
		final boolean firstRequest = !player.hasQuest(Gatekeeper.SLOT);
		final String name = player.getName();
		if (Gatekeeper.punishmentQueue.contains(name)) {
			return RequestState.PUNISH_QUEUED;
		}
		Gatekeeper.addRequest(player);
		if (Gatekeeper.inViolation(player)) {
			// add name to queue in case player tries to use balloon again
			Gatekeeper.punishmentQueue.add(name);
			// first delay is to notify player, second is to apply punishment
			final TurnNotifier notifier = SingletonRepository.getTurnNotifier();
			final TurnListener listener = new TurnListener() {
				private boolean notified = false;
				@Override
				public void onTurnReached(int currentTurn) {
					if (!notified) {
						player.addEvent(new GenericEvent("kika_punishment"));
						player.sendPrivateText(NotificationType.PRIVMSG, Gatekeeper.ENTITY_NAME,
								"You have worn out your welcome and shall be punished for your greed!");
						notified = true;
						notifier.notifyInTurns(10, this);
					} else {
						Gatekeeper.punish(player);
					}
				}
			};
			notifier.notifyInTurns(10, listener);
			return RequestState.DENIED;
		}

		if (firstRequest) {
			// first time player has been granted entrace so give a warning about potential punishment
			SingletonRepository.getTurnNotifier().notifyInTurns(30, new TurnListener() {
				@Override
				public void onTurnReached(int currentTurn) {
					player.addEvent(new SoundEvent(SoundID.THUNDERCLAP, SoundLayer.FIGHTING_NOISE));
					player.sendPrivateText(NotificationType.WARNING, Gatekeeper.ENTITY_NAME, "Your request to"
							+ " enter the clouds has been granted... this time. But do not get greedy lest you"
							+ " face the wrath of heaven.");
				}
			});
		}
		return RequestState.ALLOWED;
	}
}
