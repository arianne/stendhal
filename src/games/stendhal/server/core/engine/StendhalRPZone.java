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

import games.stendhal.common.CRC;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Debug;
import games.stendhal.common.Line;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.core.config.zone.TeleportationRules;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.events.ZoneEnterExitListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.OneWayPortalDestination;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPointFactory;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.tools.tiled.LayerDefinition;
import games.stendhal.tools.tiled.TileSetDefinition;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.net.OutputSerializer;
import marauroa.common.net.message.TransferContent;
import marauroa.server.game.rp.MarauroaRPZone;

import org.apache.log4j.Logger;

public class StendhalRPZone extends MarauroaRPZone {

	TeleportationRules teleRules = new TeleportationRules();
	
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPZone.class);

	private final List<TransferContent> contents;

	private Point entryPoint;

	private final List<Portal> portals;

	private final List<NPC> npcs;

	/**
	 * The sheep foods in the zone.
	 */
	private final List<SheepFood> sheepFoods;

	private final List<CreatureRespawnPoint> respawnPoints;

	private final List<PassiveEntityRespawnPoint> plantGrowers;

	private final List<RPEntity> playersAndFriends;

	private final List<Player> players;

	/**
	 * The blood spills.
	 */
	private final List<Blood> bloods;

	//private boolean teleportAllowed = true;

	private boolean moveToAllowed = true;

	/**
	 * Objects that implement MovementListener.
	 */
	private final List<MovementListener> movementListeners;

	
	private final List<ZoneEnterExitListener> zoneListeners;

	/**
	 * A set of all items that are lying on the ground in this zone. This set is
	 * currently only used for plant growers, and these might be changed so that
	 * this set is no longer needed, so try to avoid using it.
	 */
	private final Set<Item> itemsOnGround;

	/** contains data to if a certain area is walkable. */
	public CollisionDetection collisionMap;

	/** Contains data to verify is someone is in a PK-free area. */
	public CollisionDetection protectionMap;

	/** Position of this zone in the world map. */
	private boolean interior;

	private int level;

	private int x;

	private int y;

	public StendhalRPZone(final String name) {
		super(name);

		contents = new LinkedList<TransferContent>();
		entryPoint = null;
		portals = new LinkedList<Portal>();
		itemsOnGround = new HashSet<Item>();
		bloods = new LinkedList<Blood>();
		npcs = new LinkedList<NPC>();
		sheepFoods = new LinkedList<SheepFood>();
		respawnPoints = new LinkedList<CreatureRespawnPoint>();
		plantGrowers = new LinkedList<PassiveEntityRespawnPoint>();
		players = new LinkedList<Player>();
		playersAndFriends = new LinkedList<RPEntity>();

		movementListeners = new LinkedList<MovementListener>();
		zoneListeners = new LinkedList<ZoneEnterExitListener>();

		collisionMap = new CollisionDetection();
		protectionMap = new CollisionDetection();
	}

	public StendhalRPZone(final String name, final int width, final int height) {
		this(name);
		collisionMap.init(width, height);
	}

	public StendhalRPZone(final String name, final StendhalRPZone zone) {
		this(name);
		contents.addAll(zone.contents);
		collisionMap = zone.collisionMap;
		protectionMap  = zone.protectionMap;
		
		this.zoneid = new ID(name);
	}

	/**
	 * Get blood (if any) at a specified zone position.
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 * 
	 * @return The blood, or <code>null</code>.
	 */
	public Blood getBlood(final int x, final int y) {
		for (final Blood blood : bloods) {
			if ((blood.getX() == x) && (blood.getY() == y)) {
				return blood;
			}
		}

		return null;
	}

	public List<NPC> getNPCList() {
		return npcs;
	}

	public List<Portal> getPortals() {
		return portals;
	}

	public Portal getPortal(final Object reference) {
		for (final Portal portal : portals) {
			if (portal.getIdentifier().equals(reference)) {
				return portal;
			}
		}

		return null;
	}

	/**
	 * Get the portal (if any) at a specified zone position.
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 * 
	 * @return The portal, or <code>null</code>.
	 */
	public Portal getPortal(final int x, final int y) {
		for (final Portal portal : portals) {
			if ((portal.getX() == x) && (portal.getY() == y)) {
				return portal;
			}
		}

		return null;
	}

	/**
	 * Get the list of sheep foods in the zone.
	 * 
	 * @return The list of sheep foods.
	 */
	public List<SheepFood> getSheepFoodList() {
		return sheepFoods;
	}

	public List<CreatureRespawnPoint> getRespawnPointList() {
		return respawnPoints;
	}

	/**
	 * Add a creature respawn point to the zone.
	 * 
	 * @param point
	 *            The respawn point.
	 */
	public void add(final CreatureRespawnPoint point) {
		respawnPoints.add(point);
	}

	/**
	 * Remove a creature respawn point from the zone.
	 * 
	 * @param point
	 *            The respawn point.
	 */
	public void remove(final CreatureRespawnPoint point) {
		respawnPoints.remove(point);
	}

	public List<PassiveEntityRespawnPoint> getPlantGrowers() {
		return plantGrowers;
	}

	/** We reserve the first 64 portals ids for hand made portals. */
	private int maxPortalNumber = 64;

	public Object assignPortalID(final Portal portal) {
		portal.setIdentifier(Integer.valueOf(++maxPortalNumber));

		return portal.getIdentifier();
	}

	public void setEntryPoint(final int x, final int y) {
		entryPoint = new Point(x, y);
	}

	public boolean placeObjectAtEntryPoint(final Entity entity) {
		if (entryPoint != null) {
			return StendhalRPAction.placeat(this, entity, entryPoint.x,
					entryPoint.y);
		} else {
			return false;
		}
	}

	public void addLayer(final String name, final LayerDefinition layer) throws IOException {
		final byte[] byteContents = layer.encode();
		addToContent(name, byteContents);
	}

	public void addTilesets(final String name, final List<TileSetDefinition> tilesets)
			throws IOException {
		/*
		 * Serialize the tileset data to send it to client.
		 */
		final ByteArrayOutputStream array = new ByteArrayOutputStream();
		final OutputSerializer out = new OutputSerializer(array);

		int amount = 0;

		for (final TileSetDefinition set : tilesets) {
			if (!set.getSource().contains("logic/")) {
				amount++;
			}

		}

		out.write(amount);
		for (final TileSetDefinition set : tilesets) {
			if (!set.getSource().contains("logic/")) {
				set.writeObject(out);
			}
		}

		addToContent(name, array.toByteArray());
	}

	/**
	 * Creates a new TransferContent for the specified data and adds it to the
	 * contents list.
	 * @param name 
	 * @param byteContents 
	 */
	private void addToContent(final String name, final byte[] byteContents) {
		final TransferContent content = new TransferContent();
		content.name = name;
		content.cacheable = true;
		logger.debug("Layer timestamp: " + Integer.toString(content.timestamp));
		content.data = byteContents;
		content.timestamp = CRC.cmpCRC(content.data);

		contents.add(content);
	}

	public void addCollisionLayer(final String name, final LayerDefinition collisionLayer)
			throws IOException {
		addToContent(name, collisionLayer.encode());
		collisionMap.setCollisionData(collisionLayer);
	}

	public void addProtectionLayer(final String name, final LayerDefinition protectionLayer)
			throws IOException {
		addToContent(name, protectionLayer.encode());
		protectionMap.setCollisionData(protectionLayer);
	}

	public void setPosition(final int level, final int x, final int y) {
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

	/**
	 * Determine if this zone overlaps an area in global coordinates.
	 * 
	 * @param area
	 *            The area (in global coordinate space).
	 * 
	 * @return <code>true</code> if the area overlaps.
	 */
	public boolean intersects(final Rectangle2D area) {
		final Rectangle2D zone = new Rectangle(x, y, getWidth(), getHeight());

		return zone.intersects(area);
	}

	/**
	 * Populate a zone based on it's map content.
	 * 
	 * TODO: This should be moved to the zone loader or something.
	 * @param objectsLayer 
	 */
	public void populate(final LayerDefinition objectsLayer) {
		/* We build the layer data */
		objectsLayer.build();

		for (int yTemp = 0; yTemp < objectsLayer.getHeight(); yTemp++) {
			for (int xTemp = 0; xTemp < objectsLayer.getWidth(); xTemp++) {
				final int value = objectsLayer.getTileAt(xTemp, yTemp);
				if (value > 0) {
					/*
					 * When the value is 0, it means that there is no tile at
					 * that point.
					 */
					final TileSetDefinition tileset = objectsLayer.getTilesetFor(value);
					createEntityAt(tileset.getSource(), value
							- tileset.getFirstGid(), xTemp, yTemp);
				}
			}
		}
	}

	/**
	 * Create a map entity as a given coordinate.
	 * 
	 * @param clazz
	 *            the clazz of entity we are loading.<br>
	 *            It is related to the way entities are stored in tilesets now.
	 * @param type integer to represent the type of entity to be created.
	 * <p> if the class contains portal type is evaluated as follows: 
	 * <ul>
	 * <li> 0 , 1  entry point 
	 * <li> 1 zone change
	 * <li> 5 ,2 , 3 LevelPortal
	 * </ul>
	 
	 * @param x 
	 * @param y 
	 * 
	 * 
	 */
	protected void createEntityAt(final String clazz, final int type, final int x, final int y) {
		logger.debug("creating " + clazz + ":" + type + " at " + x + "," + y);
		final int ENTRY_POINT = 0;
		final int ZONE_CHANGE = 1;
		final int DOOR = 6;
		final int PORTAL = 4;
		final int PORTAL_STAIRS_DOWN = 3;
		final int PORTAL_STAIRS_UP = 2;
		final int ONE_WAY_PORTAL_DESTINATION = 5;
		
		try {
			if (clazz.contains("logic/portal")) {
				switch (type) {
				
				case ENTRY_POINT: 
				case ZONE_CHANGE: 
					
					setEntryPoint(x, y);
					break;

				case ONE_WAY_PORTAL_DESTINATION: 
				case PORTAL_STAIRS_UP: 
				case PORTAL_STAIRS_DOWN: 
					createLevelPortalAt(type, x, y);
					break;
				case PORTAL:
					break;
				
				case DOOR: 
					break;
				default:
					logger.error("Unknown Portal (class/type: " + clazz + ":"
							+ type + ") at (" + x + "," + y + ") of " + getID()
							+ " found");
					break;
				}
			} else if (clazz.contains("sheep.png")) {
				final Sheep sheep = new Sheep();
				sheep.setPosition(x, y);
				add(sheep);
			} else if (clazz.contains("logic/creature")) {
				// get the default EntityManager
				final EntityManager manager = SingletonRepository.getEntityManager();

				// Is the entity a creature
				if (manager.isCreature(clazz, type)) {
					final Creature creature = manager.getCreature(clazz, type);
					final CreatureRespawnPoint point = new CreatureRespawnPoint(this,
							x, y, creature, 1);
					add(point);
				} else {
					logger.error("Unknown Entity (class/type: " + clazz + ":"
							+ type + ") at (" + x + "," + y + ") of " + getID()
							+ " found");
				}
			} else if (clazz.contains("logic/item")) {
				final PassiveEntityRespawnPoint passiveEntityrespawnPoint = PassiveEntityRespawnPointFactory.create(
						clazz, type, getID(), x, y);
				if (passiveEntityrespawnPoint != null) {
					passiveEntityrespawnPoint.setPosition(x, y);
					add(passiveEntityrespawnPoint);
					passiveEntityrespawnPoint.setStartState();
				
				}
			}
		} catch (final Exception e) {
			logger.error("error creating entity " + type + " at (" + x + ","
					+ y + ")", e);
		}
	}

	/*
	 * Create a portal between levels.
	 * 
	 * 
	 */
	protected void createLevelPortalAt(final int type, final int x, final int y) {
		if (logger.isDebugEnabled()) {
			logger.debug("Portal stairs at " + this + ": " + x + "," + y);
		}

		Portal portal;

		if (type != 5) {
			portal = new Portal();
		} else {
			portal = new OneWayPortalDestination();
		}

		portal.setPosition(x, y);
		assignPortalID(portal);
		add(portal);

		boolean assigned = false;

		if (isInterior()) {
			// The algo doesn't work on interiors
			return;
		}

		for (final IRPZone i : SingletonRepository.getRPWorld()) {
			final StendhalRPZone zone = (StendhalRPZone) i;

			if (zone.isInterior()) {
				continue;
			}

			/*
			 * Portals in the correct direction?
			 */
			if (type == 2) {
				/* portal stairs up */
				if ((zone.getLevel() - getLevel()) != 1) {
					continue;
				}
			} else if (type == 3) {
				/* portal stairs down */
				if ((zone.getLevel() - getLevel()) != -1) {
					continue;
				}
			} else {
				/* one way portal - POTENTIALLY WRONG LEVEL */
				/* Should they always go down (drop only)? */
				if (Math.abs(zone.getLevel() - getLevel()) != 1) {
					continue;
				}
			}

			final Portal target = zone.getPortal(
					portal.getX() + getX() - zone.getX(), portal.getY()
							+ getY() - zone.getY());

			if (target == null) {
				continue;
			}

			logger.debug(zone + " contains " + target);

			if (target.loaded()) {
				logger.debug(target + " already loaded");
				continue;
			}

			if (type != 5) {
				portal.setDestination(zone.getName(),
						zone.assignPortalID(target));
			}

			target.setDestination(getName(), portal.getIdentifier());

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

	public boolean isInProtectionArea(final Entity entity) {
		final Rectangle2D area = entity.getArea(entity.getX(), entity.getY());
		return protectionMap.collides(area);
	}

	public boolean leavesZone(final Entity entity, final double x, final double y) {
		final Rectangle2D area = entity.getArea(x, y);
		return collisionMap.leavesZone(area);
	}

	public boolean simpleCollides(final Entity entity, final double x, final double y) {
		final Rectangle2D area = entity.getArea(x, y);
		return collisionMap.collides(area);
	}

	@Override
	public synchronized void add(final RPObject object) {
		add(object, true);
	}

	/**
	 * Adds an object to the ground.
	 * 
	 * The player parameter can be used to create special items that react when
	 * they are dropped on the ground by a player.
	 * 
	 * @param object
	 *            The object that should be added to the zone
	 * @param player
	 * 		The player that dropped the item
	 */
	public void add(final RPObject object, final Player player) {
		add(object, player, true);
	}
	
	/**
	 * Adds an object to the ground.
	 * 
	 * @param object
	 *            The object that should be added to the zone
	 * @param expire
	 *            True if the item should expire according to its normal behaviour, 
	 *            false otherwise
	 */
	public void add(final RPObject object, final boolean expire) {
		add(object, null, expire);
	}
	
	private synchronized void add(final RPObject object, final Player player, final boolean expire) {
		/*
		 * Assign [zone relative] ID info. TODO: Move up to MarauroaRPZone
		 */
		assignRPObjectID(object);
		super.add(object);

		notifyAdded(object);
		
		// Needs to be before adding an item, in case Item.onPutOnGround() 
		// needs proper zone information
		if (object instanceof Entity) {
			((Entity) object).onAdded(this);
		}
		
		if (object instanceof Item) {
			final Item item = (Item) object;
			if (player != null) {
				// let item decide what to do when it's thrown by a player
				item.onPutOnGround(player);
			} else {
				// otherwise follow expire
				item.onPutOnGround(expire);
			}
			itemsOnGround.add(item);
		}

		/*
		 * Eventually move to <Entity>.onAdded().
		 */
		if (object instanceof PassiveEntityRespawnPoint) {
			plantGrowers.add((PassiveEntityRespawnPoint) object);
		}

		if (object instanceof Blood) {
			bloods.add((Blood) object);
		} else if (object instanceof Player) {
			players.add((Player) object);
			playersAndFriends.add((Player) object);
		} else if (object instanceof AttackableCreature) {
			playersAndFriends.add((AttackableCreature) object);
		} else if (object instanceof Sheep) {
			playersAndFriends.add((Sheep) object);
		} else if (object instanceof SheepFood) {
			sheepFoods.add((SheepFood) object);
		} else if (object instanceof BabyDragon) {
			playersAndFriends.add((BabyDragon) object);
		} else if (object instanceof SpeakerNPC) {
			SingletonRepository.getNPCList().add((SpeakerNPC) object);
		} else if (object instanceof Portal) {
			Portal portal = (Portal) object;
			if (portal.getIdentifier() == null) {
				logger.error("Portal without identifier: " + portal, new Throwable());
			} else {
				portals.add(portal);
			}
		}

		if (object instanceof NPC) {
			npcs.add((NPC) object);
		}

		// TODO: Move up to MarauroaRPZone?
		SingletonRepository.getRPWorld().requestSync(object);
	}

	private void notifyAdded(final RPObject object) {
		for (final ZoneEnterExitListener l : zoneListeners) {
			
				l.onEntered(object, this);
			
		}
		
	}
	private void notifyRemoved(final RPObject object) {
		for (final ZoneEnterExitListener l : zoneListeners) {
			
			l.onExited(object, this);
		
	}
		
	}

	@Override
	public synchronized RPObject remove(final RPObject.ID id) {

		final RPObject object = get(id);
		notifyRemoved(object);
		if (object instanceof Entity) {
			((Entity) object).onRemoved(this);
		}

		if (object instanceof NPC) {
			npcs.remove(object);
		}

		/*
		 * Eventually move to <Entity>.onRemoved().
		 */
		if (object instanceof PassiveEntityRespawnPoint) {
			plantGrowers.remove(object);
		}

		if (object instanceof Blood) {
			bloods.remove(object);
		} else if (object instanceof Player) {
			players.remove(object);
			playersAndFriends.remove(object);
		} else if (object instanceof AttackableCreature) {
			playersAndFriends.remove(object);
		} else if (object instanceof Sheep) {
			playersAndFriends.remove(object);
		} else if (object instanceof SheepFood) {
			sheepFoods.remove(object);
		} else if (object instanceof BabyDragon) {
			playersAndFriends.remove(object);
		} else if (object instanceof SpeakerNPC) {
			SingletonRepository.getNPCList().remove(((SpeakerNPC) object).getName());
		} else if (object instanceof Portal) {
			portals.remove(object);
		}

		super.remove(id);

		if (object instanceof Item) {
			final Item item = (Item) object;
			itemsOnGround.remove(item);
			item.onRemoveFromGround();
		}

		return object;
	}

	

	/**
	 * removes object from zone.
	 * 
	 * @param object
	 * @return the removed object
	 */
	public synchronized RPObject remove(final RPObject object) {
		if (object.isContained()) {
			// We modify the base container if the object change.

			// TODO: Remove? Isn't this the same as _in_ modify()?
			RPObject base = object.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			modify(base);

			final RPSlot slot = object.getContainerSlot();
			return slot.remove(object.getID());
		} else {
			return remove(object.getID());
		}
	}

	@Override
	public synchronized void modify(final RPObject object) {
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
	 * Checks if there is a collision on the airline between 2 positions. Only
	 * the collision map will be used.
	 * 
	 * @param x1
	 *            x value of position 1
	 * @param y1
	 *            y value of position 1
	 * @param x2
	 *            x value of position 2
	 * @param y2
	 *            y value of position 2
	 * @return true if there is a collision
	 */
	public boolean collidesOnLine(final int x1, final int y1, final int x2, final int y2) {

		final Vector<Point> points = Line.renderLine(x1, y1, x2, y2);
		for (final Point point : points) {
			if (collides((int) point.getX(), (int) point.getY())) {
				return true;
			}
		}
		return false;
	}

	public boolean collides(final int x, final int y) {
		return collisionMap.collides(x, y);
	}

	/**
	 * Checks whether the given entity would be able to stand at the given
	 * position, or if it would collide with the collision map or with another
	 * entity.
	 * 
	 * @param entity
	 *            The entity that would stand on the given position
	 * @param x
	 *            The x coordinate of the position where the entity would stand
	 * @param y
	 *            The y coordinate of the position where the entity would stand
	 * @return true iff the entity could stand on the given position
	 */
	public synchronized boolean collides(final Entity entity, final double x, final double y) {
		return collides(entity, x, y, true);
	}

	/**
	 * Checks whether the given entity would be able to stand at the given
	 * position, or if it would collide with the collision map or (if
	 * <i>checkObjects</i> is enabled) with another entity.
	 * 
	 * @param entity
	 *            The entity that would stand on the given position
	 * @param x
	 *            The x coordinate of the position where the entity would stand
	 * @param y
	 *            The y coordinate of the position where the entity would stand
	 * @param checkObjects
	 *            If false, only the collision map will be used.
	 * @return true iff the entity could stand on the given position
	 */
	public synchronized boolean collides(final Entity entity, final double x, final double y,
			final boolean checkObjects) {
		final Rectangle2D area = entity.getArea(x, y);

		if (collisionMap.collides(area)) {
			return true;
		}

		if (checkObjects) {
			return collidesObjects(entity, area);
		}

		return false;
	}

	public boolean collidesObjects(final Entity entity, final Rectangle2D area) {
		// For every other object in this zone, check whether it's in the
		// way.
		return getCollidingObject(entity, area) != null;
	}

	private Entity getCollidingObject(final Entity entity, final Rectangle2D area) {
		for (final RPObject other : objects.values()) {
			/*
			 * Ignore same object
			 */
			if (entity != other) {
				final Entity otherEntity = (Entity) other;

				// Check if the objects overlap
				final Rectangle2D otherArea = otherEntity.getArea(otherEntity.getX(),
						otherEntity.getY());

				if (area.intersects(otherArea)) {
					// Check if it's blocking
					if (otherEntity.isObstacle(entity)) {
						return otherEntity;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Finds an Entity at the given coordinates. 
	 * 
	 * @param x coordinate 
	 * @param y coordinate
	 * @return the first entity found if there are more than one or null if there are none
	 */
	public synchronized Entity getEntityAt(final double x, final double y) {
		for (final RPObject other : objects.values()) {
			final Entity otherEntity = (Entity) other;

			final Rectangle2D rect = otherEntity.getArea(otherEntity.getX(),
					otherEntity.getY());
			if (rect.contains(x, y)) {
				return otherEntity;
			}
		}
		return null;
	}
	
	/**
	 * Finds all entities at the given coordinates.
	 * @param x coordinate
	 * @param y coordinate
	 * @return list of entities at (x, y)
	 */
	public synchronized List<Entity> getEntitiesAt(final double x, final double y) {
		List<Entity> entities = new LinkedList<Entity>();
		
		for (final RPObject other : objects.values()) {
			final Entity entity = (Entity) other;

			final Rectangle2D rect = entity.getArea(entity.getX(), 
					entity.getY());
			if (rect.contains(x, y)) {
				entities.add(entity);
			}
		}

		return entities;
	}

	/**
	 * Get the zone name. This is the same as <code>getID().getID()</code>,
	 * only cleaner to use.
	 * 
	 * @return The zone name.
	 */
	public String getName() {
		return getID().getID();
	}

	/**
	 * Notify anything interested in when an entity entered.
	 * 
	 * @param entity
	 *            The entity that entered.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	public void notifyEntered(final ActiveEntity entity, final int newX, final int newY) {
		Rectangle2D eArea;

		eArea = entity.getArea(newX, newY);

		for (final MovementListener l : movementListeners) {
			if (l.getArea().intersects(eArea)) {
				l.onEntered(entity, this, newX, newY);
			}
		}
	}

	/**
	 * Notify anything interested in when an entity exited.
	 * 
	 * @param entity
	 *            The entity that moved.
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 */
	public void notifyExited(final ActiveEntity entity, final int oldX, final int oldY) {
		Rectangle2D eArea;

		eArea = entity.getArea(oldX, oldY);

		for (final MovementListener l : movementListeners) {
			if (l.getArea().intersects(eArea)) {
				l.onExited(entity, this, oldX, oldY);
			}
		}
	}

	/**
	 * Notify anything interested that an entity moved.
	 * 
	 * @param entity
	 *            The entity that moved.
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	public void notifyMovement(final ActiveEntity entity, final int oldX, final int oldY,
			final int newX, final int newY) {
		Rectangle2D area;
		Rectangle2D oeArea;
		Rectangle2D neArea;
		boolean oldIn;
		boolean newIn;

		/*
		 * Not in this zone?
		 */
		if (!has(entity.getID())) {
			return;
		}

		oeArea = entity.getArea(oldX, oldY);
		neArea = entity.getArea(newX, newY);

		for (final MovementListener l : movementListeners) {
			area = l.getArea();

			oldIn = area.intersects(oeArea);
			newIn = area.intersects(neArea);

			if (!oldIn && newIn) {
				l.onEntered(entity, this, newX, newY);
			}

			if (oldIn && newIn) {
				l.onMoved(entity, this, oldX, oldY, newX, newY);
			}

			if (oldIn && !newIn) {
				l.onExited(entity, this, oldX, oldY);
			}
		}
	}

	public void addZoneEnterExitListener(final ZoneEnterExitListener listener) {
		zoneListeners.add(listener);
	}
	
	public void removeZoneEnterExitListener(final ZoneEnterExitListener listener) {
		zoneListeners.add(listener);
	}
	
	
	
	/**
	 * Register a movement listener for notification. Eventually create a
	 * macro-block hash to cut down on listeners to check.
	 * 
	 * @param listener
	 *            A movement listener to register.
	 */
	public void addMovementListener(final MovementListener listener) {
		movementListeners.add(listener);
	}

	/**
	 * Unregister a movement listener from notification.
	 * 
	 * @param listener
	 *            A movement listener to unregister.
	 */
	public void removeMovementListener(final MovementListener listener) {
		movementListeners.remove(listener);
	}

	@Override
	public String toString() {
		return "zone " + zoneid + " at (" + x + "," + y + ")";
	}

	/**
	 * @return a set of all items that are lying on the ground in this zone.
	 */
	public Set<Item> getItemsOnGround() {
		return itemsOnGround;
	}

	/**
	 * Gets all players in this zone.
	 * 
	 * @return A list of all players.
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Gets all players in this zone, as well as friendly entities such as
	 * sheep. These are the targets (enemies) for wild creatures such as orcs.
	 * 
	 * @return a list of all players and friendly entities
	 */
	public List<RPEntity> getPlayerAndFriends() {
		return playersAndFriends;
	}

	/**
	 * Can moveto (mouse movement using pathfinding) be done on this map?
	 * 
	 * @return true, if moveto is possible, false otherwise
	 */
	public boolean isMoveToAllowed() {
		return moveToAllowed;
	}

	/**
	 * Sets the flag whether moveto (mouse movement using pathfinding) is
	 * possible in this zone.
	 * 
	 * @param moveToAllowed
	 *            true, if it is possible, false otherwise
	 */
	public void setMoveToAllowed(final boolean moveToAllowed) {
		this.moveToAllowed = moveToAllowed;

	}

	private int debugturn;

	

	@Override
	public void nextTurn() {
		super.nextTurn();

		debugturn++;

		if (Debug.SHOW_LIST_SIZES && (debugturn % 1000 == 0)) {
			final StringBuilder os = new StringBuilder("Name: " + this.getID());
			os.append("blood: " + bloods.size() + "\n");
			os.append("itemsOnGround: " + itemsOnGround.size() + "\n");
			os.append("movementListeners: " + movementListeners.size() + "\n");
			os.append("npcs: " + npcs.size() + "\n");
			os.append("plantGrowers: " + plantGrowers.size() + "\n");
			os.append("players: " + players.size() + "\n");
			os.append("playersAndFriends: " + playersAndFriends.size() + "\n");
			os.append("portals: " + portals.size() + "\n");
			os.append("respawnPoints: " + respawnPoints.size() + "\n");
			os.append("sheepFoods: " + sheepFoods.size() + "\n");
			os.append("objects: " + objects.size() + "\n");
			logger.info(os);
		}
	}

	public void logic() {
		for (final NPC npc : npcs) {
			try {
				npc.logic();
			} catch (final Exception e) {
				logger.error("Error in npc logic for zone " + getID().getID(), e);
			}
		}
	}

	/**
	 * Return whether the zone is completely empty.
	 * @return true if there are no objects in zone
	 */
	public boolean isEmpty() {
	    return objects.isEmpty();
	}

	/**
	 * Return whether the zone contains one or more players.
	 * @return if there are players in zone
	 */
	public boolean containsPlayer() {
	    for (final RPObject obj : objects.values()) {
	        if (obj instanceof Player) {
	            return true;
            }
	    }

	    return false;
	}

    /**
     * Return whether the zone contains one or more animals.
     * @return true if there are domesticalanimals in zone
     */
    public boolean containsAnimal() {
        for (final RPObject obj : objects.values()) {
            if (obj instanceof DomesticAnimal) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return whether the zone contains any creature including players and animals.
     * @return true if there are creatures in zone
     */
    public boolean containsCreature() {
        for (final RPObject obj : objects.values()) {
            if (obj instanceof Creature) {
                return true;
            }
        }

        return false;
    }

    
	public List<Entity> getFilteredEntities(final FilterCriteria<Entity> criteria) {
		final List <Entity> result = new LinkedList<Entity>();
		
		for (final RPObject obj : objects.values()) {
	            if (obj instanceof Entity) {
					final Entity entity = (Entity) obj;
					if (criteria.passes(entity)) {
						result.add(entity);
					}
					
				}
	        }
		
		return result;
		
		
	}
	
	/**
	 * Sets the flag whether magic scrolls for teleportation may be uses in this
	 * zone.
	 */
	public void disAllowTeleport() {
		disallowIn();
		disallowOut();
	}
	
	/**
	 * Disallow teleporting to and from a specified area.
	 * 
	 * @param x left x coordinate
	 * @param y top y coordinate
	 * @param width width of the area
	 * @param height height of the area
	 */
	public void disAllowTeleport(int x, int y, int width, int height) {
		disallowIn(x, y, width, height);
		disallowOut(x, y, width, height);
	}

	/**
	 * Check if teleporting with a scroll to a location is allowed.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return <code>true</code> iff teleporting is allowed
	 */
	public boolean isTeleportInAllowed(int x, int y) {
		return teleRules.isInAllowed(x, y);
	}

	/**
	 * Check if teleporting with a scroll from a location is allowed.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return <code>true</code> iff teleporting is allowed
	 */
	public boolean isTeleportOutAllowed(int x, int y) {
		return teleRules.isOutAllowed(x, y);
	}

	/**
	 * Forbid teleporting to the entire zone using a scroll. 
	 */
	public void disallowIn() {
		teleRules.disallowIn();
	}
	
	/**
	 * Disallow teleporting to specified area.
	 * 
	 * @param x left x coordinate
	 * @param y top y coordinate
	 * @param width width of the area
	 * @param height height of the area
	 */
	public void disallowIn(int x, int y, int width, int height) {
		teleRules.disallowIn(x, y, width, height);
	}

	/**
	 * Forbid teleporting from the entire zone using a scroll.
	 */
	public void disallowOut() {
		teleRules.disallowOut();
	}
	
	/**
	 * Disallow teleporting from specified area.
	 * 
	 * @param x left x coordinate
	 * @param y top y coordinate
	 * @param width width of the area
	 * @param height height of the area
	 */
	public void disallowOut(int x, int y, int width, int height) {
		teleRules.disallowOut(x, y, width, height);
	}

	public void onRemoved() {
		for (RPObject inspected : this) {
			if (inspected instanceof ActiveEntity) {
				((ActiveEntity) inspected).onRemoved(this);
			}
		}
	}
	
}
