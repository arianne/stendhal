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
package games.stendhal.server;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * RespawnPoints are points at which creatures can appear. Several creatures
 * can be spawned, until a maximum has been reached (note that this maximum
 * is usually 1); then the RespawnPoint will stop spawining creatures until 
 * at least one of the creatures has died. It will then continue to spawn
 * creatures. A certain time must pass between respawning creatures; this
 * respawn time is usually dependent of the type of the creatures that are
 * spawned.
 * 
 * Each respawn point can only spawn one type of creature. The Prototype
 * design pattern is used; the <i>prototypeCreature</i> will be copied
 * to create new creatures.  
 */
public class RespawnPoint implements TurnListener {
	private static final int TURNSTORESPAWN = 90;

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(RespawnPoint.class);
	
	private StendhalRPZone zone;
	
	private int x;
	
	private int y;
	
	/**
	 * The number of creatures spawned here that can exist at
	 *  the same time
	 */
	private int maximum;
	
	/** 
	 * This is the prototype; it will be copied to create new creatures
	 * that will be spawned here.
	 */
	private Creature prototypeCreature;
	
	/** All creatures that were spawned here and that are still alive*/
	private List<Creature> creatures;
	
	/**
	 * Stores if this respawn point is currently waiting for a creature
	 * to respawn
	 */
	private boolean respawning;
	
	/**
	 * How long it takes to respawn a creature. This defaults to the
	 * creature's default respawn time. 
	 */
	private int respawnTime;

	/**
	 * Creates a new RespawnPoint.
	 * @param zone
	 * @param x
	 * @param y
	 * @param creature The prototype creature 
	 * @param maximum The number of creatures spawned here that can exist at
	 *                the same time
	 */
	public RespawnPoint(StendhalRPZone zone, int x, int y, Creature creature, int maximum) {
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.prototypeCreature = creature;
		this.maximum = maximum;

		this.respawnTime = creature.getRespawnTime();
		this.creatures = new LinkedList<Creature>();

		respawning = true;
		TurnNotifier.get().notifyInTurns(0, this, null); // respawn in next turn
		respawnTime = TURNSTORESPAWN;
	}

	/**
	 * Sets the time it takes to respawn a creature. Note that this value 
	 * defaults to the creature's default respawn time. 
	 */
	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}
	
	/**
	 * Notifies this respawn point about the death of a creature that
	 * was spawned here.
	 * @param dead The creature that has died
	 */
	public void notifyDead(Creature dead) {
		Log4J.startMethod(logger, "notifyDead");

		if (!respawning) {
			// start respawning a new creature
			respawning = true;
			TurnNotifier.get().notifyInTurns(Rand.rand(respawnTime, respawnTime / 30), this, null);
		}

		creatures.remove(dead);
		Log4J.finishMethod(logger, "notifyDead");
	}

	
	/**
	 * Is called when a new creature is ready to pop up. 
	 * @see games.stendhal.server.events.TurnListener#onTurnReached(int, java.lang.String)
	 */
	public void onTurnReached(int currentTurn, String message) {
		respawn();

		// Is this all or should we spawn more creatures?
		if (creatures.size() == maximum) {
			respawning = false;
		} else {
			// TODO: consider increasing the variance to increase randomization
			TurnNotifier.get().notifyInTurns(Rand.rand(respawnTime, respawnTime / 30), this, null);
		}
	}

	/**
	 * Checks how many creatures which were spawned here are currently alive.
	 * @return
	 */
	public int size() {
		return creatures.size();
	}

	/**
	 * Pops up a new creature.
	 */
	private void respawn() {
		Log4J.startMethod(logger, "respawn");
		try {
			// clone the prototype creature
			Creature newentity = prototypeCreature.getInstance();

			// A bit of randomization to make Joan and Snaketails a bit happier.
			// :)
			newentity.setATK(Rand.rand(newentity.getATK(),
					newentity.getATK() / 10));
			newentity.setDEF(Rand.rand(newentity.getDEF(),
					newentity.getDEF() / 10));

			zone.assignRPObjectID(newentity);
			StendhalRPAction.placeat(zone, newentity, x, y);

			newentity.setRespawnPoint(this);
			creatures.add(newentity);

			zone.add(newentity);
			newentity.init();
		} catch (Exception e) {
			logger.error("error respawning entity " + prototypeCreature, e);
		} finally {
			Log4J.finishMethod(logger, "respawn");
		}
	}

    public void logic() {
		for (Creature creature : creatures) {
			creature.logic();
		}
	}
}
