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
import java.util.List;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;

import org.apache.log4j.Logger;

/**
 * A baby dragon is a domestic animal that can be owned by a player.
 * <p>
 * It eats ham or pizza from the ground.
 * <p>
 * They move much faster than sheep
 * <p>
 * Baby dragons attack animals which attack them
 * 
 * @author kymara (based on sheep by Daniel Herding)
 * 
 */
public class BabyDragon extends Pet {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(BabyDragon.class);

	private void setUp() {

		HP = 500;

		incHP = 6;

		ATK = 15;

		DEF = 40;

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
			final RPClass baby_dragon = new RPClass("baby_dragon");
			baby_dragon.isA("pet");
			// baby_dragon.add("weight", Type.BYTE);
			// baby_dragon.add("eat", Type.FLAG);
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild baby dragon.
	 */
	public BabyDragon() {
		this(null);
	}

	/**
	 * Creates a new baby dragon that may be owned by a player.
	 * @param owner The player who should own the baby dragon
	 */
	public BabyDragon(final Player owner) {
		super();
		setOwner(owner);
		setUp();
		setRPClass("baby_dragon");
		put("type", "baby_dragon");

		if (owner != null) {
			// add pet to zone and create RPID to be used in setPet()
			owner.getZone().add(this);
			owner.setPet(this);
		}

		update();
	}

	/**
	 * Creates a Baby Dragon based on an existing pet RPObject, and assigns it
	 * to a player.
	 * 
	 * @param object
	 * @param owner
	 *            The player who should own the baby dragon
	 */
	public BabyDragon(final RPObject object, final Player owner) {
		
		super(object, owner);
		

		setRPClass("baby_dragon");
		put("type", "baby_dragon");

		update();
	}

	@Override
	protected
	List<String> getFoodNames() {
		return Arrays.asList("ham", "pizza", "meat");
	}
}
