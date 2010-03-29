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

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.KillNotificationCreature;

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

import org.apache.log4j.Logger;

public class KillNotificationCreatureRespawnPoint extends CreatureRespawnPoint {
	/** longest possible respawn time in turns. half a year - should be longer than the 
	 * server is up in one phase */
	private static final int MAX_RESPAWN_TIME = 200 * 60 * 24 * 30 * 6;
	/** minimum respawn time in turns. about 10s */
	private static final int MIN_RESPAWN_TIME = 33;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(CreatureRespawnPoint.class);

	private final StendhalRPZone zone;

	private final int x;

	private final int y;

	/**
	 * The number of creatures spawned here that can exist at the same time.
	 */
	private final int maximum;

	/**
	 * This is the prototype; it will be copied to create new creatures that
	 * will be spawned here.
	 */
	private final KillNotificationCreature prototypeCreature;

	/** All creatures that were spawned here and that are still alive. */
	private final List<KillNotificationCreature> creatures;

	/*
	 * Stores if this respawn point is currently waiting for a creature to
	 * respawn.
	 */
	//private boolean respawning;

	/**
	 * How long it takes to respawn a creature. This defaults to the creature's
	 * default respawn time. It is in turns.
	 */
	private int respawnTime;
	
	private Observer observer;

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
	public KillNotificationCreatureRespawnPoint(final StendhalRPZone zone, final int x, final int y,
			final KillNotificationCreature creature, final int maximum, final Observer observer) {
		super(zone, x, y, creature, maximum);
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.prototypeCreature = creature;
		this.maximum = maximum;
		this.observer = observer;
		this.respawnTime = creature.getRespawnTime();
		this.creatures = new LinkedList<KillNotificationCreature>();

		respawning = true;
		
		// don't respawn in next turn!
		SingletonRepository.getTurnNotifier().notifyInTurns(calculateNextRespawnTurn(), this); 
	
	}

	/**
	 * Calculates a randomized respawn time.
	 * @return the amount of turns calculated
	 */
	private int calculateNextRespawnTurn() {
		final int time = Rand.randExponential(respawnTime);
		
		// limit between MAX_ and MIN_
		return Math.max(Math.min(time, MAX_RESPAWN_TIME), MIN_RESPAWN_TIME);
	}
	
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
	 * Checks how many creatures which were spawned here are currently alive.
	 * 
	 * @return amount of living creatures
	 */
	public int size() {
		return creatures.size();
	}

	/**
	 * Pops up a new creature.
	 */
	private void respawn() {

		try {
			// clone the prototype creature
			final KillNotificationCreature newentity = prototypeCreature.getNewInstance();

			// A bit of randomization to make Joan and Snaketails a bit happier.
			// :)
			newentity.setATK(Rand.randGaussian(newentity.getATK(),
					newentity.getATK() / 10));
			newentity.setDEF(Rand.randGaussian(newentity.getDEF(),
					newentity.getDEF() / 10));
			newentity.registerObjectsForNotification(observer);
			
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
}
