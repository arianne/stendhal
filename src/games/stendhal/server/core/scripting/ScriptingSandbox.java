/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.TransitionContext;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

public abstract class ScriptingSandbox {

	// loadedNPCs and loadedRPObject are Sets. They are implemented using
	// maps because there are no WeakSets in Java. (In current Sun
	// Java HashSet is implemented using HashMap anyway).
	private final Map<NPC, Object> loadedNPCs = new WeakHashMap<NPC, Object>();
	private final Map<RPObject, Object> loadedRPObjects = new WeakHashMap<RPObject, Object>();

	private String exceptionMessage;

	private StendhalRPZone zone;

	private final String filename;

	private static final Logger logger = Logger.getLogger(ScriptingSandbox.class);

	public ScriptingSandbox(final String filename) {
		this.filename = filename;
	}

	public StendhalRPZone getZone(final RPObject rpobject) {
		return (StendhalRPZone) SingletonRepository.getRPWorld().getRPZone(
				rpobject.getID());
	}

	/**
	 * Retrieves a zone by string ID.
	 *
	 * @param zoneName
	 * 		Name of zone to retrieve.
	 * @return
	 * 		StendhalRPZone, if exists, <code>null</code> otherwise.
	 */
	public StendhalRPZone getZone(final String zoneName) {
		return SingletonRepository.getRPWorld().getZone(zoneName);
	}

	public boolean setZone(final String name) {
		zone = SingletonRepository.getRPWorld().getZone(name);
		return (zone != null);
	}

	public boolean setZone(final StendhalRPZone zone) {
		this.zone = zone;
		return (zone != null);
	}

	public boolean playerIsInZone(final Player player, final String zoneName) {
		return player.getZone().getName().equals(zoneName);
	}

	public void add(final NPC npc) {
		if (zone != null) {
			zone.add(npc);
			loadedNPCs.put(npc, null);
			logger.debug(filename + " added NPC: " + npc);
		}
	}

	public void add(final RPObject object, final Boolean expire) {
		if (zone != null) {
			if (expire == null) {
				zone.add(object);
			} else {
				zone.add(object, expire);
			}

			loadedRPObjects.put(object, null);
			logger.debug(filename + " added object: " + object);
		}
	}

	public void add(final RPObject object) {
		add(object, null);
	}

	public Creature[] getCreatures() {
		return (SingletonRepository.getEntityManager().getCreatures().toArray(new Creature[1]));
	}

	public Creature getCreature(final String clazz) {
		return SingletonRepository.getEntityManager().getCreature(
				clazz);
	}

	public Item[] getItems() {
		return (SingletonRepository.getEntityManager().getItems().toArray(new Item[1]));
	}

	public Item getItem(final String name) {
		return SingletonRepository.getEntityManager().getItem(
				name);
	}

	public Creature add(final Creature template, final int x, final int y) {
		Creature creature = template.getNewInstance();
		if (zone != null) {
			if (StendhalRPAction.placeat(zone, creature, x, y)) {
				loadedNPCs.put(creature, null);
				logger.info(filename + " added creature: " + creature);
			} else {
				logger.info(filename + " could not add a creature: " + creature);
				creature = null;
			}
		}
		return (creature);
	}
//TODO  : inline this
	public void addGameEvent(final String source, final String event, final List<String> params) {
		new GameEvent(source, event, params.toArray(new String[params.size()])).raise();
	}

	public void modify(final RPEntity entity) {
		entity.notifyWorldAboutChanges();
	}

	public void privateText(final Player player, final String text) {
		player.sendPrivateText(text);
	}

	// ------------------------------------------------------------------------

	public abstract boolean load(Player player, List<String> args);

	public String getMessage() {
		return exceptionMessage;
	}

	protected void setMessage(final String message) {
		this.exceptionMessage = message;
	}

	public void remove(final NPC npc) {
		logger.info("Removing " + filename + " added NPC: " + npc);
		try {
			SingletonRepository.getNPCList().remove(npc.getName());

			zone = npc.getZone();
			zone.remove(npc);
			loadedNPCs.remove(npc);
		} catch (final Exception e) {
			logger.warn("Exception while removing " + filename + " added NPC: "
					+ e);
		}
	}

	public void remove(final RPObject object) {
		try {
			logger.info("Removing script added object: " + object);
			final String id = object.getID().getZoneID();
			zone = SingletonRepository.getRPWorld().getZone(id);
			zone.remove(object);
			loadedRPObjects.remove(object);
		} catch (final Exception e) {
			logger.warn("Exception while removing " + filename
					+ " added object: " + e);
		}
	}

	/**
	 * Unloads this script.
	 *
	 * @param player
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	public void unload(final Player player, final List<String> args) {
		final Set<NPC> setNPC = new HashSet<NPC>(loadedNPCs.keySet());

		for (final NPC npc : setNPC) {
			remove(npc);
		}
		for (SpeakerNPC npc : NPCList.get()) {
			npc.getEngine().remove(filename);
		}

		final Set<RPObject> setRPObject = new HashSet<RPObject>(
				loadedRPObjects.keySet());
		for (final RPObject object : setRPObject) {
			remove(object);
		}
	}

	/**
	 * Prepares execution of the script.
	 *
	 * @param player
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	protected void preExecute(final Player player, final List<String> args) {
		TransitionContext.set(filename);
	}

	/**
	 * Cleans up execution of the script.
	 *
	 * @param player
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 * @param result true, if the execution was successful; false otherwise
	 */
	protected void postExecute(final Player player, final List<String> args, boolean result) {
		TransitionContext.set(null);
	}


	/**
	 * Executes this script.
	 *
	 * @param player
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 * @return <code>true</code> at successful execution, otherwise
	 * 	<code>false</code>
	 */
	public boolean execute(final Player player, final List<String> args) {
		// do nothing
		return true;
	}
}
