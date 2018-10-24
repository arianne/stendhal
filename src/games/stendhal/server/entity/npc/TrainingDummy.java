/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.Entity;

/**
 * An entity that can be used for training a player's ATK & RATK stats.
 */
public class TrainingDummy extends NPC {

	public TrainingDummy() {
		super();

		init("other/training_dummy");
	}

	public TrainingDummy(final String image) {
		super();

		init(image);
	}

	/**
	 * Initializes attributes.
	 * @param image
	 * 		Sprite image to be used.
	 */
	private void init(final String image) {
		put("unnamed", "");
		put("no_hpbar", "");

		setEntityClass(image);
		setName("training dummy");

		initHP(1);
	}

	/**
	 * Allow training dummies to be attacked.
	 */
	@Override
	public boolean isAttackable() {
		return true;
	}

	/**
	 * Override this so training dummies do not lose HP & do not die.
	 */
	@Override
	public void onDamaged(final Entity attacker, final int damage) {
		// training dummies not damaged
	}
}
