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

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * This respwan point has to be "used" to get the item. After that, it will
 * slowly regrow; there are several regrowing steps in which the graphics will
 * change to show the progress.
 *
 * @author daniel, hendrik
 */
public abstract class GrowingPassiveEntityRespawnPoint extends
		PassiveEntityRespawnPoint {

	private static Logger logger = Logger.getLogger(GrowingPassiveEntityRespawnPoint.class);

	/** How long it takes for one growing step. */
	private static final int GROWING_RATE = 3000;

	/** Current stage of the growth. */
	private int ripeness;

	/** Maximum ripeness, at which stage the item will be ripe for harvesting. */
	private int maxRipeness;

	/**
	 * Get the maximum ripeness.
	 *
	 * @return the stage at which the grown item is ready for harvesting
	 */
	protected final int getMaxRipeness() {
		return maxRipeness;
	}

	/**
	 * Set the maximum ripeness, at which the grown item is ready for harvesting.
	 *
	 * @param maxRipeness maximum ripeness
	 */
	final void setMaxRipeness(final int maxRipeness) {
		this.maxRipeness = maxRipeness;
		put("max_ripeness", maxRipeness);
	}

	/**
	 * Register RPClass for GrowingPassiveEntityRespawnPoints. The used
	 * identifier is "growing_entity_spawner".
	 */
	public static void generateRPClass() {
		final RPClass grainFieldClass = new RPClass("growing_entity_spawner");
		grainFieldClass.isA("plant_grower");
		grainFieldClass.addAttribute("action_name", Type.STRING);
		grainFieldClass.addAttribute("max_ripeness", Type.BYTE);
		grainFieldClass.addAttribute("ripeness", Type.BYTE);
	}

	/**
	 * Initialize various data for the grower.
	 *
	 * @param clazz entity class
	 * @param actionName name of the use action
	 * @param maxRipeness maximum ripeness stage of the grower
	 * @param width width of the grower entity
	 * @param height height of the grower entity
	 */
	private void init(final String clazz, final String actionName, final int maxRipeness,
			final int width, final int height) {
		this.maxRipeness = maxRipeness;
		setRPClass("growing_entity_spawner");
		put("type", "growing_entity_spawner");
		setEntityClass(clazz);
		put("action_name", actionName);
		put("max_ripeness", maxRipeness);

		setSize(width, height);
		setResistance(30);
	}

	/**
	 * Create a GrowingPassiveEntityRespawnPoint from an RPObject. Used for
	 * restoring growers stored in the zone.
	 *
	 * @param object The RPObject to be converted
	 * @param type Entity class
	 * @param itemName Name of the spawned item
	 * @param actionName Name of the use action
	 * @param maxRipeness Maximum ripeness stage of the grower
	 * @param growthRate Average time between growth steps in turns
	 */
	public GrowingPassiveEntityRespawnPoint(final RPObject object, final String type, final String itemName,
			final String actionName, final int maxRipeness, final int growthRate) {
		super(object, itemName, growthRate);
		init(type, actionName, maxRipeness, getInt("width"), getInt("height"));
		ripeness = getInt("ripeness");

		update();
		// Start the timer, if needed
		if (ripeness < maxRipeness) {
			SingletonRepository.getTurnNotifier().notifyInTurns(getRandomTurnsForRegrow(), this);
		}
	}

	/**
	 * Create a new GrowingPassiveEntityRespawnPoint.
	 *
	 * @param type Entity class
	 * @param itemName Name of the grown entity
	 * @param actionName Name of the use action
	 * @param maxRipeness Maximum ripeness stage of the grower
	 * @param width Width of the grower entity
	 * @param height Height of the grower entity
	 */
	public GrowingPassiveEntityRespawnPoint(final String type, final String itemName, final String actionName,
			final int maxRipeness, final int width, final int height) {
		super(itemName, GROWING_RATE);
		init(type, actionName, maxRipeness, width, height);
	}

	@Override
	public void update() {
		super.update();
		if (has("ripeness")) {
			ripeness = getInt("ripeness");
		}
	}

	/**
	 * Set the current ripeness stage of the grower.
	 *
	 * @param ripeness
	 */
	protected void setRipeness(final int ripeness) {
		this.ripeness = ripeness;
		put("ripeness", ripeness);
	}

	/**
	 * Get the current ripeness stage of the grower.
	 *
	 * @return ripeness
	 */
	protected int getRipeness() {
		return ripeness;
	}

	@Override
	protected void growNewFruit() {
		setRipeness(ripeness + 1);
		logger.debug("Grow " + ripeness + " up to " + maxRipeness);
		notifyWorldAboutChanges();

		if (ripeness < maxRipeness) {
			SingletonRepository.getTurnNotifier().notifyInTurns(getRandomTurnsForRegrow(), this);
		}
	}

	@Override
	public void onFruitPicked(final Item picked) {
		super.onFruitPicked(picked);
		setRipeness(0);
		notifyWorldAboutChanges();
	}



	@Override
	public void onItemPickedUp(Player player) {
		// do nothing, harvest is already counted in Use-action
	}

	@Override
	public void setToFullGrowth() {
		setRipeness(maxRipeness);
		// don't grow anything new until someone harvests
		SingletonRepository.getTurnNotifier().dontNotify(this);
	}

}
