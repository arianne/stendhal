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

	private String cloneOf = null;

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
		onIdea(object.get("idea"));

		// if this is a clone, the name of the original NPC will be displayed
		if (object.has("cloned")) {
			cloneOf = object.get("cloned");
		}
	}

	/**
	 * Called when the idea changes.
	 *
	 * @param idea new idea
	 */
	private void onIdea(String idea) {
		this.idea = idea;
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
			onIdea(changes.get("idea"));
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
			onIdea(null);
			fireChange(PROP_IDEA);
		}
	}

	/**
	 * Checks if this entity is a clone of another NPC.
	 */
	public boolean isClone() {
		return cloneOf != null;
	}
}
