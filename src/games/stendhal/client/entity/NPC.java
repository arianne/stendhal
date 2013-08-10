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

import marauroa.common.game.RPObject;

/**
 * An NPC entity.
 */
public class NPC extends RPEntity {
	/**
	 * Idea property for NPCs and domestic animals.
	 */
	public static final Property PROP_IDEA = new Property();
	
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
	 * Ask NPC if they are attending.
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

		/*
		 * Idea
		 */
		if (object.has("idea")) {
			idea = object.get("idea");
		} else {
			idea = null;
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

		/*
		 * Idea
		 */
		if (changes.has("idea")) {
			idea = changes.get("idea");
			fireChange(PROP_IDEA);
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
			fireChange(PROP_IDEA);
		}
	}
}
