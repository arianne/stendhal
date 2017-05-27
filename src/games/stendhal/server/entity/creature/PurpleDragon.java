/***************************************************************************
 *                   (C) Copyright 2015-2016 - Marauroa                    *
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

import games.stendhal.common.ItemTools;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;

/**
 * A purple dragon is a domestic animal that can be owned by a player.
 * <p>
 * It eats ham or pizza from the ground.
 * <p>
 * They move much faster than sheep
 * <p>
 * Purple dragons attack animals which attack them
 */
public class PurpleDragon extends Pet {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(PurpleDragon.class);

	@Override
	void setUp() {
		HP = 1450;
		incHP = 100;
		ATK = 535;
		DEF = 250;
		lv_cap = 25;
		XP = 28900;
		baseSpeed = 0.9;

		setAtk(ATK);
		setDef(DEF);
		//setLVCap(LVCap);
		setXP(XP);
		setBaseHP(HP);
		setHP(HP);
		setSize(2, 2);

	}

	public static void generateRPClass() {
		try {
			final RPClass purple_dragon = new RPClass("purple_dragon");
			purple_dragon.isA("pet");
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild purple dragon.
	 */
	public PurpleDragon() {
		this(null);
	}

	/**
	 * Creates a new purple dragon that may be owned by a player.
	 * @param owner The player who should own the purple dragon
	 */
	public PurpleDragon(final Player owner) {
		super();
		setOwner(owner);
		setUp();
		setRPClass("purple_dragon");
		put("type", "purple_dragon");

		if (owner != null) {
			// add pet to zone and create RPID to be used in setPet()
			owner.getZone().add(this);
			owner.setPet(this);
		}

		update();
	}

	/**
	 * Creates a Purple Dragon based on an existing pet RPObject, and assigns it
	 * to a player.
	 *
	 * @param object
	 * @param owner
	 *            The player who should own the purple dragon
	 */
	public PurpleDragon(final RPObject object, final Player owner) {

		super(object, owner);


		setRPClass("purple_dragon");
		put("type", "purple_dragon");

		update();
	}

	@Override
	protected
	List<String> getFoodNames() {
		return Arrays.asList("ham", "pizza", "meat");
	}

	@Override
	public String describe() {
		String text = "You see a " + ItemTools.itemNameToDisplayName(get("type")) + "; it looks like it weighs about " + weight + " and is level " + getLevel() + ". Def XP: " +  getDefXP() + "";
		if (hasDescription()) {
			text = getDescription();
		}
		return (text);
	}
}
