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
 * A cat is a domestic animal that can be owned by a player.
 * Ideally it eats meat from the ground.
 * They move much faster than sheep
 * Ideally cats attack rats for you
 */
/**
 * @author kymara
 * (based on sheep by Daniel Herding)
 *
 */
public class Cat extends Pet {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Cat.class);

	static {
		HP = 200;

		incHP = 6;

		ATK = 10;

		DEF = 30;

		XP = 0;
	}

	public static void generateRPClass() {
		try {
			RPClass cat = new RPClass("cat");
			cat.isA("pet");
			//cat.add("weight", Type.BYTE);
			//cat.add("eat", Type.FLAG);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild Cat.
	 * @throws AttributeNotFoundException
	 */
	public Cat() {
		this(null);
	}

	/**
	 * Creates a new Cat that is owned by a player.
	 * @throws AttributeNotFoundException
	 */
	public Cat(Player owner) {
		super(owner);
		setRPClass("cat");
		put("type", "cat");
		put("title", "cat");
		baseSpeed = 0.9;

		update();
		logger.debug("Created Cat: " + this);
	}

	/**
	 * Creates a Cat based on an existing cat RPObject, and assigns it to
	 * a player.
	 * @param object
	 * @param owner The player who should own the cat
	 * @throws AttributeNotFoundException
	 */
	public Cat(RPObject object, Player owner) {
		super(object, owner);

		setRPClass("cat");
		put("type", "cat");
		baseSpeed = 0.9;
		update();
		logger.debug("Created Cat: " + this);
	}

	@Override
	public String describe() {
		String text = "You see a friendly cat; it looks like it weighs about " + weight + ".";
		if (hasDescription()) {
			text = getDescription();
		}
		return (text);
	}

}
