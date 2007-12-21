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
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.OneWayPortalDestination;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPointFactory;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.NPCList;
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

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPZone.class);

	private List<TransferContent> contents;

	private Point entryPoint;

	private List<Portal> portals;

	private List<NPC> npcs;

	/**
	 * The sheep foods in the zone.
	 */
	private List<SheepFood> sheepFoods;

	private List<CreatureRespawnPoint> respawnPoints;

	private List<PassiveEntityRespawnPoint> plantGrowers;

	private List<RPEntity> playersAndFriends;

	private List<Player> players;

	/**
	 * The blood spills.
	 */
	private List<Blood> bloods;

	private boolean teleportAllowed = true;

	private boolean moveToAllowed = true;

	/**
	 * Objects that implement MovementListener.
	 */
	private List<MovementListener> movementListeners;

	/**
	 * A set of all items that are lying on the ground in this zone. This set is
	 * currently only used for plant growers, and these might be changed so that
	 * this set is no longer needed, so try to avoid using it.
	 */
	private Set<Item> itemsOnGround;

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
		bloods = new LinkedList<Blood>();
		npcs = new LinkedList<NPC>();
		sheepFoods = new LinkedList<SheepFood>();
		respawnPoints = new LinkedList<CreatureRespawnPoint>();
		plantGrowers = new LinkedList<PassiveEntityRespawnPoint>();
		players = new LinkedList<Player>();
		playersAndFriends = new LinkedList<RPEntity>();

		movementListeners = new LinkedList<MovementListener>();

		collisionMap = new CollisionDetection();
		protectionMap = new CollisionDetection();
	}

	public StendhalRPZone(String name, int width, int height) {
		this(name);
		collisionMap.setWidth(width);
		collisionMap.setHeight(height);
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
		for (Blood blood : bloods) {
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

	public Portal getPortal(Object reference) {
		for (Portal portal : portals) {
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
	public Portal getPortal(int x, int y) {
		for (Portal portal : portals) {
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
	 * TODO: Make CreatureRespawnPoint a sub-class of Entity and use normal
	 * add().
	 * 
	 * @param point
	 *            The respawn point.
	 */
	public void add(CreatureRespawnPoint point) {
		respawnPoints.add(point);
	}

	/**
	 * Remove a creature respawn point from the zone.
	 * 
	 * TODO: Make CreatureRespawnPoint a sub-class of Entity and use normal
	 * remove().
	 * 
	 * @param point
	 *            The respawn point.
	 */
	public void remove(CreatureRespawnPoint point) {
		respawnPoints.remove(point);
	}

	public List<PassiveEntityRespawnPoint> getPlantGrowers() {
		return plantGrowers;
	}

	// We reserve the first 64 portals ids for hand made portals
	private int maxPortalNumber = 64;

	public Object assignPortalID(Portal portal) {
		portal.setIdentifier(new Integer(++maxPortalNumber));

		return portal.getIdentifier();
	}

	public void setEntryPoint(int x, int y) {
		entryPoint = new Point(x, y);
	}

	public boolean placeObjectAtEntryPoint(Entity entity) {
		if (entryPoint != null) {
			return StendhalRPAction.placeat(this, entity, entryPoint.x,
					entryPoint.y);
		} else {
			return false;
		}
	}

	public void addLayer(String name, LayerDefinition layer) throws IOException {
		byte[] byteContents = layer.encode();
		addToContent(name, byteContents);
	}

	public void addTilesets(String name, List<TileSetDefinition> tilesets)
			throws IOException {
		/*
		 * Serialize the tileset data to send it to client.
		 */
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		OutputSerializer out = new OutputSerializer(array);

		int amount = 0;

		for (TileSetDefinition set : tilesets) {
			if (!set.getSource().contains("logic/")) {
				amount++;
			}

		}

		out.write(amount);
		for (TileSetDefinition set : tilesets) {
			if (!set.getSource().contains("logic/")) {
				set.writeObject(out);
			}
		}

		addToContent(name, array.toByteArray());
	}

	/**
	 * Creates a new TransferContent for the specified data and adds it to the
	 * contents list.
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

	public void addCollisionLayer(String name, LayerDefinition collisionLayer)
			throws IOException {
		addToContent(name, collisionLayer.encode());
		collisionMap.setCollisionData(collisionLayer);
	}

	public void addProtectionLayer(String name, LayerDefinition protectionLayer)
			throws IOException {
		protectionMap.setCollisionData(protectionLayer);
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

	/**
	 * Determine if this zone overlaps an area in global coordinates.
	 * 
	 * @param area
	 *            The area (in global coordinate space).
	 * 
	 * @return <code>true</code> if the area overlaps.
	 */
	public boolean intersects(Rectangle2D area) {
		Rectangle2D zone = new Rectangle(x, y, getWidth(), getHeight());

		return zone.intersects(area);
	}

	/**
	 * Populate a zone based on it's map content.
	 * 
	 * TODO: XXX - This should be moved to the zone loader or something.
	 */
	public void populate(LayerDefinition objectsLayer) throws IOException {
		/* We build the layer data */
		objectsLayer.build();

		for (int yTemp = 0; yTemp < objectsLayer.getHeight(); yTemp++) {
			for (int xTemp = 0; xTemp < objectsLayer.getWidth(); xTemp++) {
				int value = objectsLayer.getTileAt(xTemp, yTemp);
				if (value > 0) {
					/*
					 * When the value is 0, it means that there is no tile at
					 * that point.
					 */
					TileSetDefinition tileset = objectsLayer.getTilesetFor(value);
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
	 * 
	 * 
	 */
	protected void createEntityAt(String clazz, int type, int x, int y) {
		logger.debug("creating " + clazz + ":" + type + " at " + x + "," + y);

		/*
		 * TODO: Refactor Stinky code... hardcoded... uuuaaarrgghh! No idea how
		 * to improve it...
		 */

		try {
			if (clazz.contains("logic/portal")) {
				switch (type) {
				case 0: /* Entry point */
				case 1: /* Zone change */
					setEntryPoint(x, y);
					break;

				case 5: /* one way portal destination */
				case 2: /* portal stairs up */
				case 3: /* portal stairs down */
					createLevelPortalAt(type, x, y);
					break;
				case 4: /* portal */
					break;
				case 6: /* door */
					break;
				}
			} else if (clazz.contains("sheep.png")) {
				Sheep sheep = new Sheep();
				sheep.setPosition(x, y);
				add(sheep);
			} else if (clazz.contains("logic/creature")) {
				// get the default EntityManager
				EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();

				// Is the entity a creature
				if (manager.isCreature(clazz, type)) {
					Creature creature = manager.getCreature(clazz, type);
					CreatureRespawnPoint point = new CreatureRespawnPoint(this,
							x, y, creature, 1);
					add(point);
				} else {
					logger.error("Unknown Entity (class/type: " + clazz + ":"
							+ type + ") at (" + x + "," + y + ") of " + getID()
							+ " found");
				}
			} else if (clazz.contains("logic/item")) {
				PassiveEntityRespawnPoint passiveEntityrespawnPoint = PassiveEntityRespawnPointFactory.create(
						clazz, type, getID(), x, y);
				if (passiveEntityrespawnPoint != null) {
					passiveEntityrespawnPoint.setPosition(x, y);
					add(passiveEntityrespawnPoint);

					// full fruits on server restart
					passiveEntityrespawnPoint.setToFullGrowth();
				}
			}
		} catch (Exception e) {
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

		for (IRPZone i : StendhalRPWorld.get()) {
			StendhalRPZone zone = (StendhalRPZone) i;

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

			Portal target = zone.getPortal(
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

	public boolean isInProtectionArea(Entity entity) {
		Rectangle2D area = entity.getArea(entity.getX(), entity.getY());
		return protectionMap.collides(area);
	}

	public boolean leavesZone(Entity entity, double x, double y) {
		Rectangle2D area = entity.getArea(x, y);
		return collisionMap.leavesZone(area);
	}

	public boolean simpleCollides(Entity entity, double x, double y) {
		Rectangle2D area = entity.getArea(x, y);
		return collisionMap.collides(area);
	}

	@Override
	public synchronized void add(RPObject object) {
		add(object, null);
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
	 *            The player who put the object on the ground, or null if the
	 *            object wasn't carried by a player before
	 * @throws RPObjectInvalidException
	 */
	public synchronized void add(RPObject object, Player player) {
		/*
		 * Assign [zone relative] ID info. TODO: Move up to MarauroaRPZone
		 */
		assignRPObjectID(object);
		super.add(object);

		/*
		 * This check is to avoid PassiveEntityRespawnPoint to make items grown
		 * and zone to make them disappear. FIXME: Change later to a proper
		 * event based system.
		 */
		if ((object instanceof Item) && (player != null)) {
			Item item = (Item) object;
			item.onPutOnGround(player);
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
			NPCList.get().add((SpeakerNPC) object);
		} else if (object instanceof Portal) {
			portals.add((Portal) object);
		}

		if (object instanceof NPC) {
			npcs.add((NPC) object);
		}

		if (object instanceof Entity) {
			((Entity) object).onAdded(this);
		}

		// TODO: Move up to MarauroaRPZone?
		StendhalRPWorld.get().requestSync(object);
	}

	@Override
	public synchronized RPObject remove(RPObject.ID id) {
		RPObject object = get(id);

		if (object instanceof Entity) {
			((Entity) object).onRemoved(this);
		}

		if (object instanceof NPC) {
			/*
			 * TODO: Move NPC (and all entity) handling to zone
			 * scoped/managed/dispatched.
			 */
			// npcs.remove((NPC) object);
			StendhalRPRuleProcessor.get().removeNPC((NPC) object);
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
			NPCList.get().remove(((SpeakerNPC) object).getName());
		} else if (object instanceof Portal) {
			portals.remove(object);
		}

		super.remove(id);

		if (object instanceof Item) {
			Item item = (Item) object;
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
	public synchronized RPObject remove(RPObject object) {
		if (object.isContained()) {
			// We modify the base container if the object change.

			// TODO: Remove? Isn't this the same as _in_ modify()?
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
	public boolean collidesOnLine(int x1, int y1, int x2, int y2) {

		Vector<Point> points = Line.renderLine(x1, y1, x2, y2);
		for (Point point : points) {
			if (collides((int) point.getX(), (int) point.getY())) {
				return true;
			}
		}
		return false;
	}

	public boolean collides(int x, int y) {
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
	 * @throws AttributeNotFoundException
	 */
	public synchronized boolean collides(Entity entity, double x, double y) {
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
	 * @throws AttributeNotFoundException
	 */
	public synchronized boolean collides(Entity entity, double x, double y,
			boolean checkObjects) {
		Rectangle2D area = entity.getArea(x, y);

		if (collisionMap.collides(area)) {
			return true;
		}

		if (checkObjects) {
			return collidesObjects(entity, area);
		}

		return false;
	}

	public boolean collidesObjects(Entity entity, Rectangle2D area) {
		// For every other object in this zone, check whether it's in the
		// way.
		return getCollidingObject(entity, area) != null;
	}

	private Entity getCollidingObject(Entity entity, Rectangle2D area) {
		Rectangle2D otherArea = new Rectangle.Double();
		for (RPObject other : objects.values()) {
			/*
			 * Ignore same object
			 */
			if (entity != other) {
				Entity otherEntity = (Entity) other;

				// Check if the objects overlap
				otherEntity.getArea(otherArea, otherEntity.getX(),
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
	 * 
	 * @return the entity at x,y or null if there is none
	 */
	public synchronized Entity getEntityAt(double x, double y) {
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
	public void notifyEntered(ActiveEntity entity, int newX, int newY) {
		Rectangle2D eArea;

		eArea = entity.getArea(newX, newY);

		for (MovementListener l : movementListeners) {
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
	public void notifyExited(ActiveEntity entity, int oldX, int oldY) {
		Rectangle2D eArea;

		eArea = entity.getArea(oldX, oldY);

		for (MovementListener l : movementListeners) {
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
	public void notifyMovement(ActiveEntity entity, int oldX, int oldY,
			int newX, int newY) {
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

		for (MovementListener l : movementListeners) {
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

	/**
	 * Register a movement listener for notification. Eventually create a
	 * macro-block hash to cut down on listeners to check.
	 * 
	 * @param listener
	 *            A movement listener to register.
	 */
	public void addMovementListener(MovementListener listener) {
		movementListeners.add(listener);
	}

	/**
	 * Unregister a movement listener from notification.
	 * 
	 * @param listener
	 *            A movement listener to unregister.
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
	 * This set is currently only used for plant growers, and these might be
	 * changed so that this set is no longer needed, so try to avoid using it.
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
	 * Can magic scrolls for teleportation be used in this zone?
	 * 
	 * @return true, if teleportion is possible, false otherwise
	 */
	public boolean isTeleportAllowed() {
		return teleportAllowed;
	}

	/**
	 * Sets the flag whether magic scrolls for teleportation may be uses in this
	 * zone.
	 * 
	 * @param teleportAllowed
	 *            true, if teleportion is possible, false otherwise
	 */
	public void setTeleportAllowed(boolean teleportAllowed) {
		this.teleportAllowed = teleportAllowed;
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
	public void setMoveToAllowed(boolean moveToAllowed) {
		this.moveToAllowed = moveToAllowed;

	}

	private int debugturn;

	@Override
	public void nextTurn() {
		super.nextTurn();

		debugturn++;

		if (Debug.SHOW_LIST_SIZES && (debugturn % 1000 == 0)) {
			StringBuffer os = new StringBuffer("Name: " + this.getID());
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
		for (NPC npc : npcs) {
			try {
				npc.logic();
			} catch (Exception e) {
				logger.error("Error in npc logic for zone " + getID().getID(), e);
			}
		}
	}
}
