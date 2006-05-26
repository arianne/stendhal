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
import java.util.LinkedList;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.server.game.RPWorld;
import org.apache.log4j.Logger;

public class RespawnPoint {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(RespawnPoint.class);

	private int x;

	private int y;

	private int maximum;

	private Creature entity;

	private List<Creature> entities;

	private boolean respawning;

	final public static int TURNSTORESPAWN = 90; // Five minute at 300ms

	private int respawnTime;

	private int turnsToRespawn;

	private StendhalRPZone zone;

	protected static StendhalRPRuleProcessor rp;

	protected static RPWorld world;

	public static void setRPContext(StendhalRPRuleProcessor rpContext,
			RPWorld worldContext) {
		rp = rpContext;
		world = worldContext;
	}

	public RespawnPoint(int x, int y, int radius) {
		this.x = x;
		this.y = y;
		maximum = 0;

		respawning = true;
		turnsToRespawn = 1; // respawn now
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
			turnsToRespawn = Rand.rand(respawnTime, respawnTime / 30);
		}

		entities.remove(dead);
		Log4J.finishMethod(logger, "notifyDead");
	}

	public void nextTurn() {
		Log4J.startMethod(logger, "nextTurn");
		if (respawning) {
			logger.debug("Turns to respawn: " + turnsToRespawn);

			if (turnsToRespawn == 0) {
				turnsToRespawn = respawnTime;

				respawn();

				if (entities.size() == maximum) {
					respawning = false;
				}
			}

			turnsToRespawn--;
		}

		for (Creature creature : entities) {
			creature.logic();
		}

		Log4J.finishMethod(logger, "nextTurn");
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
		} catch (Exception e) {
			logger.error("error respawning entity " + entity, e);
		} finally {
			Log4J.finishMethod(logger, "respawn");
		}
	}
}
