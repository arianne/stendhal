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
package games.stendhal.server.core.engine;

import games.stendhal.server.core.config.ZoneGroupsXMLLoader;
import games.stendhal.server.core.rp.guilds.Guild;
import games.stendhal.server.core.rp.guilds.GuildMember;
import games.stendhal.server.core.rp.guilds.GuildPermission;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.Fire;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.area.WalkBlocker;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.portal.Door;
import games.stendhal.server.entity.mapstuff.portal.Gate;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.OneWayPortalDestination;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.mapstuff.source.FishSource;
import games.stendhal.server.entity.mapstuff.source.GoldSource;
import games.stendhal.server.entity.mapstuff.source.WellSource;
import games.stendhal.server.entity.mapstuff.spawner.GrowingPassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.parser.WordList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.Spell;
import games.stendhal.server.events.BuddyLoginEvent;
import games.stendhal.server.events.BuddyLogoutEvent;
import games.stendhal.server.events.DamagedEvent;
import games.stendhal.server.events.ExamineEvent;
import games.stendhal.server.events.HealedEvent;
import games.stendhal.server.events.PrivateTextEvent;
import games.stendhal.server.events.TextEvent;

import java.net.URI;
import java.util.Iterator;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.server.game.rp.RPWorld;

import org.apache.log4j.Logger;

