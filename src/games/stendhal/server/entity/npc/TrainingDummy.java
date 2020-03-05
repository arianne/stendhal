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

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.SyntaxException;

/**
 * An entity that can be used for training a player's ATK & RATK stats.
 */
public class TrainingDummy extends NPC {

	private static final Logger logger = Logger.getLogger(TrainingDummy.class);

	public static final String RPCLASS_NAME = "training_dummy";


	public static void generateRPClass() {
		try {
			final RPClass dummy = new RPClass(RPCLASS_NAME);
			dummy.isA("npc");
			dummy.addAttribute("melee_only", Type.FLAG, Definition.VOLATILE);
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public TrainingDummy() {
		super();

		init("other/training_dummy", "You see a training dummy.");
	}

	public TrainingDummy(final String image) {
		super();

		init(image, "You see a training dummy.");
	}

	public TrainingDummy(final String image, final String descr) {
		super();

		init(image, descr);
	}

	/**
	 * Initializes attributes.
	 * @param image
	 * 		Sprite image to be used.
	 * @param descr
	 * 		Text to show when player "looks".
	 */
	private void init(final String image, final String descr) {
		setRPClass(RPCLASS_NAME);

		put("unnamed", "");
		put("no_hpbar", "");
		put("no_shadow", "");

		setEntityClass(image);
		setName("training dummy");
		setDescription(descr);

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

	/**
	 * Marks dummy to only be usable with melee weapons only.
	 */
	public void setMeleeOnly(final boolean set) {
		if (set) {
			put("melee_only", "");
		} else {
			remove("melee_only");
		}
	}

	/**
	 * Checks if only melee weapons may be used against this dummy.
	 *
	 * @return
	 * 		<code>true</code> if only melee weapons may be used, <code>false</code> otherwise.
	 */
	public boolean isMeleeOnly() {
		return has("melee_only");
	}

	/**
	 * Checks requirements for attacking this entity.
	 *
	 * @param entity
	 * 		The entity that wants to attack.
	 * @return
	 * 		<code>true</code> if can attack, <code>false</code> otherwise.
	 */
	public boolean canBeAttacked(final RPEntity entity) {
		if (isMeleeOnly()) {
			return entity.nextTo(this);
		}

		return true;
	}
}
