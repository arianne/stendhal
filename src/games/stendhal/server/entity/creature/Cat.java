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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;
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

	@Override
	void setUp() {
		HP = 200;
		// each chicken or fish would give +5 HP
		incHP = 4;
		ATK = 10;
		DEF = 30;
		XP = 100;
		baseSpeed = 0.9;

		setAtk(ATK);
		setDef(DEF);
		setXP(XP);
		setBaseHP(HP);
		setHP(HP);
	}

	public static void generateRPClass() {
		try {
			final RPClass cat = new RPClass("cat");
			cat.isA("pet");
			// cat.add("weight", Type.BYTE);
			// cat.add("eat", Type.FLAG);
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild Cat.
	 */
	public Cat() {
		this(null);
	}

	/**
	 * Creates a new Cat that may be owned by a player.
	 * @param owner owning player, or <code>null</code>
	 */
	public Cat(final Player owner) {
		// call set up before parent constructor is called as it needs those
		// values
		super();
		setOwner(owner);
		setUp();
		setRPClass("cat");
		put("type", "cat");

		if (owner != null) {
			// add pet to zone and create RPObject.ID to be used in setPet()
			owner.getZone().add(this);
			owner.setPet(this);
		}

		update();
	}

	/**
	 * Creates a Cat based on an existing cat RPObject, and assigns it to a
	 * player.
	 *
	 * @param object object containing the data for the Cat
	 * @param owner
	 *            The player who should own the cat
	 */
	public Cat(final RPObject object, final Player owner) {
		super(object, owner);
		setRPClass("cat");
		put("type", "cat");
		update();
	}

	@Override
	protected List<String> getFoodNames() {
		return Arrays.asList(
				//"meat",
				//"ham",
				//"tripe",
				"milk",
				"chicken",
				"trout",
				"cod",
				"mackerel",
				"char",
				"perch",
				"roach",
				"surgeonfish",
				"clownfish"
				);
	}

	/**
	 * Does this domestic animal take part in combat?
	 *
	 * @return true, if it can be attacked by creatures, false otherwise
	 */
	@Override
	protected boolean takesPartInCombat() {
		return false;
	}

}
