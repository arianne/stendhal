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

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.Triple;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.util.Observer;
import marauroa.common.Pair;


/**
 * Class that spawn multiple creatures at random points in a zone.
 */
public class DynamicCreatureSpawner extends LinkedHashMap<String, Triple<Integer, Integer, Integer>> implements CreatureSpawner {

	private static final Logger logger = Logger.getLogger(DynamicCreatureSpawner.class);

	/** Zone associated with this spawner. */
	private final StendhalRPZone zone;
	/** Creatures to be spawned at turn reached. */
	final List<Pair<Integer, String>> queued;

	final List<Observer> observers;

	/** Denotes whether turn notifier is active. */
	private int spawnsAt = -1;


	/**
	 * Creates a new spawner.
	 *
	 * @param zone
	 */
	public DynamicCreatureSpawner(final StendhalRPZone zone) {
		this.zone = zone;
		this.observers = new LinkedList<Observer>();
		this.queued = new LinkedList<Pair<Integer, String>>();
	}

	/**
	 * Starts the spawning cycle of all registered creatures.
	 */
	public void init() {
		for (final String name: keySet()) {
			queue(name);
		}
		// NOTE: Do we want to queue all possible spawns of a creature simultaneously instead of
		//       sequentially? E.g. if max active = 10 queue up all 10 instances instead of only the
		//       first.
		startNotifier();
	}

	/**
	 * Disables associated turn notifiers for clean removal.
	 */
	public void remove() {
		SingletonRepository.getTurnNotifier().dontNotify(this);
		// NOTE: should active spawned creatures also be removed?
	}

	/**
	 * Adds a creature to spawn list.
	 *
	 * Registration is represented in a map indexed by creature name. The value is a triple of
	 * consisting of the following integer representations:
	 * - first:  respawn time
	 * - second: max active
	 * - third:  actual acitve
	 *
	 * @param name
	 *   Creature name.
	 * @param max
	 *   Maximum instances allowed at one time from this spawner.
	 */
	public void register(final String name, final int max) {
		if (max < 1) {
			logger.warn("Max must be a positive integer");
			return;
		}
		final DefaultCreature creature = SingletonRepository.getEntityManager().getDefaultCreature(name);
		if (creature == null) {
			logger.warn("Creature \"" + name + "\" not found");
			return;
		}
		put(name, new Triple<Integer, Integer, Integer>(creature.getRespawnTime(), max, 0));
	}

	/**
	 * Retrieves respawn time value of a creature.
	 *
	 * @param name
	 *   Creature name.
	 * @return
	 *   Rate at which creature respawns.
	 */
	private int getRespawnTime(final String name) {
		if (containsKey(name)) {
			return get(name).getFirst();
		}
		return 0;
	}

	/**
	 * Retrieves maximum allowed active spawned instances.
	 *
	 * @param name
	 *   Creature name.
	 * @return
	 *   Maximum allowed creatures at one time.
	 */
	private int getActiveMax(final String name) {
		if (containsKey(name)) {
			return get(name).getSecond();
		}
		return 0;
	}

	/**
	 * Retrieves the current number of active creatures from this spawner.
	 *
	 * @param name
	 *   Creature name.
	 * @return
	 *   Active creatures.
	 */
	private int getActiveCount(final String name) {
		if (containsKey(name)) {
			return get(name).getThird();
		}
		return 0;
	}

	/**
	 * Retrieves the total number of active creatures that can be spawned at the same time.
	 *
	 * @return
	 *   Max total active creatures.
	 */
	public int maxTotal() {
		int total = 0;
		for (final Triple<Integer, Integer, Integer> tr: values()) {
			total += tr.getSecond();
		}
		return total;
	}

	/**
	 * Adds a new creature instance to zone at a random position.
	 *
	 * @param name
	 *   Creature name.
	 */
	private boolean spawn(final String name) {
		final DefaultCreature defaultCreature = SingletonRepository.getEntityManager().getDefaultCreature(name);
		if (defaultCreature == null) {
			logger.error("Default creature \"" + name + "\" not found");
			return false;
		}
		final Creature creature = defaultCreature.getCreatureRandomizeStats();
		try {
			// find a suitable location on map for spawning
			final Point pos = zone.getRandomSpawnPosition(creature);
			if (pos == null) {
				throw new Exception("No suitable position for dynamic spawning available in zone " + zone.getName());
			}
			// NOTE: behavior copied from `CreatureRespawnPoint`, not sure what it does
			creature.registerObjectsForNotification(observers);
			if (StendhalRPAction.placeat(zone, creature, pos.x, pos.y)) {
				onSpawned(creature);
				return true;
			} else {
				// could not place the creature anywhere so treat it like it died
				onRemoved(creature);
				logger.warn("Could not spawn " + creature.getName() + " near " + zone.getName() + " "
						+ pos.x + " " + pos.y);
			}
		} catch (final Exception e) {
			logger.error("Error spawning entity " + creature, e);
		}
		return false;
	}

