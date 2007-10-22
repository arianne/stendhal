/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.player.Player;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;

/**
 * A baby dragon is a domestic animal that can be owned by a player.
 * It eats chicken from the ground.
 * They move much faster than sheep
 * Ideally baby dragon attack animals for you
 */
/**
 * @author kymara
 * (based on sheep by Daniel Herding)
 *
 */
public class BabyDragon extends Pet {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(BabyDragon.class);

	static {
		HP = 500;

		incHP = 10;

		ATK = 15;

		DEF = 40;

		XP = 100;
	}

	public static void generateRPClass() {
		try {
			RPClass baby_dragon = new RPClass("baby_dragon");
			baby_dragon.isA("pet");
			//baby_dragon.add("weight", Type.BYTE);
			//baby_dragon.add("eat", Type.FLAG);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild baby dragon.
	 * @throws AttributeNotFoundException
	 */
	public BabyDragon() {
		this(null);
	}

	/**
	 * Creates a new baby dragon that is owned by a player.
	 * @throws AttributeNotFoundException
	 */
	public BabyDragon(Player owner) {
		super(owner);
		setRPClass("baby_dragon");
		put("type", "baby_dragon");

		baseSpeed = 0.9;
		update();
	}

	/**
	 * Creates a Baby Dragon based on an existing pet RPObject, and assigns it to
	 * a player.
	 * @param object
	 * @param owner The player who should own the baby dragon
	 * @throws AttributeNotFoundException
	 */
	public BabyDragon(RPObject object, Player owner) {
		super(object, owner);

		setRPClass("baby_dragon");
		put("type", "baby_dragon");

		baseSpeed = 0.9;
		update();
	}

	@Override
	public String describe() {
		String text;

		if (hasDescription()) {
			text = getDescription();
		} else {
			text = "You see a nippy little baby dragon; it looks like it weighs about " + weight + ".";
		}

		return (text);
	}
}
