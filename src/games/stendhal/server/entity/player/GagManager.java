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
package games.stendhal.server.entity.player;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.util.TimeUtil;

/**
 * Manages gags.
 */
public class GagManager implements LoginListener {

	private static final Logger logger = Logger.getLogger(GagManager.class);

	/** The Singleton instance. */
	private static GagManager instance;


	/**
	 * returns the GagManager object (Singleton Pattern).
	 *
	 * @return GagManager
	 */
	public static GagManager get() {
		if (instance == null) {
			instance = new GagManager();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private GagManager() {
		SingletonRepository.getLoginNotifier().addListener(this);
	}

	/**
	 * @param criminalName
	 *            The name of the player who should be gagged
	 * @param policeman
	 *            The name of the admin who wants to gag the criminal
	 * @param minutes
	 *            The duration of the sentence
	 * @param reason why criminal was gagged
	 */
	public void gag(final String criminalName, final Player policeman, final int minutes,
			final String reason) {
		final Player criminal = SingletonRepository.getRuleProcessor().getPlayer(
				criminalName);

		if (criminal == null) {
			final String text = "Player " + criminalName + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}

		gag(criminal, policeman, minutes, reason, criminalName);
	}

	void gag(final Player criminal, final Player policeman, final int minutes,
			final String reason, final String criminalName) {
		// no -1
		if (minutes < 0) {
			policeman.sendPrivateText("Infinity (negative numbers) is not supported.");
			return;
		}

		// Set the gag
		final long expireDate = System.currentTimeMillis() + (MathHelper.MILLISECONDS_IN_ONE_MINUTE * minutes);
		criminal.setQuest("gag", "" + expireDate);

		// Send messages
		policeman.sendPrivateText("You have gagged " + criminalName + " for "
				+ minutes + " minutes. Reason: " + reason + ".");
		criminal.sendPrivateText(NotificationType.SUPPORT,
				"You have been gagged for " + minutes
				+ " minutes. Reason: " + reason + ".");
		SingletonRepository.getRuleProcessor().sendMessageToSupporters("GagManager", policeman.getName()
				+ " gagged " + criminalName + " for " + minutes
				+ " minutes. Reason: " + reason + ".");

		setupNotifier(criminal);
	}

	/**
	 * Removes a gag.
	 *
	 * @param inmate
	 *            player who should be released
	 */
	public void release(final Player inmate) {
		if (isGagged(inmate)) {
			inmate.removeQuest("gag");
			inmate.sendPrivateText(NotificationType.SUPPORT, "Your gag sentence is over.");
			logger.debug("Player " + inmate.getName() + "released from gag.");
		}
	}

	/**
	 * Is player gagged?
	 *
	 * @param player player to check
	 * @return true, if it is gagged, false otherwise.
	 */
	public static boolean isGagged(final Player player) {
		if (player.hasQuest("gag")) {
			return true;
		}
		return false;
	}

	/**
	 * Like isGagged(player) but informs the player in case it is gagged.
	 *
	 * @param player player to check
	 * @return true, if it is gagged, false otherwise.
	 */
	public static boolean checkIsGaggedAndInformPlayer(final Player player) {
		final boolean res = GagManager.isGagged(player);
		if (res) {
			final long timeRemaining = SingletonRepository.getGagManager().getTimeRemaining(player);
			player.sendPrivateText("You are gagged, it will expire in "
					+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)));
		}

		return res;
	}

	/**
	 * If the players' gag has expired, remove it.
	 *
	 * @param player
	 *            player to check
	 * @return true, if the gag expired and was removed or was already removed.
	 *         false, if the player still has time to serve.
	 */
	private boolean tryExpire(final Player player) {
		if (!isGagged(player)) {
			return true;
		}

		// allow for an error of 10 seconds
		if (getTimeRemaining(player) < (10L * 1000L)) {
			release(player);
			return true;
		}

		return false;
	}

	@Override
	public void onLoggedIn(final Player player) {
		if (!isGagged(player)) {
			return;
		}

		if (!tryExpire(player)) {
			setupNotifier(player);
		}
	}

	private void setupNotifier(final Player criminal) {

		final String criminalName = criminal.getName();

		// Set a timer so that the inmate is automatically released after
		// serving his sentence. We're using the TurnNotifier; we use
		SingletonRepository.getTurnNotifier().notifyInSeconds(
				(int) (getTimeRemaining(criminal) / 1000), new TurnListener() {
					@Override
					public void onTurnReached(final int currentTurn) {

						final Player criminal2 = SingletonRepository.getRuleProcessor().getPlayer(
								criminalName);
						if (criminal2 == null) {
							logger.debug("Gagged player " + criminalName
									+ "has logged out.");
							return;
						}

						tryExpire(criminal2);

					}
				});
	}

	/**
	 * Gets time remaining in milliseconds.
	 *
	 * @param criminal
	 *            player to check
	 * @return time remaining in milliseconds
	 */
	public long getTimeRemaining(final Player criminal) {
		if (!isGagged(criminal)) {
			return 0L;
		}
		final long expireDate = Long.parseLong(criminal.getQuest("gag"));
		final long timeRemaining = expireDate - System.currentTimeMillis();
		return timeRemaining;
	}
}
