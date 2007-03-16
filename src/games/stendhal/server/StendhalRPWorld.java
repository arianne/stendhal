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

import java.net.URI;

import games.stendhal.server.config.ZoneGroupsXMLLoader;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Entity;
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
import games.stendhal.server.pathfinder.PathfinderThread;
import games.stendhal.server.rule.RuleManager;
import games.stendhal.server.rule.RuleSetFactory;
import games.stendhal.server.util.Translate;

import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPClass;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class StendhalRPWorld extends RPWorld {
	/**
	 * A common place for milliseconds per turn.
	 */
	public static final int	MILLISECONDS_PER_TURN		= 300;


	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalRPWorld.class);

	/** The Singleton instance */
	private static StendhalRPWorld instance;
	
	/** The pathfinder thread. */
	private PathfinderThread pathfinderThread;

	/** The rule system manager */
	private RuleManager ruleManager;

	private StendhalRPWorld() {
		super();

		Log4J.startMethod(logger, "StendhalRPWorld");
		createRPClasses();
		
		// init language support
		String language = "en";
		try {
			language = Configuration.getConfiguration().get("language");
		} catch (Exception e) {
			// ignore
		}
		Translate.initLanguage(language);

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
			logger.fatal("Pathfinderthread died");
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

		Sign.generateRPClass();
		Portal.generateRPClass();
		Door.generateRPClass();
		PassiveEntityRespawnPoint.generateRPClass();
		GrowingPassiveEntityRespawnPoint.generateRPClass();
		SheepFood.generateRPClass();
		Corpse.generateRPClass();
		Item.generateRPClass();
		Chest.generateRPClass();

		RPEntity.generateRPClass();

		NPC.generateRPClass();

		Creature.generateRPClass();
		Sheep.generateRPClass();

		Player.generateRPClass();

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

		ZoneGroupsXMLLoader loader =
			new ZoneGroupsXMLLoader(
				new URI("/data/conf/zones.xml"));

		loader.load(this);


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

		if(portal instanceof OneWayPortalDestination) {
			return;
		}

		String id = portal.getDestinationZone();

		if(id == null) {
			logger.warn(portal + " has no destination zone");
			return;
		}

		StendhalRPZone zone = (StendhalRPZone) getRPZone(id);

		if(zone == null) {
			logger.warn(portal + " has an invalid destination zone: " + id);
			return;
		}

		Object ref = portal.getDestinationReference();

		if(ref == null) {
			logger.warn(portal + " has no destination reference");
			return;
		}

		if(zone.getPortal(ref) == null) {
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
	 */
	public StendhalRPZone addArea(String name) throws org.xml.sax.SAXException,
			java.io.IOException {
		return addArea(name, name.replace("-", "sub_"));
	}

	public StendhalRPZone addArea(String name, String content)
			throws org.xml.sax.SAXException, java.io.IOException {
		logger.info("Loading area: " + name);
		StendhalRPZone area = new StendhalRPZone(name);

		ZoneXMLLoader instance = ZoneXMLLoader.get();
		ZoneXMLLoader.XMLZone xmlzone = instance.load("data/maps/" + content + ".xstend");

		area.addLayer(name + "_0_floor", xmlzone.getLayer("0_floor"));
		area.addLayer(name + "_1_terrain", xmlzone.getLayer("1_terrain"));
		area.addLayer(name + "_2_object", xmlzone.getLayer("2_object"));
		area.addLayer(name + "_3_roof", xmlzone.getLayer("3_roof"));

		byte[] layer = xmlzone.getLayer("4_roof_add");
		if (layer != null) {
			area.addLayer(name + "_4_roof_add", layer);
		}

		area.addCollisionLayer(name + "_collision", xmlzone.getLayer("collision"));
		area.addProtectionLayer(name + "_protection", xmlzone.getLayer("protection"));

/*
		try {
			String filename = "/home/brummermann/workspace/HEAD/stendhal/tiled/world/" + content + ".png";
			InputStream is = new FileInputStream(filename);
			byte[] mapData = new byte[(int) new File(filename).length()];
			is.read(mapData);
			area.addMap(name + "_map", mapData);
		} catch (Exception e) {
			logger.error(e, e);
		}
*/

		if (xmlzone.isInterior()) {
			area.setPosition();
		} else {
			area.setPosition(xmlzone.getLevel(), xmlzone.getX(), xmlzone.getY());
		}

		addRPZone(area);
		area.populate(xmlzone.getLayer("objects"));

		return area;
	}

	/**
	 * Creates a new house and add it to the zone. num is the unique idenfier
	 * for portals x and y are the position of the door of the house.
	 */
	public void createHouse(StendhalRPZone zone, int x, int y)
			throws org.xml.sax.SAXException, java.io.IOException {
		Portal door = new Portal();
		door.setX(x);
		door.setY(y);
		Object dest = zone.assignPortalID(door);

		String name = "int_" + zone.getID().getID() + "_house_"
				+ dest;

		door.setDestination(name, new Integer(0));
		zone.assignRPObjectID(door);
		zone.addPortal(door);

		StendhalRPZone house = addArea(name, "int_house_000");
		Portal portal = new Portal();
		portal.setDestination(zone.getID().getID(), dest);
		portal.setX(7);
		portal.setY(1);
		portal.setReference(new Integer(0));
		house.assignRPObjectID(portal);
		house.addPortal(portal);
	}

	@Override
	public void onFinish() throws Exception {
		StendhalRPRuleProcessor.get().addGameEvent("server system", "shutdown");
	}

}
