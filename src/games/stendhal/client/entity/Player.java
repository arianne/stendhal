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
public class Player extends AudibleEntity {
	
	/**
	 * Away property.
	 */
	public static final Property PROP_AWAY = new Property();
	/**
	 * Grumpy property.
	 */
	public static final Property PROP_GRUMPY = new Property();
	
	
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
		if (message != null) {
			addTextIndicator("Away", NotificationType.INFORMATION);
		} else {
			addTextIndicator("Back", NotificationType.INFORMATION);
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


	@Override
	public void onTalk(String text) {
		if (!User.isIgnoring(this.getName())) {
			super.onTalk(text);
		}
	}

	@Override
	public void initialize(RPObject object) {
		super.initialize(object);
		
		addSounds("sfx", "attack",
			"punch-1"   , "punch-2", "punch-3",
			"punch-4"   , "punch-5", "punch-6",
			"swingaxe-1", "slap-1" , "arrow-1");
	}

	@Override
	public void onDamaged(Entity attacker, int damage) {
		super.onDamaged(attacker, damage);
		playRandomSoundFromCategory("sfx", "attack");
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
