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
package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import org.apache.log4j.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

/**
 * A PassiveEntityRespawnPoint basically is a 1x1 area where a plant, a fruit or
 * another non-moving thing grows. This growing thing is a pickable Item (e.g. a
 * mushroom, an apple); by extending this class, it can also grow something
 * special (e.g. SheepFood).
 * 
 * PassiveEntityRespawnPoint are currently invisible (fully transparent) on the
 * client side. Extend GrowingPassiveEntityRespawnPoint and implement
 * UseListener if grown item should not be dragable without special interaction.
 * 
 * @author Daniel Herding
 * 
 */
public class PassiveEntityRespawnPoint extends Entity implements TurnListener {
	private static Logger logger = Logger.getLogger(PassiveEntityRespawnPoint.class);

	/**
	 * Is there still a fruit that has not yet been picked up?
	 */
	private boolean hasPickableFruit;

	/**
	 * The name of the fruit (Item) that is grown by the PlantGrower.
	 */
	private String growingItemName;

	/**
	 * Remember which turn we were called last to compute the ripeness
	 */
	// private int lastTurn = 0;
	/**
	 * Tells how many turns it takes in average for a new fruit to become ripe.
	 */
	protected int meanTurnsForRegrow;

	public PassiveEntityRespawnPoint(RPObject object, String growingItemName,
			int meanTurnsForRegrow) {
		super(object);
		this.growingItemName = growingItemName;
		this.meanTurnsForRegrow = meanTurnsForRegrow;
		setDescription("It looks like there's "
				+ Grammar.a_noun(growingItemName) + " sprout growing here.");

		setRPClass("plant_grower");
		put("type", "plant_grower");

		setResistance(0);
		// update();
	}

	public PassiveEntityRespawnPoint(String growingItemName,
			int meanTurnsForRegrow) {
		this.growingItemName = growingItemName;
		this.meanTurnsForRegrow = meanTurnsForRegrow;
		setDescription("It looks like there's "
				+ Grammar.a_noun(growingItemName) + " sprout growing here.");

		setRPClass("plant_grower");
		put("type", "plant_grower");

		setResistance(0);
	}

	public static void generateRPClass() {
		RPClass grower = new RPClass("plant_grower");
		grower.isA("entity");
		grower.addAttribute("class", Type.STRING);
	}

	/**
	 * Is called when a fruit has been picked from this plant grower.
	 * 
	 * @param picked
	 *            The fruit that has been picked. Use null for subclasses of
	 *            PlantGrower that don't use items as fruits.
	 */
	public void onFruitPicked(Item picked) {
		hasPickableFruit = false;
		if (picked != null) {
			picked.setPlantGrower(null);
		}
		TurnNotifier.get().notifyInTurns(getRandomTurnsForRegrow(), this);
	}

	protected int getRandomTurnsForRegrow() {
		return Rand.rand(meanTurnsForRegrow, (int) (0.1 * meanTurnsForRegrow));
	}

	/**
	 * Creates a new fruit.
	 */
	protected void growNewFruit() {
		if (!hasPickableFruit) {
			logger.debug("Growing " + growingItemName);

			StendhalRPWorld world = StendhalRPWorld.get();
			StendhalRPZone zone = world.getZone(getID().getZoneID());

			// create a new grown item
			Item grownItem = world.getRuleManager().getEntityManager().getItem(
					growingItemName);
			grownItem.setPlantGrower(this);
			grownItem.setPosition(getX(), getY());

			zone.add(grownItem);
			hasPickableFruit = true;
		}
	}

	public void setToFullGrowth() {
		if (!hasPickableFruit) {
			growNewFruit();
		}
		// don't grow anything new until someone picks a fruit
		TurnNotifier.get().dontNotify(this);
	}

	public void onTurnReached(int currentTurn) {
		growNewFruit();
	}

}
