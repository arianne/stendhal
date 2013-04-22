/* $Id$ */
/***************************************************************************
 *						(C) Copyright 2003 - Marauroa					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.common.constants.SoundLayer;
import marauroa.common.game.RPObject;

/**
 * An NPC entity.
 */
public class NPC extends AudibleEntity {
	
	/**
	 * The NPC's idea.
	 */
	private String idea;
	
	/**
	 * Get the idea setting.
	 * 
	 * @return The NPC's idea.
	 */
	public String getIdea() {
		return idea;
	}
	
	/**
	 * Ask NPC if they are attending
	 * 
	 * @return true if attending.
	 */
	public boolean isAttending() {
		return (idea != null);
	}
	
	//
	// Entity
	//

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *			  The object.
	 * 
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		final String type = getType();
		
		/*
		 * Idea
		 */
		if (object.has("idea")) {
			idea = object.get("idea");
		} else {
			idea = null;
		}
		
		if (type.startsWith("npc")) {
			if (name.equals("Diogenes")) {
				addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "laugh-1", "laugh-2");
			} else if (name.equals("Carmen")) {
				addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "giggle-1", "giggle-2");
			} else if (name.equals("Nishiya")) {
				addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "cough-11", "cough-2", "cough-3");
			} else if (name.equals("Margaret")) {
				addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "hiccup-1", "hiccup-2", "hiccup-3");
			} else if (name.equals("Sato")) {
				addSounds(SoundLayer.CREATURE_NOISE.groupName, "move", "hiccup-1", "sneeze-1");
			}
		}
	}

	/**
	 * When the entity's position changed.
	 * 
	 * @param x
	 *			  The new X coordinate.
	 * @param y
	 *			  The new Y coordinate.
	 */
	@Override
	protected void onPosition(final double x, final double y) {
		super.onPosition(x, y);
		playRandomSoundFromGroup(SoundLayer.CREATURE_NOISE.groupName, "move", 20000);
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

		/*
		 * Idea
		 */
		if (changes.has("idea")) {
			idea = changes.get("idea");
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

		/*
		 * Idea
		 */
		if (changes.has("idea")) {
			idea = null;
		}
	}
	
	@Override
	public boolean ignoresCollision() {
		return super.ignoresCollision();
	}
}
