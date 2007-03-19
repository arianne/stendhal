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
import games.stendhal.common.Line;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.entity.spawner.CarrotGrower;
import games.stendhal.server.entity.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.spawner.GrainField;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.spawner.SheepFood;
import games.stendhal.server.events.MovementListener;
import games.stendhal.server.rule.EntityManager;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
	private Point		entryPoint;
	private List<Portal> portals;
	private List<NPC> npcs;
	private List<CreatureRespawnPoint> respawnPoints;
    private List<PassiveEntityRespawnPoint> plantGrowers;
    private List<RPEntity> playersAndFriends;
    private boolean teleportable = true;

	/**
	 * Objects that implement MovementListener.
	 */
	private List<MovementListener>	movementListeners;

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
		entryPoint = null;
		portals = new LinkedList<Portal>();
		itemsOnGround = new HashSet<Item>();
		numHouses = 0;

		npcs = new LinkedList<NPC>();
		respawnPoints = new LinkedList<CreatureRespawnPoint>();
        plantGrowers = new LinkedList<PassiveEntityRespawnPoint>();
        playersAndFriends = new LinkedList<RPEntity>();

		movementListeners = new LinkedList<MovementListener>();

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


	public Portal getPortal(Object reference) {
		for (Portal portal : portals) {
			if (portal.getReference().equals(reference)) {
				return portal;
			}
		}

		return null;
	}

	/**
	 * Get the portal (if any) at a specified zone position.
	 *
	 *
	 */
	public Portal getPortal(int x, int y) {
		for(Portal portal : portals) {
			if((portal.getX() == x) && (portal.getY() == y)) {
				return portal;
			}
		}

		return null;
	}

	public List<CreatureRespawnPoint> getRespawnPointList() {
		return respawnPoints;
	}

	public void addRespawnPoint(CreatureRespawnPoint point) {
		respawnPoints.add(point);
	}

	public List<PassiveEntityRespawnPoint> getPlantGrowers() {
		return plantGrowers;
	}

	// We reserve the first 64 portals ids for hand made portals
	private int maxPortalNumber = 64;

	public Object assignPortalID(Portal portal) {
		portal.setReference(new Integer(++maxPortalNumber));

		return portal.getReference();
	}

	public void setEntryPoint(int x, int y) {
		entryPoint = new Point(x, y);
	}

	public void placeObjectAtEntryPoint(Entity object) {
		if (entryPoint != null) {
			object.setX(entryPoint.x);
			object.setY(entryPoint.y);
		}
	}

	public void addLayer(String name, byte[] byteContents) {
		Log4J.startMethod(logger, "addLayer");
		addToContent(name, byteContents);
		Log4J.finishMethod(logger, "addLayer");
	}

	/**
	 * Creates a new TransferContent for the specified data and adds it
	 * to the contents list. 
	 */
	private void addToContent(String name, byte[] byteContents) {
		TransferContent content = new TransferContent();
		content.name = name;
		content.cacheable = true;
		logger.debug("Layer timestamp: " + Integer.toString(content.timestamp));
		content.data = byteContents;
		content.timestamp = CRC.cmpCRC(content.data);

		contents.add(content);
	}

	public void addCollisionLayer(String name, byte[] byteContents)
			throws IOException {
		Log4J.startMethod(logger, "addCollisionLayer");
		addToContent(name, byteContents);
		collisionMap.setCollisionData(new InputStreamReader(new ByteArrayInputStream(byteContents)));

		Log4J.finishMethod(logger, "addCollisionLayer");
	}

	public void addProtectionLayer(String name, byte[] byteContents)
			throws IOException {
		Log4J.startMethod(logger, "addProtectionLayer");
		addToContent(name, byteContents);
		protectionMap.setCollisionData(new InputStreamReader(new ByteArrayInputStream(byteContents)));
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

	public int getX() {
		return x;
	}

	public int getY() {
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
		Rectangle2D area = entity.getArea(entity.getX() + zone.x, entity.getY()
				+ zone.y);
		Rectangle2D zonearea = new Rectangle(x, y, getWidth(), getHeight());

		return zonearea.intersects(area);
	}


	/**
	 * Populate a zone based on it's map content.
	 *
	 * XXX - This should be moved to the zone loader or something.
	 */
	public void populate(byte[] byteContents) throws IOException,
			RPObjectInvalidException {
		Log4J.startMethod(logger, "populate");

		BufferedReader file = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteContents)));

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


	/**
	 * Create a map entity as a given coordinate.
	 *
	 *
	 */
	protected void createEntityAt(int type, int x, int y) {
		try {
			switch (type) {
			case 1: /* Entry point */
			case 2: /* Zone change */
				setEntryPoint(x, y);
				break;

			case 6: /* one way portal destination */
			case 3: /* portal stairs up */
			case 4: /* portal stairs down */
				createLevelPortalAt(type, x, y);
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
				sheep.setX(x);
				sheep.setY(y);
				add(sheep);
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
			case 110: /* salad */
			case 131: /* arandula */
			case 132: /* wood */				
			case 133: /* iron ore */				
				PassiveEntityRespawnPoint plantGrower = null;
				if (type == 92) {
					plantGrower = new SheepFood();
				} else if (type == 93) {
					plantGrower = new GrainField();
				} else if (type == 102) {
					plantGrower = new PassiveEntityRespawnPoint("button_mushroom", 500);
				} else if (type == 103) {
					plantGrower = new PassiveEntityRespawnPoint("porcini", 1000);
				} else if (type == 104) {
					plantGrower = new PassiveEntityRespawnPoint("toadstool", 1000);
				} else if (type == 108) {
					plantGrower = new PassiveEntityRespawnPoint("apple", 750);
				} else if (type == 109) {
					plantGrower = new CarrotGrower();
				} else if (type == 110) {
					plantGrower = new PassiveEntityRespawnPoint("salad", 1500);
				} else if (type == 131) {
					plantGrower = new PassiveEntityRespawnPoint("arandula", 400);
				} else if (type == 132) {
					plantGrower = new PassiveEntityRespawnPoint("wood", 1500);
				} else if (type == 133) {
					plantGrower = new PassiveEntityRespawnPoint("iron_ore", 3000);
					// TODO: This is only a workaround. We should find a better name
					// than "plant grower", as we're also using them for resources,
					// teddies and whatever. We should also consider making them
					// non-clickable.
					plantGrower.setDescription("You see a small vein of iron ore.");
				}
				assignRPObjectID(plantGrower);
				plantGrower.setX(x);
				plantGrower.setY(y);
				add(plantGrower);
				// full fruits on server restart
				plantGrower.setToFullGrowth();
				
				plantGrowers.add(plantGrower);

				/*
				 * XXX - TEMP!!
				 * Until all maps are fixed, set all sheep food
				 * as a collision.
				 */
				if (type == 92) {
					collisionMap.setCollide(
						plantGrower.getArea(x, y),
						true);
				}

				break;
			default: {
				if (type >= 0) {
					// get the default EntityManager
					EntityManager manager = StendhalRPWorld.get().getRuleManager()
							.getEntityManager();

					// Is the entity a creature
					if (manager.isCreature(type)) {
						Creature creature = manager.getCreature(type);
						CreatureRespawnPoint point = new CreatureRespawnPoint(this, x, y, creature, 1);
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


	/*
	 * Create a portal between levels.
	 *
	 *
	 */
	protected void createLevelPortalAt(int type, int x, int y) {
		if(logger.isDebugEnabled()) {
			logger.debug("Portal stairs at " + this
				+ ": " + x + "," + y);
		}

		Portal portal;

		if (type != 6) {
			portal = new Portal();
		} else {
			portal = new OneWayPortalDestination();
		}

		assignRPObjectID(portal);
		portal.setX(x);
		portal.setY(y);
		assignPortalID(portal);
		add(portal);

		boolean assigned = false;

		if (isInterior()) {
			// The algo doesn't work on interiors
			return;
		}

		for (IRPZone i : StendhalRPWorld.get()) {
			StendhalRPZone zone = (StendhalRPZone) i;

			if (zone.isInterior()) {
				continue;
			}

			/*
			 * Portals in the correct direction?
			 */
			if(type == 3) {
				/* portal stairs up */
				if((zone.getLevel() - getLevel()) != 1) {
					continue;
				}
			}
			else if(type == 4) {
				/* portal stairs down */
				if((zone.getLevel() - getLevel()) != -1) {
					continue;
				}
			} else {
				/* one way portal - POTENTIALLY WRONG LEVEL */
				/* Should they always go down (drop only)? */
				if(Math.abs(zone.getLevel() - getLevel()) != 1) {
					continue;
				}
			}

			if (!zone.contains(portal, this)) {
				continue;
			}

			logger.debug(zone + " contains " + portal);

			Portal target = zone.getPortal(
				portal.getX() + getX() - zone.getX(),
				portal.getY() + getY() - zone.getY());

			if(target == null) {
				continue;
			}

			if (target.loaded()) {
				logger.debug(target + " already loaded");
				continue;
			}

			if (type != 6) {
				portal.setDestination(
					zone.getID().getID(),
					zone.assignPortalID(target));
			}

			target.setDestination(
				getID().getID(), portal.getReference());

			logger.debug("Portals LINKED");
			logger.debug(portal);
			logger.debug(target);
			assigned = true;
			break;
		}

		if (!assigned) {
			logger.debug(portal + " has no destination");
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
		Rectangle2D area = entity.getArea(entity.getX(), entity.getY());
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
		add(object, null);
	}

	/**
	 * Adds an object to the ground.
	 * 
	 * The player parameter can be used to create special items that react
	 * when they are dropped on the ground by a player.
	 * 
	 * @param object The object that should be added to the zone
	 * @param player The player who put the object on the ground, or null
	 *               if the object wasn't carried by a player before
	 * @throws RPObjectInvalidException
	 */
	public synchronized void add(RPObject object, Player player)
			throws RPObjectInvalidException {
		super.add(object);
		
		if (object instanceof Item) {
			Item item = (Item) object;
			item.onPutOnGround(player);
			itemsOnGround.add(item);
		}

		if(object instanceof SpeakerNPC) {
			npcs.add((NPC) object);
		} else if(object instanceof AttackableCreature) {
			npcs.add((NPC) object);
		}

		if(object instanceof Portal) {
			portals.add((Portal) object);
		}

		if(object instanceof Entity) {
			((Entity) object).onAdded(this);
		}
	}


	@Override
	public synchronized RPObject remove(RPObject.ID id)
			throws RPObjectNotFoundException {
		RPObject object = get(id);

		if(object instanceof Entity) {
			((Entity) object).onRemoved(this);
		}

		if(object instanceof Portal) {
			portals.remove((Portal) object);
		}

		/*
		 * Remove from secondary lists
		 */
		playersAndFriends.remove(object);

		if(object instanceof NPC) {
			npcs.remove(object);
			StendhalRPRuleProcessor.get().removeNPC((NPC) object);
		}

		super.remove(id);

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

	/**
	 * Checks if there is a collision on the airline between 2 positions. 
	 * Only the collision map will be used.
	 * 
	 * @param x1 x value of position 1
	 * @param y1 y value of position 1
	 * @param x2 x value of position 2
	 * @param y2 y value of position 2
	 * @return true if there is a collision
	 */
	public boolean collidesOnLine(int x1, int y1, int x2, int y2)
			throws AttributeNotFoundException {

		Vector<Point> points = Line.renderLine(x1, y1, x2, y2);
		for (Point point : points) {
			if (collides((int) point.getX(), (int) point.getY())) {
				return true;
			}
		}
		return false;
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
            return false;
        } else {
        	// For every other object in this zone, check whether it's in the
        	// way.
			Rectangle2D otherArea = new Rectangle.Double();
			for (RPObject other : objects.values()) {
				Entity otherEntity = (Entity) other;

				if (otherEntity.isObstacle(entity)) {
					// There is something the entity couldn't stand upon.
					// Check if it's in the way. 
					otherEntity.getArea(otherArea, otherEntity.getX(),
							otherEntity.getY());
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

			Rectangle2D rect = otherEntity.getArea(otherEntity.getX(),
					otherEntity.getY());
			if (rect.contains(x, y)) {
				return otherEntity;
			}
		}
		return null;
	}


	/**
	 * Notify anything interested in when an entity entered.
	 *
	 * @param	entity		The entity that entered.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void notifyEntered(RPEntity entity, int newX, int newY) {
		Rectangle2D	eArea;


		eArea = entity.getArea(newX, newY);

		for(MovementListener l : movementListeners) {
			if(l.getArea().intersects(eArea)) {
				l.onEntered(entity, this, newX, newY);
			}
		}
	}


	/**
	 * Notify anything interested in when an entity exited.
	 *
	 * @param	entity		The entity that moved.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 */
	public void notifyExited(RPEntity entity, int oldX, int oldY) {
		Rectangle2D	eArea;


		eArea = entity.getArea(oldX, oldY);

		for(MovementListener l : movementListeners) {
			if(l.getArea().intersects(eArea)) {
				l.onExited(entity, this, oldX, oldY);
			}
		}
	}


	/**
	 * Notify anything interested that an entity moved.
	 *
	 * @param	entity		The entity that moved.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void notifyMovement(RPEntity entity, int oldX, int oldY,
	 int newX, int newY) {
		Rectangle2D	area;
		Rectangle2D	oeArea;
		Rectangle2D	neArea;
		boolean		oldIn;
		boolean		newIn;


		/*
		 * Not in this zone?
		 */
		if(!has(entity.getID())) {
			return;
		}

		oeArea = entity.getArea(oldX, oldY);
		neArea = entity.getArea(newX, newY);

		for(MovementListener l : movementListeners) {
			area = l.getArea();

			oldIn = area.intersects(oeArea);
			newIn = area.intersects(neArea);

			if(!oldIn && newIn) {
				l.onEntered(entity, this, newX, newY);
			}

			if(oldIn && newIn) {
				l.onMoved(entity, this, oldX, oldY, newX, newY);
			}

			if(oldIn && !newIn) {
				l.onExited(entity, this, oldX, oldY);
			}
		}
	}


	/**
	 * Register a movement listener for notification. Eventually create
	 * a macro-block hash to cut down on listeners to check.
	 *
	 * @param	listener	A movement listener to register.
	 */
	public void addMovementListener(MovementListener listener) {
		movementListeners.add(listener);
	}


	/**
	 * Unregister a movement listener from notification.
	 *
	 * @param	listener	A movement listener to unregister.
	 */
	public void removeMovementListener(MovementListener listener) {
		movementListeners.remove(listener);
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

	/**
	 * Can magic scrolls for teleportation be used in this zone?
	 *
	 * @return true, if teleportion is possible, false otherwise
	 */
	public boolean isTeleportable() {
		return teleportable;
	}

	/**
	 * Sets the flag whether magic scrolls for teleportation may
	 * be uses in this zone.
	 *
	 * @param teleportable true, if teleportion is possible, false otherwise
	 */
	public void setTeleportable(boolean teleportable) {
		this.teleportable = teleportable;
	}

	public void addMap(String name, byte[] mapData) {
		addToContent(name, mapData);
	}
}
