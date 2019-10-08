/* $Id$ */
/***************************************************************************
 *		      (C) Copyright 2003 - Marauroa		      *
 ***************************************************************************
 ***************************************************************************
 *									 *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.				   *
 *									 *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.common.NotificationType;
import marauroa.common.game.RPObject;

/** A Player entity. */
public class Player extends RPEntity {

	/**
	 * Away property.
	 */
	public static final Property PROP_AWAY = new Property();
	/**
	 * Grumpy property.
	 */
	public static final Property PROP_GRUMPY = new Property();
	/**
	 * Player killer property.
	 */
	public static final Property PROP_PLAYER_KILLER = new Property();

	private static final String LAST_PLAYER_KILL_TIME = "last_player_kill_time";

	/**
	 * The away message of this player.
	 */
	private String away;
	/**
	 * The grumpy message of this player.
	 */
	private String grumpy;
	private boolean badboy;

	/**
	 * Create a player entity.
	 */
	public Player() {
		away = null;
		grumpy = null;
	}

	//
	// Player
	//

	/**
	 * Determine if the player is away.
	 *
	 * @return <code>true</code> if the player is away.
	 */
	public boolean isAway() {
		return (getAway() != null);
	}

	public boolean isBadBoy() {
		return badboy;
	}

	/**
	 * Get the away message.
	 *
	 * @return The away text, or <code>null</code> if not away.
	 */
	public String getAway() {
		return away;
	}

	/**
	 * An away message was set/cleared.
	 *
	 * @param message
	 *            The away message, or <code>null</code> if no-longer away.
	 */
	protected void onAway(final String message) {
		// Filter out events about changing to same message
		if (messageChanged(away, message)) {
			away = message;
			fireChange(PROP_AWAY);
			if (message != null) {
				addTextIndicator("Away", NotificationType.INFORMATION);
			} else {
				addTextIndicator("Back", NotificationType.INFORMATION);
			}
		}
	}

	/**
	 * Determine if the player is grumpy.
	 *
	 * @return <code>true</code> if the player is grumpy.
	 */
	public boolean isGrumpy() {
		return (grumpy != null);
	}

	/**
	 * An away message was set/cleared.
	 *
	 * @param message
	 *            The away message, or <code>null</code> if no-longer away.
	 */
	private void onGrumpy(final String message) {
		// Filter out events about changing to same message
		if (messageChanged(grumpy, message)) {
			grumpy = message;
			fireChange(PROP_GRUMPY);
			if (message != null) {
				addTextIndicator("Grumpy", NotificationType.INFORMATION);
			} else {
				addTextIndicator("Receptive", NotificationType.INFORMATION);
			}
		}
	}

	/**
	 * Check if a message string has changed.
	 *
	 * @param oldMessage
	 * @param newMessage
	 * @return <code>true</code> if the message has changed
	 */
	private boolean messageChanged(String oldMessage, String newMessage) {
		return ((newMessage == null) && (oldMessage != null))
		|| ((newMessage != null) && !newMessage.equals(oldMessage));
	}

	@Override
	public void onTalk(String text) {
		if (!User.isIgnoring(this.getName())) {
			super.onTalk(text);
		}
	}

	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		if (changes.has("away")) {
			onAway(changes.get("away"));
		}

		if (changes.has("grumpy")) {
			onGrumpy(changes.get("grumpy"));
		}

		if (changes.has(LAST_PLAYER_KILL_TIME)) {
			badboy = true;
			fireChange(PROP_PLAYER_KILLER);
		}
	}

	/**
	 * The object removed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		if (changes.has("away")) {
			onAway(null);
		}
		if (changes.has("grumpy")) {
			onGrumpy(null);
		}
		if (changes.has(LAST_PLAYER_KILL_TIME)) {
			badboy = false;
			fireChange(PROP_PLAYER_KILLER);
		}
	}
}
