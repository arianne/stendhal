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

import games.stendhal.common.CRC;
import games.stendhal.common.CollisionDetection;
import games.stendhal.server.entity.GrainField;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PlantGrower;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.SheepFood;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.rule.EntityManager;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObjectInvalidException;
import marauroa.common.game.RPObjectNotFoundException;
import marauroa.common.game.RPSlot;
import marauroa.common.net.TransferContent;
import marauroa.server.game.MarauroaRPZone;

import org.apache.log4j.Logger;

public class StendhalRPZone extends MarauroaRPZone {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalRPZone.class);

	private List<TransferContent> contents;
	private List<String> entryPoints;
	private List<String> zoneChangePoints;
	private List<Portal> portals;
	private List<NPC> npcs;
	private List<RespawnPoint> respawnPoints;
    private List<PlantGrower> plantGrowers;
    private List<RPEntity> playersAndFriends;


	/**
	 * A set of all items that are lying on the ground in this zone.
	 * This set is currently only used for plant growers, and these
	 * might be changed so that this set is no longer needed,
	 * so try to avoid using it.
	 */
	private Set<Item> itemsOnGround;

	private int numHouses;

	/** contains data to if a certain area is walkable */
	public CollisionDetection collisionMap;

	/** Contains data to verify is someone is in a PK-free area. */
	public CollisionDetection protectionMap;

	/** Position of this zone in the world map */
	private boolean interior;

	private int level;

	private int x;

	private int y;

	public StendhalRPZone(String name) {
		super(name);

		contents = new LinkedList<TransferContent>();
		entryPoints = new LinkedList<String>();
		zoneChangePoints = new LinkedList<String>();
		portals = new LinkedList<Portal>();
		itemsOnGround = new HashSet<Item>();
		numHouses = 0;

		npcs = new LinkedList<NPC>();
		respawnPoints = new LinkedList<RespawnPoint>();
        plantGrowers = new LinkedList<PlantGrower>();
        playersAndFriends = new LinkedList<RPEntity>();

		collisionMap = new CollisionDetection();
		protectionMap = new CollisionDetection();
	}

	@Override
	public void onInit() throws Exception {
		// do nothing
	}

	@Override
	public void onFinish() throws Exception {
		// do nothing
	}

	public List<NPC> getNPCList() {
		return npcs;
	}

	public List<Portal> getPortals() {
		return portals;
	}

	public Portal getPortal(int number) {
		for (Portal portal : portals) {
			if (portal.getNumber() == number) {
				return portal;
			}
		}

		return null;
	}

	public List<RespawnPoint> getRespawnPointList() {
		return respawnPoints;
	}

	public void addRespawnPoint(RespawnPoint point) {
		respawnPoints.add(point);
	}

	public List<PlantGrower> getPlantGrowers() {
		return plantGrowers;
	}

	public void addZoneChange(String entry) {
		zoneChangePoints.add(entry);
	}

    public void addPortal(Portal portal) {
		add(portal);
		portals.add(portal);
	}

	public int assignPortalID(Portal portal) {
		// We reserve the first 64 portals ids for hand made portals
		int max = 64;

		for (Portal p : portals) {
			if (p.getNumber() > max) {
				max = p.getNumber();
			}
		}

		portal.setNumber(max + 1);

		return portal.getNumber();
	}

	public void addNPC(NPC npc) {
		add(npc);
		npcs.add(npc);
	}

	public void addEntryPoint(String entry) {
		entryPoints.add(0, entry);
	}

	public void placeObjectAtEntryPoint(Entity object) {
		if (entryPoints.size() == 0) {
			return;
		}

		String entryPoint = entryPoints.get(0);
		String[] components = entryPoint.split(",");

		object.setx(Integer.parseInt(components[0]));
		object.sety(Integer.parseInt(components[1]));
	}

	public void placeObjectAtZoneChangePoint(StendhalRPZone oldzone,
			Entity object) {
		if (zoneChangePoints.size() == 0) {
			return;
		}

		String exitDirection = null;

		if (object.gety() < 4) {
			exitDirection = "N";
		} else if (object.gety() > oldzone.getHeight() - 4) {
			exitDirection = "S";
		} else if (object.getx() < 4) {
			exitDirection = "W";
		} else if (object.getx() > oldzone.getWidth() - 4) {
			exitDirection = "E";
		} else {
			// NOTE: If any of the above is true, then it just put object on the
			// first zone change point.
			String[] components = zoneChangePoints.get(0).split(",");
			logger.debug("Player zone change default: " + components);
			object.setx(Integer.parseInt(components[0]));
			object.sety(Integer.parseInt(components[1]));
			return;
		}

		logger.debug("Player exit direction: " + exitDirection);

		int x = 0;
		int y = 0;
		int distance = Integer.MAX_VALUE;
		String minpoint = zoneChangePoints.get(0);

		if (exitDirection.equals("N")) {
			x = object.getx();
			y = getHeight();
		} else if (exitDirection.equals("S")) {
			x = object.getx();
			y = 0;
		} else if (exitDirection.equals("W")) {
			x = getWidth();
			y = object.gety();
		} else if (exitDirection.equals("E")) {
			x = 0;
			y = object.gety();
		}

		logger.debug("Player entry point: (" + x + "," + y + ")");

		for (String point : zoneChangePoints) {
			String[] components = point.split(",");
			int px = Integer.parseInt(components[0]);
			int py = Integer.parseInt(components[1]);

			if ((px - x) * (px - x) + (py - y) * (py - y) < distance) {
				logger.debug("Best entry point: (" + px + "," + py + ") --> "
						+ distance);
				distance = (px - x) * (px - x) + (py - y) * (py - y);
				minpoint = point;
			}
		}

		logger.debug("Choosen entry point: (" + minpoint + ") --> " + distance);
		String[] components = minpoint.split(",");
		object.setx(Integer.parseInt(components[0]));
		object.sety(Integer.parseInt(components[1]));
	}

	public void addLayer(String name, String byteContents) {
		Log4J.startMethod(logger, "addLayer");
		TransferContent content = new TransferContent();
		content.name = name;
		content.cacheable = true;
		content.data = byteContents.getBytes();
		content.timestamp = CRC.cmpCRC(content.data);

		contents.add(content);
		Log4J.finishMethod(logger, "addLayer");
	}

	public void addCollisionLayer(String name, String byteContents)
			throws IOException {
		Log4J.startMethod(logger, "addCollisionLayer");
		TransferContent content = new TransferContent();
		content.name = name;
		content.cacheable = true;
		logger.debug("Layer timestamp: " + Integer.toString(content.timestamp));
		content.data = byteContents.getBytes();
		content.timestamp = CRC.cmpCRC(content.data);

		contents.add(content);

		collisionMap.setCollisionData(new StringReader(byteContents));

		Log4J.finishMethod(logger, "addCollisionLayer");
	}

	public void addProtectionLayer(String name, String byteContents)
			throws IOException {
		Log4J.startMethod(logger, "addProtectionLayer");
		TransferContent content = new TransferContent();
		content.name = name;
		content.cacheable = true;
		logger.debug("Layer timestamp: " + Integer.toString(content.timestamp));
		content.data = byteContents.getBytes();
		content.timestamp = CRC.cmpCRC(content.data);

		contents.add(content);

		protectionMap.setCollisionData(new StringReader(byteContents));
		Log4J.finishMethod(logger, "addProtectionLayer");
	}

	public void setPosition(int level, int x, int y) {
		this.interior = false;
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public void setPosition() {
		this.interior = true;
	}

	public int getx() {
		return x;
	}

	public int gety() {
		return y;
	}

	public int getLevel() {
		return level;
	}

	public boolean isInterior() {
		return interior;
	}

	public boolean contains(Entity player, int level, int player_x, int player_y) {
		Rectangle2D area = player.getArea(player_x, player_y);
		Rectangle2D zone = new Rectangle(x, y, getWidth(), getHeight());

		return zone.intersects(area);
	}

	public boolean contains(Entity entity, StendhalRPZone zone) {
		Rectangle2D area = entity.getArea(entity.getx() + zone.x, entity.gety()
				+ zone.y);
		Rectangle2D zonearea = new Rectangle(x, y, getWidth(), getHeight());

		return zonearea.intersects(area);
	}

	// NOTE: Navigation layer is useless.
	// public void addNavigationLayer(String name, String byteContents) throws
	// IOException
	// {
	// Log4J.startMethod(logger,"addNavigationLayer");
	//   
	// if(byteContents==null)
	// {
	// logger.info("No navigation map for "+name+" found.");
	// return;
	// }
	//      
	// try
	// {
	// navigationMap = new NavigationMap();
	// navigationMap.setNavigationPoints(new StringReader(byteContents));
	// return;
	// }
	// catch (IOException fnfe)
	// {
	// logger.info("No navigation map for "+name+" found.", fnfe);
	// }
	//      
	// Log4J.finishMethod(logger,"addNavigationLayer");
	// }
	//  
	public void populate(String byteContents) throws IOException,
			RPObjectInvalidException {
		Log4J.startMethod(logger, "populate");

		BufferedReader file = new BufferedReader(new StringReader(byteContents));

		String text = file.readLine();
		String[] size = text.split(" ");
		int width = Integer.parseInt(size[0]);

		int j = 0;

		while ((text = file.readLine()) != null) {
			if (text.trim().equals("")) {
				break;
			}

			String[] items = text.split(",");
			for (String item : items) {
				int value = Integer.parseInt(item) - (2401) /*
															 * Number of tiles
															 * at
															 * zelda_outside_chipset
															 */;
				createEntityAt(value, j % width, j / width);
				j++;
			}
		}

		Log4J.finishMethod(logger, "populate");
	}

	protected void createEntityAt(int type, int x, int y) {
		try {
			switch (type) {
			case 1: /* Entry point */
			{
				String entryPoint = new String(x + "," + y);
				addEntryPoint(entryPoint);
				break;
			}
			case 2: /* Zone change */
			{
				String entryPoint = new String(x + "," + y);
				addZoneChange(entryPoint);
				break;
			}
			case 6: /* one way portal destination */
			case 3: /* portal stairs up */
			case 4: /* portal stairs down */
			{
				logger.debug("Portal stairs at " + this + ": " + x + "," + y);
				Portal portal;
				if (type != 6) {
					portal = new Portal();
				} else {
					portal = new OneWayPortalDestination();
				}

				assignRPObjectID(portal);
				portal.setx(x);
				portal.sety(y);
				assignPortalID(portal);
				addPortal(portal);

				boolean assigned = false;

				if (isInterior()) {
					// The algo doesn't work on interiors
					return;
				}

				for (IRPZone i : StendhalRPWorld.get()) {
					StendhalRPZone zone = (StendhalRPZone) i;

					if (zone.isInterior() == false
							&& Math.abs(zone.getLevel() - getLevel()) == 1) {
						if (!zone.contains(portal, this)) {
							continue;
						}

						logger.debug(zone + " contains " + portal);

						for (Portal target : zone.getPortals()) {
							if (target.loaded()) {
								logger.debug(target + " already loaded");
								continue;
							}

							logger.debug(target + " isn't loaded");

							if (target.getx() + zone.getx() == portal.getx()
									+ getx()
									&& target.gety() + zone.gety() == portal
											.gety()
											+ gety()) {
								int source = portal.getNumber();
								int dest = zone.assignPortalID(target);

								if (type != 6) {
									portal.setDestination(zone.getID().getID(),
											dest);
								}

								target.setDestination(getID().getID(), source);

								logger.debug("Portals LINKED");
								logger.debug(portal);
								logger.debug(target);
								assigned = true;
								break;
							} else {
								logger.debug("can't assign because it is a different portal");
							}
						}
					}
				}

				if (!assigned) {
					logger.debug(portal + " has no destination");
				}
			}
				break;
			case 5: /* portal */
				break;
			case 7: /* door */
				try {
					StendhalRPWorld.get().createHouse(this, x, y);
					numHouses++;
				} catch (Exception e) {
					logger.error("Error adding house to " + this, e);
				}
				break;
			case 11: /* sheep */ {
				/*RespawnPoint point = new RespawnPoint(x, y, 2);
				Creature creature = new Sheep();
				assignRPObjectID(creature);
				point.set(this, creature, 1);
				//point.setRespawnTime(creature.getRespawnTime());
				respawnPoints.add(point);*/
				
				Sheep sheep = new Sheep();
				assignRPObjectID(sheep);
				sheep.setx(x);
				sheep.sety(y);
				add(sheep);
				npcs.add(sheep);
				break;
			}
			case 91: /* sign */
				break;
			case 92: /* SheepFood */
			case 93: /* corn field */
			case 102: /* button mushroom */
			case 103: /* porcini */
			case 104: /* toadstool */
			case 108: /* apple */
			case 109: /* carrot */
			case 131: /* arandula */
			case 132: /* wood */				
			case 133: /* iron ore */				
				PlantGrower plantGrower = null;
				if (type == 92) {
					plantGrower = new SheepFood();
					// full fruits on server restart
					((SheepFood) plantGrower).setAmount(5);
				} else if (type == 93) {
					plantGrower = new GrainField();
					// full growth on server restart
					((GrainField) plantGrower).setRipeness(GrainField.RIPE);
				} else if (type == 102) {
					plantGrower = new PlantGrower("button_mushroom", 500);
				} else if (type == 103) {
					plantGrower = new PlantGrower("porcini", 1000);
				} else if (type == 104) {
					plantGrower = new PlantGrower("toadstool", 1000);
				} else if (type == 108) {
					plantGrower = new PlantGrower("apple", 750);
				} else if (type == 109) {
					plantGrower = new PlantGrower("carrot", 1000);
				} else if (type == 131) {
					plantGrower = new PlantGrower("arandula", 400);
				} else if (type == 132) {
					plantGrower = new PlantGrower("wood", 1500);
				} else if (type == 133) {
					plantGrower = new PlantGrower("iron_ore", 3000);
					// TODO: This is only a workaround. We should find a better name
					// than "plant grower", as we're also using them for resources,
					// teddies and whatever. We should also consider making them
					// non-clickable.
					plantGrower.setDescription("You see a place that seems to be rich in iron ore.");
				}
				assignRPObjectID(plantGrower);
				plantGrower.setx(x);
				plantGrower.sety(y);
				add(plantGrower);

				plantGrowers.add(plantGrower);
				break;
			default: {
				if (type >= 0) {
					// get the default EntityManager
					EntityManager manager = StendhalRPWorld.get().getRuleManager()
							.getEntityManager();

					// Is the entity a creature
					if (manager.isCreature(type)) {
						Creature creature = manager.getCreature(type);
						RespawnPoint point = new RespawnPoint(this, x, y, creature, 1);
						respawnPoints.add(point);
					} else {
						logger.warn("Unknown Entity (type: " + type + ") at ("
								+ x + "," + y + ") of " + getID() + " found");
					}
				}
				break;
			}
			}
		} catch (AttributeNotFoundException e) {
			logger.error("error creating entity " + type + " at (" + x + ","
					+ y + ")", e);
		}
	}

	public int getWidth() {
		return collisionMap.getWidth();
	}

	public int getHeight() {
		return collisionMap.getHeight();
	}

	public List<TransferContent> getContents() {
		return contents;
	}

	public boolean isInProtectionArea(Entity entity)
			throws AttributeNotFoundException {
		Rectangle2D area = entity.getArea(entity.getx(), entity.gety());
		return protectionMap.collides(area);
	}

	public boolean leavesZone(Entity entity, double x, double y)
			throws AttributeNotFoundException {
		Rectangle2D area = entity.getArea(x, y);
		return collisionMap.leavesZone(area);
	}

	public boolean simpleCollides(Entity entity, double x, double y)
			throws AttributeNotFoundException {
		Rectangle2D area = entity.getArea(x, y);
		return collisionMap.collides(area);
	}

	@Override
	public synchronized void add(RPObject object)
			throws RPObjectInvalidException {
		super.add(object);

		if (object instanceof Item) {
			Item item = (Item) object;
			item.onPutOnGround();
			itemsOnGround.add(item);
		}
	}

	@Override
	public synchronized RPObject remove(RPObject.ID id)
			throws RPObjectNotFoundException {
		RPObject object = super.remove(id);

		if (object instanceof Item) {
			Item item = (Item) object;
			itemsOnGround.remove(item);
			item.onRemoveFromGround();
		}
		return object;
	}

	public synchronized RPObject remove(RPObject object)
			throws RPObjectNotFoundException {
		if (object.isContained()) {
			// We modify the base container if the object change.
			RPObject base = object.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			modify(base);

			RPSlot slot = object.getContainerSlot();
			return slot.remove(object.getID());
		} else {
			return remove(object.getID());
		}
	}

	@Override
	public synchronized void modify(RPObject object) {
		if (object.isContained()) {
			// We modify the base container if the object change.
			RPObject base = object.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			super.modify(base);
		} else {
			super.modify(object);
		}
	}

	public boolean collides(int x, int y) throws AttributeNotFoundException {
		return collisionMap.collides(x, y);
	}

    /**
     * Checks whether the given entity would be able to stand at the given
     * position, or if it would collide with the collision map or with another
     * entity. 
     * @param entity The entity that would stand on the given position
     * @param x The x coordinate of the position where the entity would stand
     * @param y The y coordinate of the position where the entity would stand
     * @return true iff the entity could stand on the given position
     * @throws AttributeNotFoundException
     */
    public synchronized boolean collides(Entity entity, double x, double y)
    throws AttributeNotFoundException {
        return collides(entity, x, y, true);
    }

    /**
     * Checks whether the given entity would be able to stand at the given
     * position, or if it would collide with the collision map or
     * (if <i>checkObjects</i> is enabled) with another entity. 
     * @param entity The entity that would stand on the given position
     * @param x The x coordinate of the position where the entity would stand
     * @param y The y coordinate of the position where the entity would stand
     * @param checkObjects If false, only the collision map will be used. 
     * @return true iff the entity could stand on the given position
     * @throws AttributeNotFoundException
     */
    public synchronized boolean collides(Entity entity, double x, double y, boolean checkObjects)
			throws AttributeNotFoundException {
		Rectangle2D area = entity.getArea(x, y);

		if (collisionMap.collides(area)) {
			return true;
		} else if (!checkObjects) {
            return true;
        } else {
        	// For every other object in this zone, check whether it's in the
        	// way.
			Rectangle2D otherArea = new Rectangle.Double();
			for (RPObject other : objects.values()) {
				Entity otherEntity = (Entity) other;

				if (otherEntity.isObstacle()) {
					// There is something the entity couldn't stand upon.
					// Check if it's in the way. 
					otherEntity.getArea(otherArea, otherEntity.getx(),
							otherEntity.gety());
					if (area.intersects(otherArea)
							&& !entity.getID().equals(otherEntity.getID())) {
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * 
	 * @return the entity at x,y or null if there is none
	 */
	public synchronized Entity getEntityAt(double x, double y)
			throws AttributeNotFoundException {
		for (RPObject other : objects.values()) {
			Entity otherEntity = (Entity) other;

			Rectangle2D rect = otherEntity.getArea(otherEntity.getx(),
					otherEntity.gety());
			if (rect.contains(x, y)) {
				return otherEntity;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "zone " + zoneid + " at (" + x + "," + y + ")";
	}

	/**
	 * Returns a set of all items that are lying on the ground in this zone.
	 * This set is currently only used for plant growers, and these
	 * might be changed so that this set is no longer needed,
	 * so try to avoid using it.
	 */
	public Set<Item> getItemsOnGround() {
		return itemsOnGround;
	}
	
	public void addPlayerAndFriends(RPEntity player) {
		playersAndFriends.add(player);
	}

	/**
	 * Gets all players in this zone, as well as friendly entities
	 * such as sheep. These are the targets (enemies) for wild
	 * creatures such as orcs.
	 * @return a list of all players and friendly entities
	 */
	public List<RPEntity> getPlayerAndFirends() {
		return playersAndFriends;
	}
	
	public void removePlayerAndFriends(RPEntity player) {
		playersAndFriends.remove(player);
	}
}
