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
package games.stendhal.server.core.events;

import static games.stendhal.common.NotificationType.getServerNotificationType;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

/**
 * Delays the sending of text until the next turn (to work
 * around problems like zone changes).
 */
public class DelayedPlayerTextSender implements TurnListener {
	protected final Player player;
	protected final String message;
	protected final NotificationType type;

	/**
	 * Creates a new private message type DelayedPlayerTextSender.
	 *
	 * @param player
	 *            Player to send this message to
	 * @param message
	 *            message
	 * @param seconds
	 */
	public DelayedPlayerTextSender(final Player player, final String message, final int seconds) {
		this(player, message, getServerNotificationType(player.getClientVersion()), seconds);
	}

	/**
	 * Creates a new DelayedPlayerTextSender.
	 *
	 * @param player
	 *            Player to send this message to
	 * @param message
	 *            message
	 * @param type
	 *            logical notificationType
	 * @param seconds delay in seconds
	 */
	public DelayedPlayerTextSender(final Player player, final String message, final NotificationType type, final int seconds) {
		this.player = player;
		this.message = message;
		this.type = type;
		SingletonRepository.getTurnNotifier().notifyInSeconds(seconds, this);
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		player.sendPrivateText(type, message);
	}
}
