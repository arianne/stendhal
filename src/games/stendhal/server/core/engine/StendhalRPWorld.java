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
import games.stendhal.server.core.rule.defaultruleset.DefaultRuleManager;
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
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.portal.Door;
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
import games.stendhal.tools.tiled.LayerDefinition;
import games.stendhal.tools.tiled.ServerTMXLoader;
import games.stendhal.tools.tiled.StendhalMapStructure;

import java.net.URI;

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

	/** The rule system manager. */
	private DefaultRuleManager ruleManager;

	protected StendhalRPWorld() {
		super();

		createRPClasses();

		ruleManager = SingletonRepository.getRuleSet();
		instance = this;
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
	public int getTurnsInSeconds(int seconds) {
		return seconds * 1000 / MILLISECONDS_PER_TURN;
	}

	// /**
	// * Returns the pathfinder. The return value is undefined until onInit() is
	// * called.
	// *
	// * @return the pathfinder
	// */
	// public PathfinderThread getPathfinder() {
	// return pathfinderThread;
	// }
	//
	// /**
	// * checks if the pathfinder thread is still alive. If it is not, it is
	// * restarted.
	// */
	// public void checkPathfinder() {
	// if ((pathfinderThread == null) || !pathfinderThread.isAlive()) {
	// logger.error("Pathfinderthread died");
	// pathfinderThread = new PathfinderThread(this);
	// pathfinderThread.start();
	// }
	// }

	/** returns the current rulemanager. */
	public DefaultRuleManager getRuleManager() {
		return ruleManager;
	}

	protected void createRPClasses() {
		/*
		 * TODO: Refactor Do as Chadf proposed so the classes self initialize.
		 * This method is prone to be forgotten on addition of new classes.
		 */
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
		GoldSource.generateRPClass();
		WellSource.generateRPClass();
		Item.generateRPClass();
		PassiveEntityRespawnPoint.generateRPClass();
		Portal.generateRPClass();
		Sign.generateRPClass();
		Spell.generateRPClass();

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

		/*
		 * TODO: Refactor Create RPClasses for actions
		 */

		// Chat action class
		RPClass chatAction = new RPClass("chat");
		chatAction.add(DefinitionClass.ATTRIBUTE, "type", Type.STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "text", Type.LONG_STRING);

		// Tell action class
		chatAction = new RPClass("tell");
		chatAction.add(DefinitionClass.ATTRIBUTE, "type", Type.STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "text", Type.LONG_STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "target", Type.LONG_STRING);
	}

	@Override
	public void onInit() {
		try {
			super.onInit();

			WordList.attachDatabase();

			// create the pathfinder thread and start it
			// pathfinderThread = new PathfinderThread(this);
			// pathfinderThread.start();

			ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(new URI(
					"/data/conf/zones.xml"));

			loader.load();

			/*
			 * TODO: Refactor Extract to new method.
			 */
			/**
			 * After all the zones has been loaded, check how many portals are
			 * unpaired
			 */
			for (IRPZone zone : this) {
				for (Portal portal : ((StendhalRPZone) zone).getPortals()) {
					validatePortal(portal);
				}
			}

			// TODO: make sure this is the proper place for this + way to do
			// this
			// make sure that it is always initialized on server startup so that
			// its LoginListener does not miss anyone.
			SingletonRepository.getGagManager();
		} catch (Exception e) {
			logger.error("Error on Init the server.", e);
		}
	}

	@Override
	public void onFinish() {
		super.onFinish();
		SingletonRepository.getRuleProcessor().addGameEvent("server system", "shutdown");
	}

	protected void validatePortal(Portal portal) {
		if (!portal.loaded()) {
			logger.warn(portal + " has no destination");
			return;
		}

		if (portal instanceof OneWayPortalDestination) {
			return;
		}

		String id = portal.getDestinationZone();

		if (id == null) {
			logger.warn(portal + " has no destination zone");
			return;
		}

		StendhalRPZone zone = getZone(id);

		if (zone == null) {
			logger.warn(portal + " has an invalid destination zone: " + id);
			return;
		}

		Object ref = portal.getDestinationReference();

		if (ref == null) {
			logger.warn(portal + " has no destination reference");
			return;
		}

		if (zone.getPortal(ref) == null) {
			logger.warn(portal + " has an invalid destination reference: " + id
					+ "[" + ref + "]");
		}
	}

	public IRPZone getRPZone(String zone) {
		return getRPZone(new IRPZone.ID(zone));
	}

	/**
	 * Add zone area.
	 * 
	 * Pathfinding code still uses this, but should use it's own XML file for
	 * testing.
	 * 
	 * @throws Exception
	 */
	@Deprecated
	public StendhalRPZone addArea(String name, String content) throws Exception {
		logger.info("Loading area: " + name);
		StendhalRPZone area = new StendhalRPZone(name);

		StendhalMapStructure zonedata = null;

		zonedata = ServerTMXLoader.load(StendhalRPWorld.MAPS_FOLDER + content);

		area.addTilesets(name + ".tilesets", zonedata.getTilesets());
		area.addLayer(name + ".0_floor", zonedata.getLayer("0_floor"));
		area.addLayer(name + ".1_terrain", zonedata.getLayer("1_terrain"));
		area.addLayer(name + ".2_object", zonedata.getLayer("2_object"));
		area.addLayer(name + ".3_roof", zonedata.getLayer("3_roof"));

		LayerDefinition layer = zonedata.getLayer("4_roof_add");

		if (layer != null) {
			area.addLayer(name + ".4_roof_add", layer);
		}

		area.addCollisionLayer(name + ".collision",
				zonedata.getLayer("collision"));
		area.addProtectionLayer(name + ".protection",
				zonedata.getLayer("protection"));

		/*
		 * NOTE: This is only used for int_house_000 now, so assume int
		 */
		area.setPosition();

		addRPZone(area);
		area.populate(zonedata.getLayer("objects"));

		return area;
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
	public StendhalRPZone getZoneAt(int level, int wx, int wy, Entity entity) {
		for (IRPZone izone : this) {
			StendhalRPZone zone = (StendhalRPZone) izone;

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
