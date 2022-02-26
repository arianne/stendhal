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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.util.Observer;

/**
 * RespawnPoints are points at which creatures can appear. Several creatures can
 * be spawned, until a maximum has been reached (note that this maximum is
 * usually 1); then the RespawnPoint will stop spawning creatures until at least
 * one of the creatures has died. It will then continue to spawn creatures. A
 * certain time must pass between respawning creatures; this respawn time is
 * usually dependent of the type of the creatures that are spawned.
 *
 * Each respawn point can only spawn one type of creature. The Prototype design
 * pattern is used; the <i>prototypeCreature</i> will be copied to create new
 * creatures.
 */
public class CreatureRespawnPoint implements TurnListener {
	/** longest possible respawn time in turns. half a year - should be longer than the
	 * server is up in one phase */
	private static final int MAX_RESPAWN_TIME = 200 * 60 * 24 * 30 * 6;
	/** minimum respawn time in turns. about 10s */
	private static final int MIN_RESPAWN_TIME = 33;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(CreatureRespawnPoint.class);

	protected final StendhalRPZone zone;

	private LinkedList<Observer> observers = new LinkedList<Observer>();

	protected final int x;

	protected final int y;

	/**
	 * The number of creatures spawned here that can exist at the same time.
	 */
	private final int maximum;

	/**
	 * This is the prototype; it will be copied to create new creatures that
	 * will be spawned here.
	 */
	protected Creature prototypeCreature;

	/** All creatures that were spawned here and that are still alive. */
	protected final List<Creature> creatures;

	/**
	 * Stores if this respawn point is currently waiting for a creature to
	 * respawn.
	 */
	protected boolean respawning;

	/**
	 * How long it takes to respawn a creature. This defaults to the creature's
	 * default respawn time. It is in turns.
	 */
	private int respawnTime;

	/**
	 * Creates a new RespawnPoint.
	 *
	 * @param zone
	 * @param x
	 * @param y
	 * @param creature
	 *            The prototype creature
	 * @param maximum
	 *            The number of creatures spawned here that can exist at the
	 *            same time
	 */
	public CreatureRespawnPoint(final StendhalRPZone zone, final int x, final int y,
			final Creature creature, final int maximum) {
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.prototypeCreature = creature;
		this.maximum = maximum;

		this.respawnTime = creature.getRespawnTime();
		this.creatures = new LinkedList<Creature>();

		respawning = true;

		// don't respawn in next turn!
		SingletonRepository.getTurnNotifier().notifyInTurns(calculateNextRespawnTurn(), this);
	}

	/**
	 * Creates a new RespawnPoint.
	 *
	 * @param zone
	 * @param x
	 * @param y
	 * @param creature
	 *            The prototype creature
	 * @param maximum
	 *            The number of creatures spawned here that can exist at the
	 *            same time
	 * @param observer
	 */
	public CreatureRespawnPoint(StendhalRPZone zone, int x,
			int y, Creature creature, int maximum, final Observer observer) {
		this(zone, x, y, creature, maximum);
		this.observers.add(observer);
	}

	public Creature getPrototypeCreature() {
		return prototypeCreature;
	}

	/**
	 * Sets the time it takes to respawn a creature. Note that this value
	 * defaults to the creature's default respawn time.
	 * @param respawnTime the middled delay between spawns in turns
	 */
	public void setRespawnTime(final int respawnTime) {
		this.respawnTime = respawnTime;
	}

	/**
	 * Notifies this respawn point about the death of a creature that was
	 * spawned here.
	 *
	 * @param dead
	 *            The creature that has died
	 */
	public void notifyDead(final Creature dead) {

		if (!respawning) {
			// start respawning a new creature
			respawning = true;
			SingletonRepository.getTurnNotifier().notifyInTurns(
					calculateNextRespawnTurn(), this);
		}

		creatures.remove(dead);
	}

	/**
	 * Is called when a new creature is ready to pop up.
	 *
	 * @see games.stendhal.server.core.events.TurnListener#onTurnReached(int)
	 */
	@Override
	public void onTurnReached(final int currentTurn) {
		respawn();

		// Is this all or should we spawn more creatures?
		if (creatures.size() == maximum) {
			respawning = false;
		} else {
			SingletonRepository.getTurnNotifier().notifyInTurns(
					calculateNextRespawnTurn(), this);
		}
	}

	/**
	 * Calculates a randomized respawn time.
	 * @return the amount of turns calculated
	 */
	protected int calculateNextRespawnTurn() {
		return MathHelper.clamp(Rand.randExponential(respawnTime), MIN_RESPAWN_TIME, MAX_RESPAWN_TIME);
	}

	/**
	 * Checks how many creatures which were spawned here are currently alive.
	 *
	 * @return amount of living creatures
	 */
	public int size() {
		return creatures.size();
	}

	/**
	 * function returns X coord of this respawn point
	 * @return - x coord
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * function returns Y coord of this respawn point
	 * @return - y coord
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Set the prototype creature for the spawner.
	 *
	 * @param creature prototype creature
	 */
    public void setPrototypeCreature(final Creature creature) {
    	this.prototypeCreature = creature;
    }

	/**
	 * add observer to observers list
	 * @param observer - observer to add
	 */
	public void addObserver(final Observer observer) {
		observers.add(observer);
	}

	/**
	 * remove observer from list
	 * @param observer - observer to remove
	 */
	public void removeObserver(final Observer observer) {
		observers.remove(observer);
	}

	/**
	 * return zone where respawn point placed
	 * @return - zone where respawn point placed
	 */
	public StendhalRPZone getZone() {
		return this.zone;
	}

	/**
	 * Pops up a new creature.
	 */
	protected void respawn() {

		try {
			// clone the prototype creature
			final Creature newentity = prototypeCreature.getNewInstance();

			// A bit of randomization to make Joan and Snaketails a bit happier.
			// :)
			newentity.setAtk(Rand.randGaussian(newentity.getAtk(),
					newentity.getAtk() / 10));
			newentity.setDef(Rand.randGaussian(newentity.getDef(),
					newentity.getDef() / 10));

			newentity.registerObjectsForNotification(observers);

			if (StendhalRPAction.placeat(zone, newentity, x, y)) {
				newentity.init();
				newentity.setRespawnPoint(this);

				creatures.add(newentity);
			} else {
				// Could not place the creature anywhere.
				// Treat it like it just had died.
				notifyDead(newentity);
				logger.warn("Could not respawn " + newentity.getName() + " near "
						+ zone.getName() + " " + x + " " + y);
			}
		} catch (final Exception e) {
			logger.error("error respawning entity " + prototypeCreature, e);
		}
	}

	/**
	 * Pops up a new creature.
	 */
	public void spawnNow() {
		if (creatures.size() < maximum) {
			SingletonRepository.getTurnNotifier().dontNotify(this);
			//SingletonRepository.getTurnNotifier().notifyInTurns(1, this);
			onTurnReached(0);
		}
	}
}