public class StendhalRPWorld extends RPWorld {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPWorld.class);

	static {
		MAPS_FOLDER = "data/maps/";

		if (StendhalRPWorld.class.getClassLoader().getResource(
				"tiled/tileset/README") != null) {
			logger.warn("Developing mode, loading maps from tiled/ instead of data/maps");
			MAPS_FOLDER = "tiled/";
		}
	}

	public static String MAPS_FOLDER;

	/**
	 * A common place for milliseconds per turn.
	 */
	public static final int MILLISECONDS_PER_TURN = 300;

	/** The Singleton instance. */
	protected static StendhalRPWorld instance;

	// /** The pathfinder thread. */
	// private PathfinderThread pathfinderThread;



	protected StendhalRPWorld() {
		super();

		createRPClasses();

		instance = this;
	}

	public void removeZone(final StendhalRPZone toBeRemoved) {
		Iterator<IRPZone> it = iterator();
		while (it.hasNext()) {
			IRPZone zone = it.next();
			if (zone.getID().equals(toBeRemoved.getID())) {
				it.remove();
			}
		}

	}
	
	
	public static StendhalRPWorld get() {
		if (instance == null) {
			instance = new StendhalRPWorld();
			instance.initialize();
		}

		return instance;
	}

	/**
	 * This method is a workaround for a groovy bug:
	 * http://jira.codehaus.org/browse/GROOVY-1484
	 * 
	 * Don't use it in Java code, only in Groovy. Remove this method once the
	 * Groovy bug has been resolved.
	 * 
	 * @return StendhalRPWorld
	 * @deprecated use StendhalRPWorld.get()
	 */
	@Deprecated
	public static StendhalRPWorld getInstance() {
		return SingletonRepository.getRPWorld();
	}

	/**
	 * Gives the number of turns that will take place during a given number of
	 * seconds.
	 * 
	 * @param seconds
	 *            The number of seconds.
	 * 
	 * @return The number of turns.
	 */
	public int getTurnsInSeconds(final int seconds) {
		return seconds * 1000 / MILLISECONDS_PER_TURN;
	}

	
	protected void createRPClasses() {
		Entity.generateRPClass();

		// Entity sub-classes
		ActiveEntity.generateRPClass();
		AreaEntity.generateRPClass();
		Blood.generateRPClass();
		Chest.generateRPClass();
		Corpse.generateRPClass();
		Door.generateRPClass();
		Fire.generateRPClass();
		FishSource.generateRPClass();
		Gate.generateGateRPClass();
		GoldSource.generateRPClass();
		WellSource.generateRPClass();
		Item.generateRPClass();
		PassiveEntityRespawnPoint.generateRPClass();
		Portal.generateRPClass();
		Sign.generateRPClass();
		Spell.generateRPClass();
		WalkBlocker.generateRPClass();
		HousePortal.generateRPClass();

		// ActiveEntity sub-classes
		RPEntity.generateRPClass();

		// RPEntity sub-classes
		NPC.generateRPClass();
		Player.generateRPClass();

		// NPC sub-classes
		Creature.generateRPClass();

		// Creature sub-classes
		Sheep.generateRPClass();
		Pet.generateRPClass();
		Cat.generateRPClass();
		BabyDragon.generateRPClass();

		// PassiveEntityRespawnPoint sub-class
		GrowingPassiveEntityRespawnPoint.generateRPClass();
		SheepFood.generateRPClass();

		// zone storage
		ArrestWarrant.generateRPClass();
		RentedSign.generateRPClass();

		// rpevents
		BuddyLoginEvent.generateRPClass();
		BuddyLogoutEvent.generateRPClass();
		DamagedEvent.generateRPClass();
		ExamineEvent.generateRPClass();
		HealedEvent.generateRPClass();
		PrivateTextEvent.generateRPClass();
		TextEvent.generateRPClass();

		//guilds
		Guild.generateRPClass();
		GuildMember.generateRPClass();
		GuildPermission.generateRPClass();

		createChatActionRPClass();

		createTellActionRPClass();
	}

	private void createTellActionRPClass() {
		RPClass chatAction;
		chatAction = new RPClass("tell");
		chatAction.add(DefinitionClass.ATTRIBUTE, "type", Type.STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "text", Type.LONG_STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "target", Type.LONG_STRING);
	}

	private void createChatActionRPClass() {
		RPClass chatAction = new RPClass("chat");
		chatAction.add(DefinitionClass.ATTRIBUTE, "type", Type.STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "text", Type.LONG_STRING);
	}

	@Override
	public void onInit() {
		try {
			super.onInit();

			WordList.attachDatabase();

			// create the pathfinder thread and start it
			// pathfinderThread = new PathfinderThread(this);
			// pathfinderThread.start();

			final ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(new URI(
					"/data/conf/zones.xml"));

			loader.load();

			validatePortals();

			SingletonRepository.getGagManager();
			SingletonRepository.getJail();
		} catch (final Exception e) {
			logger.error("Error on Init the server.", e);
		}
	}

	/**
	 * Checks for unpaired portals.
	 */
	private void validatePortals() {
		
		for (final IRPZone zone : this) {
			for (final Portal portal : ((StendhalRPZone) zone).getPortals()) {
				validatePortal(portal);
			}
		}
	}

	@Override
	public void onFinish() {
		super.onFinish();
		new GameEvent("server system", "shutdown").raise();
		try {
			//TODO: find a more appropriate way to do this
			// give gameevents a chance to be processed;
			Thread.sleep(500);
		} catch (InterruptedException e) {
			//do nothing
		}
		
	}

	protected void validatePortal(final Portal portal) {
		if (!portal.loaded()) {
			logger.warn(portal + " has no destination");
			return;
		}

		if (portal instanceof OneWayPortalDestination) {
			return;
		}

		final String id = portal.getDestinationZone();

		if (id == null) {
			logger.warn(portal + " has no destination zone");
			return;
		}

		final StendhalRPZone zone = getZone(id);

		if (zone == null) {
			logger.warn(portal + " has an invalid destination zone: " + id);
			return;
		}

		final Object ref = portal.getDestinationReference();

		if (ref == null) {
			logger.warn(portal + " has no destination reference");
			return;
		}

		if (zone.getPortal(ref) == null) {
			logger.warn(portal + " has an invalid destination reference: " + id
					+ "[" + ref + "]");
		}
	}

	public IRPZone getRPZone(final String zone) {
		return getRPZone(new IRPZone.ID(zone));
	}

	/**
	 * Finds a zone by its id.
	 * 
	 * @param id
	 *            The zone's id
	 * 
	 * @return The matching zone, or <code>null</code> if not found.
	 */
	public StendhalRPZone getZone(final String id) {
		return (StendhalRPZone) getRPZone(new IRPZone.ID(id));
	}

	/**
	 * Find the zone that would contain an entity at global coordinates.
	 * 
	 * TODO: This is likely broken for entity larger than 2x2, because parts of
	 * them will exist in multiple zones (and not in collision)
	 * 
	 * @param level
	 *            The level.
	 * @param wx
	 *            The global X coordinate.
	 * @param wy
	 *            The global Y coordinate.
	 * @param entity
	 *            The entity.
	 * 
	 * @return The matching zone, or <code>null</code> if not found.
	 */
	public StendhalRPZone getZoneAt(final int level, final int wx, final int wy, final Entity entity) {
		for (final IRPZone izone : this) {
			final StendhalRPZone zone = (StendhalRPZone) izone;

			if (zone.isInterior()) {
				continue;
			}

			if (zone.getLevel() != level) {
				continue;
			}

			if (zone.intersects(entity.getArea(wx, wy))) {
				logger.debug("Contained at :" + zone.getID());
				return zone;
			}
		}

		return null;
	}
}
