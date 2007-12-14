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

import java.util.Arrays;

import org.apache.log4j.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;

/**
 * A cat is a domestic animal that can be owned by a player.
 * <p>
 * It eats chicken from the ground.
 * <p>
 * They move much faster than sheep
 * <p>
 * Ideally cats attack rats for you
 * 
 * @author kymara (based on sheep by Daniel Herding)
 * 
 */
public class Cat extends Pet {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Cat.class);

	private void setUp() {

		foodName = Arrays.asList("chicken", "trout", "cod", "mackerel", "char",
				"perch", "roach", "surgeonfish", "clownfish");

		HP = 200;

		incHP = 4; // each chicken or fish would give +5 HP

		ATK = 10;

		DEF = 30;

		XP = 100;

		baseSpeed = 0.9;

		setATK(ATK);
		setDEF(DEF);
		setXP(XP);
		setBaseHP(HP);
		setHP(HP);

	}

	public static void generateRPClass() {
		try {
			RPClass cat = new RPClass("cat");
			cat.isA("pet");
			// cat.add("weight", Type.BYTE);
			// cat.add("eat", Type.FLAG);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild Cat.
	 * 
	 * @throws AttributeNotFoundException
	 */
	public Cat() {
		this(null);
	}

	/**
	 * Creates a new Cat that is owned by a player.
	 * 
	 * @throws AttributeNotFoundException
	 */
	public Cat(Player owner) {
		// call set up before parent constructor is called as it needs those
		// values
		super(owner);
		setUp();
		setRPClass("cat");
		put("type", "cat");
		update();
	}

	/**
	 * Creates a Cat based on an existing cat RPObject, and assigns it to a
	 * player.
	 * 
	 * @param object
	 * @param owner
	 *            The player who should own the cat
	 * @throws AttributeNotFoundException
	 */
	public Cat(RPObject object, Player owner) {
		// call set up before parent constructor is called as it needs those
		// values
		super(object, owner);
		setUp();
		setRPClass("cat");
		put("type", "cat");
		update();
	}

	@Override
	public String describe() {
		String text;

		if (hasDescription()) {
			text = getDescription();
		} else {
			text = "You see a friendly cat; it looks like it weighs about "
					+ weight + ".";
		}

		return (text);
	}
}
