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
package games.stendhal.client.entity;

//
//

import games.stendhal.common.constants.SoundLayer;
import marauroa.common.game.RPObject;

/**
 * A domestic animal entity.
 */
public abstract class DomesticAnimal extends RPEntity {
	/**
	 * DomesticAnimal idea property.
	 */
	public static final Property PROP_IDEA = new Property();

	/**
	 * DomesticAnimal weight property.
	 */
	public static final Property PROP_WEIGHT = new Property();

	/**
	 * The animal's weight (0-100).
	 */
	private int weight;

	/**
	 * The animal's idea.
	 */
	private String idea;

	//
	// DomesticAnimal
	//

	/**
	 * Get the idea setting.
	 * 
	 * @return The animal's idea.
	 */
	public String getIdea() {
		return idea;
	}

	/**
	 * Get the weight.
	 * 
	 * @return The animal's weight.
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * The idea changed.
	 * 
	 * @param idea
	 *            The idea, or <code>null</code>.
	 */
	protected void onIdea(final String idea) {
		if ("eat".equals(idea)) {
			probableChat(15);
		} else if ("food".equals(idea) || "walk".equals(idea) || "follow".equals(idea)) {
			probableChat(20);
		} 
	}

	protected abstract void probableChat(final int chance);

	//
	// Entity
	//

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		addSounds(SoundLayer.CREATURE_NOISE.groupName, "eat", "eat-1");
		
		/*
		 * Idea
		 */
		if (object.has("idea")) {
			idea = object.get("idea");
		} else {
			idea = null;
		}

		/*
		 * Weight
		 */
		if (object.has("weight")) {
			weight = object.getInt("weight");
		} else {
			weight = 0;
		}

		onIdea(idea);
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
			onIdea(idea);
			fireChange(PROP_IDEA);
		}

		/*
		 * Weight
		 */
		if (changes.has("weight")) {
			final int oldWeight = weight;
			weight = changes.getInt("weight");

			if (weight > oldWeight) {
				playRandomSoundFromCategory(SoundLayer.CREATURE_NOISE.groupName, "eat");
			}

			fireChange(PROP_WEIGHT);
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
			onIdea(idea);
			fireChange(PROP_IDEA);
		}
	}
}
