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
package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.DelayedPlayerTextSender;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.player.Player;

/**
 * Area that sends a private message to any player entering and/or leaving it.
 */
public class MessagingArea extends AreaEntity implements MovementListener {
	/** true if the area should cover the whole zone. */
	private final boolean coversZone;
	/** message sent to the players entering the area. */
	private final String enterMessage;
	/** message sent to the players leaving the area. */
	private final String leaveMessage;
	private final boolean isWarning;

	/**
	 * Create a MessagingArea.
	 *
	 * @param coversZone true if the area should cover the whole zone
	 * @param width width of the area
	 * @param height height of the area
	 * @param enterMessage message to be sent to players entering the area
	 * @param leaveMessage message to be sent to players leaving the area
	 * @param isWarning set to <code>true</code> if the message is a warning
	 * 	that is especially important for the player to see
	 */
	public MessagingArea(final boolean coversZone, final int width, final int height, final String enterMessage,
			final String leaveMessage, final boolean isWarning) {
		super(width, height);
		hide();

		this.coversZone = coversZone;
		this.enterMessage = enterMessage;
		this.leaveMessage = leaveMessage;
		this.isWarning = isWarning;
	}

	@Override
	public void onEntered(final ActiveEntity entity, final StendhalRPZone zone, final int newX, final int newY) {
		if ((enterMessage != null) && (entity instanceof Player)) {
			/*
			 * Needs to be delayed to avoid the message appearing before server
			 * welcome on login. Being delayed also means it would be delivered
			 * even if the player is no longer in the zone after the delay (such
			 * as logging in at dreamscape), so a special sender is needed. This
			 * could mean some inadvertently lost messages under certain unusual
			 * conditions.
			 */
			if (warnPlayer()) {
				new ConditionalDelayedPlayerTextSender((Player) entity, enterMessage, NotificationType.WARNING, 1);
			} else {
				new ConditionalDelayedPlayerTextSender((Player) entity, enterMessage, NotificationType.SCENE_SETTING, 1);
			}
		}
	}

	@Override
	public void onExited(final ActiveEntity entity, final StendhalRPZone zone, final int newX, final int newY) {
		if ((leaveMessage != null) && (entity instanceof Player)) {
			// needs to be delayed since normal messages get lost in case the player leaves zone
			if (warnPlayer()) {
				new DelayedPlayerTextSender((Player) entity, leaveMessage, NotificationType.WARNING, 1);
			} else {
				new DelayedPlayerTextSender((Player) entity, leaveMessage, NotificationType.SCENE_SETTING, 1);
			}
		}
	}

	@Override
	public void onMoved(final ActiveEntity entity, final StendhalRPZone zone, final int oldX, final int oldY, final int newX, final int newY) {
		// required by interface
	}

	/**
	 * Called when this object is added to a zone.
	 *
	 * @param zone
	 *            The zone this was added to.
	 */
	@Override
	public void onAdded(final StendhalRPZone zone) {
		super.onAdded(zone);

		if (coversZone) {
			setSize(zone.getWidth(), zone.getHeight());
		}
		zone.addMovementListener(this);
	}

	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param zone
	 *            The zone this will be removed from.
	 */
	@Override
	public void onRemoved(final StendhalRPZone zone) {
		zone.removeMovementListener(this);
		super.onRemoved(zone);
	}

	/**
	 * A sender for the delayed message that delivers the message only if the
	 * player is still on the same zone as the MessagingArea.
	 */
	private class ConditionalDelayedPlayerTextSender extends DelayedPlayerTextSender {
		public ConditionalDelayedPlayerTextSender(Player player,
				String message, NotificationType type, int seconds) {
			super(player, message, type, seconds);
		}

		@Override
		public void onTurnReached(final int currentTurn) {
			if (MessagingArea.this.getZone() == player.getZone()) {
				super.onTurnReached(currentTurn);
			}
		}
	}

	@Override
	public void beforeMove(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		// nothing to do before a movement
	}

	public boolean warnPlayer() {
		return isWarning;
	}
}
