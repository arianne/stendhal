/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.scroll;

import java.util.Map;

import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.events.DelayedPlayerTextSender;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.maps.kikareukin.islands.Gatekeeper;
import games.stendhal.server.maps.kikareukin.islands.Gatekeeper.RequestState;
import games.stendhal.server.util.TimeUtil;

/**
 * Represents the balloon that takes the player to 7 kikareukin clouds,
 * after which it will teleport player to a random location in 6 kikareukin islands.
 */
public class BalloonScroll extends TimedTeleportScroll {

	private static final long DELAY = 6 * TimeUtil.MILLISECONDS_IN_HOUR;
	private static final int NEWTIME = 540;

	/**
	 * Creates a new timed marked BalloonScroll scroll.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public BalloonScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public BalloonScroll(final BalloonScroll item) {
		super(item);
	}

	@Override
	protected String getBeforeReturnMessage() {
		return "It feels like the clouds won't take your weight much longer ... ";
	}

	@Override
	protected String getAfterReturnMessage() {
		return "You fell through a hole in the clouds, back to solid ground.";
	}

	// Only let player use balloon from 6 kika clouds
	// Balloons used more frequently than every 6 hours only last 5 minutes
	@Override
	protected boolean useTeleportScroll(final Player player) {
		if (!"6_kikareukin_islands".equals(player.getZone().getName())) {
			if ("7_kikareukin_clouds".equals(player.getZone().getName())) {
				player.sendPrivateText("Another balloon does not seem to lift you any higher.");
			} else {
				player.sendPrivateText("The balloon tried to float you away but the altitude was too low for it to even lift you. "
						+ "Try from somewhere higher up.");
			}
			return false;
		}

		long lastuse = -1;
		if (player.hasQuest("balloon")) {
			lastuse = Long.parseLong(player.getQuest("balloon"));
		}
		final boolean inCooldown = (lastuse + DELAY) - System.currentTimeMillis() > 0;

		final RequestState requestState = Gatekeeper.requestEntrance(player, inCooldown);
		if (RequestState.DENIED.equals(requestState)) {
			onPopped(player);
			return false;
		} else if (RequestState.RESPONSE_QUEUED.equals(requestState)) {
			// player tried to use balloon again immediately after being denied
			return false;
		}

		// player is allowed entrance so update time that balloon was used
		player.setQuest("balloon", Long.toString(System.currentTimeMillis()));

		if (inCooldown) {
			// player used the balloon within the last DELAY hours
			// so this use of balloon is going to be shortened
			// (the clouds can't take so much weight on them)
			// delay message for 1 turn for technical reasons
			new DelayedPlayerTextSender(player, "The clouds are weakened from your recent time on them, and will not hold you for long.", 1);

			return super.useTeleportScroll(player, "7_kikareukin_clouds", 31, 21, NEWTIME);
		}

		return super.useTeleportScroll(player);
	}

	/**
	 * Events when a balloon "pops".
	 *
	 * @param player
	 *   Player using balloon.
	 */
	private void onPopped(final Player player) {
		player.addEvent(new SoundEvent("balloon/pop", SoundLayer.FIGHTING_NOISE));
		// balloon is used
		removeOne();
		player.sendPrivateText(NotificationType.NEGATIVE, "Your balloon popped.");
	}
}
