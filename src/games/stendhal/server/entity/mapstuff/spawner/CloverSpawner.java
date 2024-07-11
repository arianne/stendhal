/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.Pair;


/**
 * Manages four-leaf clover spawning.
 *
 * TODO: initialize with quest or achievement instead of zone configurator (maybe)
 */
public class CloverSpawner extends PassiveEntityRespawnPoint {

	private static Logger logger = Logger.getLogger(CloverSpawner.class);

	private boolean initialized = false;

	private Item grownClover;

	private static CloverSpawner instance;


	/**
	 * Retrieves singleton instance.
	 */
	public static CloverSpawner get() {
		if (instance == null) {
			instance = new CloverSpawner();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private CloverSpawner() {
		super("four-leaf clover", 0);
		grownClover = null;
		hasPickableFruit = false;
	}

	/**
	 * Adds clover to world and starts spawn cycle.
	 */
	public void init() {
		if (initialized) {
			// don't allow more than one clover spawner in world
			logger.warn("Tried to re-initialize clover spawner");
			return;
		}
		initialized = true;

		// spawn clover at server start
		spawn();
		// set next spawn at midnight
		TurnNotifier.get().notifyInSeconds(TimeUtil.secondsToMidnight(), CloverSpawner.get());
	}

	/**
	 * Adds clover to world.
	 */
	private boolean spawn() {
		// remove clover & grower from current zone
		StendhalRPZone currentZone = getZone();
		if (currentZone != null) {
			currentZone.remove(grownClover);
			currentZone.remove(this);
		}
		grownClover = null;

		StendhalRPWorld world = SingletonRepository.getRPWorld();
		// acceptable zones
		List<StendhalRPZone> zones = new ArrayList<>();
		for (String region: world.getRegions()) {
			for (StendhalRPZone zone: world.getAllZonesFromRegion(region, true, true, true)) {
				if (zone.getLevel() == 0) {
					zones.add(zone);
				}
			}
		}

		StendhalRPZone newZone = Rand.rand(zones);
		Pair<Integer, Integer> pos = newZone.getRandomSpawnPosition(this, false);
		if (pos == null) {
			logger.warn("Unable to find suitable spawn position in zone " + newZone.getName()
					+ ", retrying in 15 minutes");
			hasPickableFruit = false;
			return false;
		}
		setPosition(pos.first(), pos.second());
		newZone.add(this);
		// set state to allow player to pick up
		growNewFruit();
		logger.info("Spawned four-leaf clover at " + newZone.getName() + " " + getX() + "," + getY());
		return true;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		// spawn a new clover
		if (spawn()) {
			// set next respawn for 24 hours
			TurnNotifier.get().notifyInSeconds(TimeUtil.secondsToMidnight(), this);
		} else {
			// failed, retry in 15 minutes
			TurnNotifier.get().notifyInSeconds(TimeUtil.SECONDS_IN_MINUTE * 15, this);
		}
	}

	@Override
	public void onFruitPicked(final Item picked) {
		hasPickableFruit = false;
		grownClover = null;
		if (picked != null) {
			picked.setPlantGrower(null);
		}
	}

	@Override
	protected Item growNewFruit() {
		Item grownItem = super.growNewFruit();
		if (grownItem != null) {
			grownClover = grownItem;
		}
		return grownClover;
	}

	@Override
	public void setToFullGrowth() {
		growNewFruit();
	}
}
