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

import games.stendhal.server.config.ZoneGroupsXMLLoader;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Fire;
import games.stendhal.server.entity.FishSource;
import games.stendhal.server.entity.GoldSource;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Door;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.entity.spawner.GrowingPassiveEntityRespawnPoint;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.spawner.SheepFood;
import games.stendhal.server.entity.spell.Spell;
import games.stendhal.server.pathfinder.PathfinderThread;
import games.stendhal.server.rule.RuleManager;
import games.stendhal.server.rule.RuleSetFactory;
import games.stendhal.tools.tiled.LayerDefinition;
import games.stendhal.tools.tiled.ServerTMXLoader;
import games.stendhal.tools.tiled.StendhalMapStructure;

import java.net.URI;

import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPClass;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class StendhalRPWorld extends RPWorld {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalRPWorld.class);

	static {
		MAPS_FOLDER = "data/maps/";
		
		if (StendhalRPWorld.class.getClassLoader().getResource("tiled/tileset/README") != null) {
			logger.warn("Developing mode, loading maps from tiled/ instead of data/maps");
			MAPS_FOLDER = "tiled/";
		}
	}
	
	public static String MAPS_FOLDER;

	/**
	 * A common place for milliseconds per turn.
	 */
	public static final int MILLISECONDS_PER_TURN = 300;

	/** The Singleton instance */
	protected static StendhalRPWorld instance;

	/** The pathfinder thread. */
	private PathfinderThread pathfinderThread;

	/** The rule system manager */
	private RuleManager ruleManager;

	protected StendhalRPWorld() {
		super();

		Log4J.startMethod(logger, "StendhalRPWorld");
		createRPClasses();

		// init language support
//		String language = "en";
//		try {
//			language = Configuration.getConfiguration().get("language");
//		} catch (Exception e) {
//			// ignore
//		}
		//Translate.initLanguage(language);

		ruleManager = RuleSetFactory.getRuleSet("default");
		Log4J.finishMethod(logger, "StendhalRPWorld");
		instance = this;
	}

	public static StendhalRPWorld get() {
		if (instance == null) {
			instance = new StendhalRPWorld();
		}
		return instance;
	}

	/**
	 * This method is a workaround for a groovy bug:
	 * http://jira.codehaus.org/browse/GROOVY-1484
	 * 
	 * Don't use it in Java code, only in Groovy.
	 * Remove this method once the Groovy bug has been resolved.
	 *
	 * @return StendhalRPWorld
	 * @deprecated use StendhalRPWorld.get()
	 */
	@Deprecated
	public static StendhalRPWorld getInstance() {
		return get();
	}

	/**
	 * Gives the number of turns that will take place during a given number
	 * of seconds.
	 *
	 * @param seconds The number of seconds.
	 *
	 * @return The number of turns.
	 */
	public int getTurnsInSeconds(int seconds) {
		return seconds * 1000 / MILLISECONDS_PER_TURN;
	}

	/**
	 * Returns the pathfinder. The return value is undefined until onInit() is
	 * called.
	 * 
	 * @return the pathfinder
	 */
	public PathfinderThread getPathfinder() {
		return pathfinderThread;
	}

	/**
	 * checks if the pathfinder thread is still alive. If it is not, it is
	 * restarted.
	 */
	public void checkPathfinder() {
		if ((pathfinderThread == null) || !pathfinderThread.isAlive()) {
			logger.error("Pathfinderthread died");
			pathfinderThread = new PathfinderThread(this);
			pathfinderThread.start();
		}
	}

	/** returns the current rulemanager. */
	public RuleManager getRuleManager() {
		return ruleManager;
	}

	private void createRPClasses() {
		Log4J.startMethod(logger, "createRPClasses");

		Entity.generateRPClass();

		// Entity sub-classes
		Blood.generateRPClass();
		Chest.generateRPClass();
		Corpse.generateRPClass();
		Door.generateRPClass();
		Fire.generateRPClass();
		FishSource.generateRPClass();
		GoldSource.generateRPClass();
		Item.generateRPClass();
		PassiveEntityRespawnPoint.generateRPClass();
		Portal.generateRPClass();
		RPEntity.generateRPClass();
		Sign.generateRPClass();
		Spell.generateRPClass();

		// RPEntity sub-classes
		NPC.generateRPClass();
		Player.generateRPClass();

		// NPC sub-classes
		Creature.generateRPClass();

		// Creature sub-classes
		Sheep.generateRPClass();

		// PassiveEntityRespawnPoint sub-class
		GrowingPassiveEntityRespawnPoint.generateRPClass();
		SheepFood.generateRPClass();


		// Chat action class
		RPClass chatAction = new RPClass("chat");
		chatAction.add("text", RPClass.LONG_STRING);

		// Tell action class
		chatAction = new RPClass("tell");
		chatAction.add("text", RPClass.LONG_STRING);
		chatAction.add("target", RPClass.STRING);

		Log4J.finishMethod(logger, "createRPClasses");
	}

	@Override
	public void onInit() throws Exception {
		// create the pathfinder thread and start it
		pathfinderThread = new PathfinderThread(this);
		pathfinderThread.start();

		ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(new URI("/data/conf/zones.xml"));

		loader.load();

		/**
		 * After all the zones has been loaded, check how many portals are
		 * unpaired
		 */
		for (IRPZone zone : this) {
			for (Portal portal : ((StendhalRPZone) zone).getPortals()) {
				validatePortal(portal);
			}
		}
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

		StendhalRPZone zone = (StendhalRPZone) getRPZone(id);

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
			logger.warn(portal + " has an invalid destination reference: " + id + "[" + ref + "]");
		}
	}

	public IRPZone getRPZone(String zone) {
		return getRPZone(new IRPZone.ID(zone));
	}

	/**
	 * Add zone area.
	 *
	 * Pathfinding code still uses this, but should use it's own XML
	 * file for testing.
	 * @throws Exception 
	 */
	public StendhalRPZone addArea(String name, String content) throws Exception {
		logger.info("Loading area: " + name);
		StendhalRPZone area = new StendhalRPZone(name);

		StendhalMapStructure zonedata=null;

		zonedata=ServerTMXLoader.load(StendhalRPWorld.MAPS_FOLDER + content);

		area.addTilesets(name + ".tilesets", zonedata.getTilesets());
		area.addLayer(name + ".0_floor", zonedata.getLayer("0_floor"));
		area.addLayer(name + ".1_terrain", zonedata.getLayer("1_terrain"));
		area.addLayer(name + ".2_object", zonedata.getLayer("2_object"));
		area.addLayer(name + ".3_roof", zonedata.getLayer("3_roof"));

		LayerDefinition layer = zonedata.getLayer("4_roof_add");

		if (layer != null) {
			area.addLayer(name + ".4_roof_add", layer);
		}

		area.addCollisionLayer(name + ".collision", zonedata.getLayer("collision"));
		area.addProtectionLayer(name + ".protection", zonedata.getLayer("protection"));

		/*
		 * NOTE: This is only used for int_house_000 now, so assume int
		 */
		area.setPosition();

		addRPZone(area);
		area.populate(zonedata.getLayer("objects"));

		return area;
	}

	/**
	 * Creates a new house and add it to the zone. num is the unique idenfier
	 * for portals x and y are the position of the door of the house.
	 * @throws Exception 
	 */
	public void createHouse(StendhalRPZone zone, int x, int y) throws Exception {
		Portal door = new Portal();
		door.setX(x);
		door.setY(y);
		Object dest = zone.assignPortalID(door);

		String name = "int_" + zone.getID().getID() + "_house_" + dest;

		door.setDestination(name, new Integer(0));
		zone.assignRPObjectID(door);
		zone.add(door);

		StendhalRPZone house = addArea(name, "interiors/abstract/house_000.tmx");
		Portal portal = new Portal();
		portal.setDestination(zone.getID().getID(), dest);
		portal.setX(7);
		portal.setY(1);
		portal.setReference(new Integer(0));
		house.assignRPObjectID(portal);
		house.add(portal);

		/*
		 * Change to false to disable xml emit code
		 */
		if(true) {
			StringBuffer sbuf = new StringBuffer();

			sbuf.append("zones/*.xml:\n\n");
			sbuf.append(" <zone name=\"" + name + "\" file=\"interiors/abstract/house_000.tmx\">\n");
			sbuf.append("  <portal x=\"7\" y=\"1\" ref=\"entrance\">\n");
			sbuf.append("   <destination zone=\"" + zone.getID().getID() + "\" ref=\"house_" + dest + "_entrance\"/>\n");
			sbuf.append("  </portal>\n");
			sbuf.append(" </zone>\n");

			sbuf.append("\n");
			sbuf.append(" <!-- Zone: " + zone.getID().getID() + " -->\n");
			sbuf.append("  <portal x=\"" + x + "\" y=\"" + y + "\" ref=\"house_" + dest + "_entrance\">\n");
			sbuf.append("   <destination zone=\"" + name + "\" ref=\"entrance\"/>\n");
			sbuf.append("  </portal>\n");

			sbuf.append("\n\n");

			logger.info(sbuf.toString());
		}
	}

	@Override
	public void onFinish() throws Exception {
		StendhalRPRuleProcessor.get().addGameEvent("server system", "shutdown");
	}

}