	/**
	 * Adds creature to spawn queue.
	 *
	 * @param name
	 *   Creature name.
	 */
	private void queue(final String name) {
		if (getActiveCount(name) >= getActiveMax(name)) {
			// maximum number of creatures is active so don't spawn more
			return;
		}
		final int nextTurn = MathHelper.clamp(Rand.randExponential(getRespawnTime(name)), MIN_RESPAWN_TIME, MAX_RESPAWN_TIME);
		queued.add(new Pair<>(nextTurn, name));
	}

	/**
	 * Removes next entry from the queue.
	 */
	private void unqueue() {
		final int idx = queued.indexOf(getNextQueued(false));
		if (idx > -1) {
			queued.remove(idx);
		}
	}

	/**
	 * Gets the next entry in the queue.
	 *
	 * @param copy
	 *   If {@code true} make a copy before returning.
	 * @return
	 *   Attributes of next entry.
	 */
	private Pair<Integer, String> getNextQueued(final boolean copy) {
		Pair<Integer, String> next = null;
		// NOTE: might be more efficient to get first value of sorted list
		for (final Pair<Integer, String> p: queued) {
			final int t = p.first();
			if (next == null || t < next.first()) {
				next = p;
			}
		}
		if (copy && next != null) {
			return new Pair<>(next.first(), next.second());
		}
		return next;
	}

	/**
	 * Increments the known number of active creature instances.
	 *
	 * @param name
	 *   Creature name.
	 * @param amount
	 *   Number to adjust by (usually 1 or -1).
	 */
	private void incActive(final String name, final short amount) {
		if (!containsKey(name)) {
			logger.warn("Creature \"" + name + "\" not registered");
			return;
		}
		final Triple<Integer, Integer, Integer> oldValue = get(name);
		put(name, new Triple<Integer, Integer, Integer>(oldValue.getFirst(), oldValue.getSecond(), oldValue.getThird() + amount));
	}

	@Override
	public void onSpawned(final Creature spawned) {
		spawned.init();
		spawned.setSpawner(this);
		final String name = spawned.getName();
		incActive(name, (short) 1);
		// spawning succeeded so reset active spawning property
		spawnsAt = -1;
		// add another to queue
		queue(name);
		// don't restart notifier here as it will be done in {@code TurnListener.onTurnReached}
	}

	@Override
	public void onRemoved(final Creature removed) {
		final String name = removed.getName();
		incActive(name, (short) -1);
		// add another to queue
		queue(name);
		// restart notifier if needed
		startNotifier();
	}

	/**
	 * Starts turn notifier for next queued entry.
	 */
	private void startNotifier() {
		final Pair<Integer, String> next = getNextQueued(false);
		if (next == null) {
			return;
		}
		final int turns = next.first();
		if (spawnsAt > -1 && turns >= spawnsAt) {
			// don't override earlier spawning
			return;
		}
		spawnsAt = turns;
		SingletonRepository.getTurnNotifier().notifyInTurns(turns, this);
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		final Pair<Integer, String> next = getNextQueued(true);
		if (next != null) {
			final String nextName = next.second();
			/* this check doesn't work because currentTurn is total turns since server started &
			 * next.first() is turns since time of queue
			if (next.first() != currentTurn) {
				logger.warn("Creature \"" + nextName + "\" scheduled to spawn at turn " + nextTurn + " but spawning at " + currentTurn);
			}
			*/
			// remove from queue
			unqueue();
			spawn(nextName);
		} else {
			logger.warn("Turn reached but nothing queued for spawn");
		}

		// restart notifier if another entry is in queue
		startNotifier();
		if (spawnsAt > -1 && queued.size() > 0) {
			logger.warn("Queue not empty but not scheduled for spawn");
		}
	}

	@Override
	public void addObserver(final Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(final Observer observer) {
		observers.remove(observer);
	}
}
