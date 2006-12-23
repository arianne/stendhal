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

import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.portal.Door;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.PathfinderThread;
import games.stendhal.server.rule.RuleManager;
import games.stendhal.server.rule.RuleSetFactory;
import games.stendhal.server.maps.IContent;
import marauroa.common.Log4J;
import marauroa.common.game.RPClass;
import marauroa.common.game.IRPZone;
import marauroa.server.game.RPWorld;
import org.apache.log4j.Logger;

public class StendhalRPWorld extends RPWorld {
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
	 * @return
	 */
	public static StendhalRPWorld getInstance() {
		return get();
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
		if (pathfinderThread == null || !pathfinderThread.isAlive()) {
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
		Blackboard.generateRPClass();
		Portal.generateRPClass();
		Door.generateRPClass();
		PlantGrower.generateRPClass();
		GrainField.generateRPClass();
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

		// Load zones. Written from left to right and from top to bottom.
		// Please respect it!

		// Ground level
		addArea("0_semos_mountain_n_w4");
		addArea("0_semos_mountain_n2_w2");
		addArea("0_semos_mountain_n2_w");
		addArea("0_semos_mountain_n2");
		addArea("0_semos_mountain_n2_e");
		addArea("0_semos_mountain_n2_e2");
		addArea("0_ados_mountain_n2_w2");

		addArea("0_semos_mountain_n_w3");
		addArea("0_semos_mountain_n_w2");
		addArea("0_semos_plains_n");
		addArea("0_semos_plains_ne");
		addArea("0_semos_mountain_n_e2");
		addArea("0_ados_mountain_n_w2");
		addArea("0_ados_mountain_nw");
		addArea("int_ados_magician_house");
		addArea("0_ados_outside_nw");
		addArea("0_ados_wall_n");

		addArea("0_orril_mountain_n2_w2");
		addArea("0_semos_mountain_w2");
		addArea("0_semos_plains_w");
		addArea("0_semos_village_w");
		addArea("0_semos_city");
		{
			addArea("int_semos_tavern_0");
			addArea("int_semos_tavern_1");
			addArea("int_semos_temple");
			addArea("int_semos_blacksmith");
			addArea("int_semos_library");
			addArea("int_semos_storage_0");
			addArea("int_semos_storage_-1");
			addArea("int_semos_bank");
			addArea("int_semos_townhall");
			addArea("int_semos_bakery");
		}
		addArea("0_semos_road_e");
		addArea("0_semos_road_se");
		addArea("0_semos_plains_s");
		addArea("0_ados_forest_w2");
		addArea("0_ados_rock_w");
		addArea("0_ados_rock");
		addArea("0_ados_outside_w");
		// addArea("0_ados_wall");
		// addArea("0_ados_city_w");
		{
			addArea("int_ados_tavern_0");
			addArea("int_ados_temple");
			addArea("int_ados_bank");
			addArea("int_ados_bakery");
		}

		addArea("0_ados_city");
		addArea("int_ados_reverse_arrow");

		addArea("0_orril_mountain_nw");
		addArea("0_orril_forest_n");
		addArea("0_semos_forest_s");
		addArea("0_nalwor_forest_nw");
		addArea("0_nalwor_forest_n");
		addArea("0_nalwor_forest_ne");
		addArea("0_ados_swamp");

		addArea("0_orril_mountain_w2");
		addArea("0_orril_mountain_w");
		addArea("0_orril_castle");
		addArea("int_orril_castle_0");
		addArea("0_orril_forest_e");
		addArea("0_nalwor_forest_w");
		addArea("0_nalwor_city");

		addArea("0_orril_river_s_w2");
		addArea("0_orril_river_sw");
		addArea("0_orril_river_s");
		addArea("int_orril_jynath_house");
		addArea("0_orril_river_se");
		addArea("0_nalwor_river_sw");
		addArea("0_nalwor_river_s");
		//addArea("0_nalwor_river_se");
		//addArea("0_ados_river_s2_w2");
		addArea("0_athos_ship_w2");


		// Level -1
		addArea("-1_ados_outside_nw");
		addArea("-1_semos_mine_n2_w2");
		addArea("-1_semos_mine_n2");
		addArea("-1_semos_mine_nw");
		addArea("-1_semos_graveyard");
		addArea("-1_semos_dungeon");
		addArea("-1_semos_jail");
		addArea("-1_orril_mountain_w");
		addArea("-1_orril_dungeon");
		addArea("-1_orril_castle_w");
		addArea("-1_orril_castle");

		// Level -2
		addArea("-2_ados_outside_nw");
		addArea("-2_semos_dungeon");
		addArea("-2_semos_jail");		
		addArea("-2_kotoch_entrance");
		addArea("-2_orril_dwarf_mine");
		addArea("-2_orril_lich_palace");
		addArea("-2_orril_dungeon");

		// Level -3
		addArea("-3_ados_outside_nw");
		addArea("-3_semos_dungeon");
		addArea("-3_orril_dungeon");

		// Level -4
		addArea("-4_semos_dungeon");

		// Level -5
		addArea("-5_kanmararn_entrance");

		// Level -6
		addArea("-6_kanmararn_city");

		// Level -7
		// addArea("-7_kanmararn_jail");

		// Level 1
		addArea("1_kikareukin_cave");

		// Level 2
		addArea("2_kikareukin_cave");

		// Level 3
		addArea("3_kikareukin_cave");

		// Level 4
		addArea("4_kikareukin_cave");

		// Level 5
		addArea("5_kikareukin_cave");

		// Level 6
		addArea("6_kikareukin_islands");

		// Interiors
		addArea("int_afterlife");
		addArea("int_admin_playground");

		addArea("int_pathfinding");

		populateZone("Afterlife");
		populateZone("Semos");
		populateZone("Kanmararn");
		populateZone("Nalwor");
		populateZone("Orril");
		populateZone("Ados");
		populateZone("Athos");
		populateZone("FeaturesTestArea");


		/**
		 * After all the zones has been loaded, check how many portals are
		 * unpaired
		 */
		for (IRPZone zone : this) {
			for (Portal portal : ((StendhalRPZone) zone).getPortals()) {
				if (!portal.loaded()) {
					logger.warn(portal + " has no destination");
				}
			}
		}
	}

	public IRPZone getRPZone(String zone) {
		return getRPZone(new IRPZone.ID(zone));
	}

	private boolean populateZone(String name) {
		try {
			Class entityClass = Class.forName("games.stendhal.server.maps."	+ name);

			boolean implementsIContent = false;

			Class[] interfaces = entityClass.getInterfaces();
			for (Class interf : interfaces) {
				if (interf.equals(IContent.class)) {
					implementsIContent = true;
					break;
				}
			}

			if (implementsIContent == false) {
				logger.debug("Class don't implement IContent interface.");
				return false;
			}

			logger.info("Loading Zone populate class: " + name);
			java.lang.reflect.Constructor constr = entityClass
					.getConstructor();

			// simply creatre a new instance. The constructor creates all
			// additionally objects
			constr.newInstance();
			return true;
		} catch (Exception e) {
			logger.warn("Zone Populate class(" + name + ") loading failed.", e);
			return false;
		}
	}

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

		String layer = xmlzone.getLayer("4_roof_add");
		if (layer != null) {
			area.addLayer(name + "_4_roof_add", layer);
		}

		area.addCollisionLayer(name + "_collision", xmlzone.getLayer("collision"));
		area.addProtectionLayer(name + "_protection", xmlzone.getLayer("protection"));

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
		int dest = zone.assignPortalID(door);

		String name = "int_" + zone.getID().getID() + "_house_"
				+ Integer.toString(dest);

		door.setDestination(name, 0);
		zone.assignRPObjectID(door);
		zone.addPortal(door);

		StendhalRPZone house = addArea(name, "int_house_000");
		Portal portal = new Portal();
		portal.setDestination(zone.getID().getID(), dest);
		portal.setX(7);
		portal.setY(1);
		portal.setNumber(0);
		house.assignRPObjectID(portal);
		house.addPortal(portal);
	}

	@Override
	public void onFinish() throws Exception {
		StendhalRPRuleProcessor.get().addGameEvent("server system", "shutdown");
	}

}
