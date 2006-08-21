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

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class RespawnPoint implements TurnListener {
	private static final int TURNSTORESPAWN = 90;

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(RespawnPoint.class);
	private StendhalRPZone zone;
	private int x;
	private int y;
	private int maximum;
	private Creature entity;
	private List<Creature> entities;
	private boolean respawning;
	private int respawnTime;

	public RespawnPoint(int x, int y, int radius) {
		this.x = x;
		this.y = y;
		maximum = 0;

		respawning = true;
		TurnNotifier.get().notifyInTurns(0, this, null); // respawn in next turn
		respawnTime = TURNSTORESPAWN;
	}

	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public void set(StendhalRPZone zone, Creature entity, int maximum) {
		this.entity = entity;
		this.entities = new LinkedList<Creature>();
		this.maximum = maximum;
		this.zone = zone;
	}

	public void notifyDead(Creature dead) {
		Log4J.startMethod(logger, "notifyDead");

		if (!respawning) {
			respawning = true;
			TurnNotifier.get().notifyInTurns(Rand.rand(respawnTime, respawnTime / 30), this, null);
		}

		entities.remove(dead);
		Log4J.finishMethod(logger, "notifyDead");
	}

	public void onTurnReached(int currentTurn, String message) {
		respawn();

		// Is this all or should we spawn more creatures?
		if (entities.size() == maximum) {
			respawning = false;
		} else {
			TurnNotifier.get().notifyInTurns(Rand.rand(respawnTime, respawnTime / 30), this, null);
		}
	}

	public int size() {
		return entities.size();
	}

	private void respawn() {
		Log4J.startMethod(logger, "respawn");
		try {
			// Creature newentity = entity.getClass().newInstance();
			// String clazz = entity.get("name");
			// Creature newentity = new
			// Creature(entity);//zone.getWorld().getRuleManager().getEntityManager().getCreature(clazz);
			Creature newentity = entity.getInstance();

			// A bit of randomization to make Joan and Snaketails a bit happier.
			// :)
			newentity.setATK(Rand.rand(newentity.getATK(),
					newentity.getATK() / 10));
			newentity.setDEF(Rand.rand(newentity.getDEF(),
					newentity.getDEF() / 10));

			zone.assignRPObjectID(newentity);
			StendhalRPAction.placeat(zone, newentity, x, y);

			newentity.setRespawnPoint(this);
			entities.add(newentity);

			zone.add(newentity);
			newentity.init();
		} catch (Exception e) {
			logger.error("error respawning entity " + entity, e);
		} finally {
			Log4J.finishMethod(logger, "respawn");
		}
	}

    public void logic() {
        //Log4J.startMethod(logger, "logic");

		for (Creature creature : entities) {
			creature.logic();
		}
        
		//Log4J.finishMethod(logger, "logic");
	}
}
