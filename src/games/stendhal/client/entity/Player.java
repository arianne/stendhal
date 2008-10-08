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
	private static final String LAST_PLAYER_KILL_TIME = "last_player_kill_time";
	/**
	 * Away property.
	 */
	public static final Property PROP_AWAY = new Property();
	/**
	 * Happy property.
	 */
	public static final Property PROP_HAPPY = new Property();
	/**
	 * Grumpy property.
	 */
	public static final Property PROP_GRUMPY = new Property();
	/**
	 * The away message of this player.
	 */
	private String away;
	/**
	 * The grumpy message of this player.
	 */
	private String grumpy;
	/**
	 * The happy message of this player.
	 */
	private String happy;
	private boolean badboy;

	/**
	 * Create a player entity.
	 */
	public Player() {
		away = null;
		grumpy = null;
		happy = null;
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
	
	/**
	 * Determine if the player is happy.
	 * 
	 * @return <code>true</code> if the player is happy.
	 */
	public boolean isHappy() {
		return (getHappy() != null);
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
	 * Get the happy message.
	 * 
	 * @return The happy text, or <code>null</code> if not happy.
	 */
	public String getHappy() {
		return happy;
	}
	
	/**
	 * An away message was set/cleared.
	 * 
	 * @param message
	 *            The away message, or <code>null</code> if no-longer away.
	 */
	protected void onAway(final String message) {
		if (message != null) {
			addTextIndicator("Away", NotificationType.INFORMATION);
		} else {
			addTextIndicator("Back", NotificationType.INFORMATION);
		}
	}
	
	/**
	 * A happy message was set/cleared.
	 * 
	 * @param message
	 *            The happy message, or <code>null</code> if no-longer happy.
	 */
	protected void onHappy(final String message) {
		if (message != null) {
			addTextIndicator("Happy", NotificationType.INFORMATION);
		}
	}

	/**
	 * Determine if the player is grumpy.
	 * 
	 * @return <code>true</code> if the player is grumpy.
	 */
	public boolean isGrumpy() {
		return (getGrumpy() != null);
	}

	/**
	 * Get the grumpy message.
	 * 
	 * @return The grumpy text, or <code>null</code> if not grumpy.
	 */
	public String getGrumpy() {
		return grumpy;
	}

	/**
	 * An away message was set/cleared.
	 * 
	 * @param message
	 *            The away message, or <code>null</code> if no-longer away.
	 */
	protected void onGrumpy(final String message) {
		if (message != null) {
			addTextIndicator("Grumpy", NotificationType.INFORMATION);
		} else {
			addTextIndicator("Receptive", NotificationType.INFORMATION);

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
			/*
			 * Filter out a player "changing" to the same message
			 */
			if (!object.has("away")
					|| !object.get("away").equals(changes.get("away"))) {
				away = changes.get("away");
				fireChange(PROP_AWAY);
				onAway(away);
			}
		}
		if (changes.has("happy")) {
			/*
			 * Filter out a player "changing" to the same message
			 */
			if (!object.has("happy")
					|| !object.get("happy").equals(changes.get("happy"))) {
				happy = changes.get("happy");
				fireChange(PROP_HAPPY);
				onHappy(happy);
			}
		}
		if (changes.has("grumpy")) {
			/*
			 * Filter out a player "changing" to the same message
			 */
			if (!object.has("grumpy")
					|| !object.get("grumpy").equals(changes.get("grumpy"))) {
				grumpy = changes.get("grumpy");
				fireChange(PROP_GRUMPY);
				onGrumpy(grumpy);
			}
		}
		
		if (changes.has(LAST_PLAYER_KILL_TIME)) {
			badboy = true;
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
			away = null;
			fireChange(PROP_AWAY);
			onAway(null);
		}
		if (changes.has("happy")) {
			happy = null;
			fireChange(PROP_HAPPY);
			onHappy(null);
		}
		if (changes.has("grumpy")) {
			grumpy = null;
			fireChange(PROP_GRUMPY);
			onGrumpy(null);
		}
		if (changes.has(LAST_PLAYER_KILL_TIME)) {
			badboy = false;
		}
	}
}
