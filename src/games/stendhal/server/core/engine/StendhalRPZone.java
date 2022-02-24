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

import static games.stendhal.common.constants.Actions.MOVE_CONTINUOUS;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import games.stendhal.common.CRC;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Debug;
import games.stendhal.common.Direction;
import games.stendhal.common.Line;
import games.stendhal.common.MathHelper;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.common.tiled.TileSetDefinition;
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
import games.stendhal.server.entity.mapstuff.area.WalkBlocker;
import games.stendhal.server.entity.mapstuff.area.WalkBlockerFactory;
import games.stendhal.server.entity.mapstuff.portal.OneWayPortalDestination;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPointFactory;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.TrainingDummy;
import games.stendhal.server.entity.npc.TrainingDummyFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.StringUtils;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.net.OutputSerializer;
import marauroa.common.net.message.TransferContent;
import marauroa.server.game.rp.MarauroaRPZone;

public class StendhalRPZone extends MarauroaRPZone {
	/**
	 * When calculating the danger level, short distance between creatures
	 * (weighted with creature levels) has an effect of adding to the total
	 * score. Adjust this constant to make the effect stronger or weaker. Value
	 * of 0 would mean that the danger level is equal to the level of the
	 * highest level creature.
	 */
	private static final double DANGER_WEIGHT_CREATURE_DENSITY = 1.0;

	private static final Pattern ZONE_NAME_PATTERN = Pattern.compile("^(-?[\\d]|int)_(.+)$");

	TeleportationRules teleRules = new TeleportationRules();

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPZone.class);

	private final List<TransferContent> contents;

	/** Data layer for zone attributes. */
	private ZoneAttributes attributes;

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
	private boolean interior = true;

	private int level;

	private int x;

	private int y;

	/** User representable name of the zone. */
	private final String readableName;

	/** Zones that some event types propagate to from this one. */
	private String associatedZones;

	/** Facing directions for portals. */
	private final int UP_FN = 8;
	private final int UP_FE = 9;
	private final int UP_FS = 10;
	private final int UP_FW = 11;
	private final int DOWN_FN = 12;
	private final int DOWN_FE = 13;
	private final int DOWN_FS = 14;
	private final int DOWN_FW = 15;
	private final int UP_FN_CM = 16;
	private final int UP_FE_CM = 17;
	private final int UP_FS_CM = 18;
	private final int UP_FW_CM = 19;
	private final int DOWN_FN_CM = 20;
	private final int DOWN_FE_CM = 21;
	private final int DOWN_FS_CM = 22;
	private final int DOWN_FW_CM = 23;
	private final List<Integer> stairsUp = new ArrayList<Integer>() {{
		add(2);
		add(UP_FN);
		add(UP_FE);
		add(UP_FS);
		add(UP_FW);
		add(UP_FN_CM);
		add(UP_FE_CM);
		add(UP_FS_CM);
		add(UP_FW_CM);
	}};
	private final List<Integer> stairsDown = new ArrayList<Integer>() {{
		add(3);
		add(DOWN_FN);
		add(DOWN_FE);
		add(DOWN_FS);
		add(DOWN_FW);
		add(DOWN_FN_CM);
		add(DOWN_FE_CM);
		add(DOWN_FS_CM);
		add(DOWN_FW_CM);
	}};

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
		String readable = createReadableName(name);
		if (!name.equals(readable)) {
			readableName = readable;
			// Ensure that the zone has attribute layer if it has a readable
			// name that differs from the internal name
			ZoneAttributes attr = new ZoneAttributes(this);
			setAttributes(attr);
		} else {
			readableName = null;
		}
	}

	public StendhalRPZone(final String name, final int width, final int height) {
		this(name);
		collisionMap.init(width, height);
	}

	public StendhalRPZone(final String name, final StendhalRPZone zone) {
		this(name);
		if (attributes != null) {
			// Try to match the attribute layer name with the rest of the zone
			attributes.setBaseName(zone.getName());
		}
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
		if (reference == null) {
			return null;
		}
		for (final Portal portal : portals) {
			if (reference.equals(portal.getIdentifier())) {
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
		// Remove old data by the same name if it exists
		Iterator<TransferContent> it = contents.iterator();
		while (it.hasNext()) {
			if (name.equals(it.next().name)) {
				logger.info("Replacing old '" + name + "' layer.");
				it.remove();
			}
		}

		final TransferContent content = new TransferContent();
		content.name = name;
		content.cacheable = true;
		logger.debug("Layer timestamp: " + Integer.toString(content.timestamp));
		content.data = byteContents;
		content.timestamp = CRC.cmpCRC(content.data);

		contents.add(content);
	}

	/**
	 * Resend the zone data to players on the zone. This is meant for situations
	 * where the map data changes. (Weather and lighting changes, and so on).
	 */
	public void notifyOnlinePlayers() {
		// Notify resident players about the changed weather
		if (!getPlayers().isEmpty()) {
			List<TransferContent> newContents = getContents();
			for (Player player : getPlayers()) {
				// Old clients do not understand content transfer that just
				// update the old map, and end up with no entities on the screen
				if (!player.isDisconnected() && player.isClientNewerThan("0.97")) {
					StendhalRPAction.transferContent(player, newContents);
				}
			}
		}
	}

	/**
	 * Set zone attributes that should be passed to the client.
	 *
	 * @param attr attributes
	 */
	public void setAttributes(ZoneAttributes attr) {
		if (readableName != null) {
			attr.put("readable_name", readableName);
		}
		attributes = attr;
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
	 * Calculate danger level for the zone, and store it in the data layer.
	 */
	public void calculateDangerLevel() {
		// Avoid divide by zero
		int maxLevel = 1;
		int levelSum = 1;
		for (CreatureRespawnPoint spawner : respawnPoints) {
			Creature creature = spawner.getPrototypeCreature();
			// Rare & abnormal creatures should not count.
			if (creature.isAbnormal()) {
				continue;
			}
			// Add 1, so that level 0 creatures do not get completely ignored.
			int level = creature.getLevel() + 1;
			maxLevel = Math.max(level, maxLevel);
			levelSum += level;
		}
		// Avoid divide by zero
		int area = getFreeArea() + 1;
		/*
		 * Use as the level of the highest level creature as the base, and
		 * adjust it upwards by creatures near the same level, inversely
		 * proportionally to the average distance between the creatures.
		 */
		double dangerLevel = maxLevel * (1 + DANGER_WEIGHT_CREATURE_DENSITY * (levelSum - maxLevel) / maxLevel / Math.sqrt(area)) - 1;
		/*
		 * Leave out if 0; the client does not need it, and it can save needing
		 * to send a data layer for zones that do not have any other attributes.
		 */
		if (maxLevel > 0) {
			if (attributes == null) {
				attributes = new ZoneAttributes(this);
			}
			attributes.put("danger_level", Double.toString(dangerLevel));
		}
	}

	/**
	 * Get the area of the zone, excluding static collisions.
	 *
	 * @return free area size
	 */
	private int getFreeArea() {
		int res = 0;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (!collides(x, y)) {
					res++;
				}
			}
		}
		return res;
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
		final int ONE_WAY_PORTAL_DESTINATION = 5;

		try {
			if (clazz.contains("logic/portal")) {
				if (stairsUp.contains(type) || stairsDown.contains(type) || (type == ONE_WAY_PORTAL_DESTINATION)) {
					createLevelPortalAt(type, x, y);
				} else {
					switch (type) {

					case ENTRY_POINT:
					case ZONE_CHANGE:
						setEntryPoint(x, y);
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
			} else if (clazz.contains("logic/training_dummy")) {
				final TrainingDummy dummy = TrainingDummyFactory.create(type);
				dummy.setPosition(x, y);
				add(dummy);
			} else if (clazz.contains("logic/area")) {
				// TODO: configure WalkBlocker & FlyOverArea on "collision" map layer

				final WalkBlocker blocker = WalkBlockerFactory.create(type);
				blocker.setPosition(x, y);
				add(blocker);
			}
		} catch (final RuntimeException e) {
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

			switch (type) {

			case UP_FN:
			case DOWN_FN:
				portal.setFaceDirection(Direction.UP);
				break;
			case UP_FE:
			case DOWN_FE:
				portal.setFaceDirection(Direction.RIGHT);
				break;
			case UP_FS:
			case DOWN_FS:
				portal.setFaceDirection(Direction.DOWN);
				break;
			case UP_FW:
			case DOWN_FW:
				portal.setFaceDirection(Direction.LEFT);
				break;
			case UP_FN_CM:
			case DOWN_FN_CM:
				portal.setFaceDirection(Direction.UP);
				portal.put(MOVE_CONTINUOUS, "");
				break;
			case UP_FE_CM:
			case DOWN_FE_CM:
				portal.setFaceDirection(Direction.RIGHT);
				portal.put(MOVE_CONTINUOUS, "");
				break;
			case UP_FS_CM:
			case DOWN_FS_CM:
				portal.setFaceDirection(Direction.DOWN);
				portal.put(MOVE_CONTINUOUS, "");
				break;
			case UP_FW_CM:
			case DOWN_FW_CM:
				portal.setFaceDirection(Direction.LEFT);
				portal.put(MOVE_CONTINUOUS, "");
				break;
			default:
				break;
			}
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
			if (stairsUp.contains(type)) {
				/* portal stairs up */
				if ((zone.getLevel() - getLevel()) != 1) {
					continue;
				}
			} else if (stairsDown.contains(type)) {
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
		if (attributes != null) {
			TransferContent attr = attributes.getContents();
			// Remove old attributes, if needed
			if (!contents.isEmpty() && (contents.get(0).name.equals(attr.name))) {
				contents.remove(0);
			}
			// Ensure the attributes comes first, so that the client has coloring
			// information
			contents.add(0, attr);
		}
		return contents;
	}

	public boolean isInProtectionArea(final Entity entity) {
		final Rectangle2D area = entity.getArea();
		return protectionMap.collides(area);
	}

	public boolean leavesZone(final Entity entity, final double x, final double y) {
		final Rectangle2D area = entity.getArea(x, y);
		return collisionMap.leavesZone(area);
	}

	public boolean simpleCollides(final Entity entity, final double x, final double y, final double w, final double h) {
		return collisionMap.collides(x, y, w, h);
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
			Player playerObject = (Player) object;
			players.add(playerObject);
			playersAndFriends.add(playerObject);
			/*
			 * super.add() clears the events, so this needs to be after it for
			 * the player to see the zone achievements. Also, Player.onAdded()
			 * sets the !visited slot, so this should be after it to have the
			 * achievement appear when the player enters the last missing zone.
			 */
			SingletonRepository.getAchievementNotifier().onZoneEnter(playerObject);
		} else if (object instanceof AttackableCreature) {
			playersAndFriends.add((AttackableCreature) object);
		} else if (object instanceof Sheep) {
			if (((Sheep) object).wasOwned()) {
				playersAndFriends.add((Sheep) object);
			}
		} else if (object instanceof SheepFood) {
			sheepFoods.add((SheepFood) object);
		} else if (object instanceof BabyDragon) {
			playersAndFriends.add((BabyDragon) object);
		} else if (object instanceof SpeakerNPC) {
			SingletonRepository.getNPCList().add((SpeakerNPC) object);
		} else if (object instanceof Portal) {
			portals.add((Portal) object);
		}

		if (object instanceof NPC) {
			npcs.add((NPC) object);
		}

		// TODO: Move up to MarauroaRPZone?
		SingletonRepository.getRPWorld().requestSync(object);
	}

	/**
	 * adds an RPEntity to the playersAndFriends list.
	 *
	 * @param object RPEntity
	 */
	public void addToPlayersAndFriends(RPEntity object) {
		if (!playersAndFriends.contains(object)) {
			playersAndFriends.add(object);
		}
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

		if (object instanceof ZoneEnterExitListener) {
			removeZoneEnterExitListener((ZoneEnterExitListener) object);
		}
		if (object instanceof MovementListener) {
			removeMovementListener((MovementListener) object);
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
			modify(object);

			if (object instanceof SpeakerNPC) {
				SingletonRepository.getNPCList().remove(((SpeakerNPC) object).getName());
			}

			if (object instanceof NPC) {
				npcs.remove(object);
			}

			final RPSlot slot = object.getContainerSlot();
			return slot.remove(object.getID());
		} else {
			return remove(object.getID());
		}
	}

	@Override
	public synchronized void modify(final RPObject object) {
		// We modify the base container if the object changes.
		super.modify(object.getBaseContainer());
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
		List<Point> points;
		// Always draw the line to the same direction, so that if A to B
		// collides, then so does B to A
		if ((x1 < x2) || ((x1 == x2) && (y1 < y2))) {
			points = Line.renderLine(x1, y1, x2, y2);
		} else {
			points = Line.renderLine(x2, y2, x1, y1);
		}
		for (final Point point : points) {
			if (collides((int) point.getX(), (int) point.getY())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks a single pair of coordinates for collision.
	 *
	 * @param x
	 * 		X-coordinate
	 * @param y
	 * 		Y-coordinate
	 * @return
	 * 		<code>true</code> if collision tile located at coordinates.
	 */
	public boolean collides(final int x, final int y) {
		return collisionMap.collides(x, y);
	}

	/**
	 * Checks an area for collision.
	 *
	 * @param shape
	 * 		Rectangle area.
	 * @return
	 * 		<code>true</code> if any collision tiles are found in the area.
	 */
	public boolean collides(final Rectangle2D shape) {
		return collisionMap.collides(shape);
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

		if (collisionMap.collides(x, y, entity.getWidth(), entity.getHeight())) {
			return true;
		}

		if (checkObjects) {
			Rectangle2D area = entity.getArea(x, y);
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
			// Ignore same object
			if (entity != other) {
				final Entity otherEntity = (Entity) other;

				// Check if the objects overlap
				if (area.intersects(otherEntity.getX(), otherEntity.getY(), otherEntity.getWidth(), otherEntity.getHeight())) {
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

			final Rectangle2D rect = otherEntity.getArea();
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

			final Rectangle2D rect = entity.getArea();
			if (rect.contains(x, y)) {
				entities.add(entity);
			}
		}

		return entities;
	}


	/**
	 * Finds all entities at the given coordinates.
	 * @param x coordinate
	 * @param y coordinate
	 * @return list of entities at (x, y)
	 */
	public synchronized <T extends Entity> List<T> getEntitiesAt(final double x, final double y, Class<T> clazz) {
		List<T> entities = new LinkedList<T>();

		for (final RPObject other : objects.values()) {
			final Entity entity = (Entity) other;
			if (!clazz.isInstance(entity)) {
				continue;
			}

			final Rectangle2D rect = entity.getArea();
			if (rect.contains(x, y)) {
				entities.add(clazz.cast(entity));
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

	public String getHumanReadableName() {
		final List<String> commonSuffixes = Arrays.asList(
				"n", "nw", "ne", "s", "sw", "se", "e", "w");

		//final StringBuilder sb = new StringBuilder();
		final List<String> prefix = new LinkedList<>();
		final List<String> suffix = new LinkedList<>();

		String level = null;
		for (final String word: getName().split("_")) {
			if (level == null) {
				level = word;
				continue;
			}

			if (commonSuffixes.contains(word)) {
				suffix.add(word);
				continue;
			}

			try {
				if (word.length() > 1) {
					Integer.parseInt(word.substring(1));
					suffix.add(word);
					continue;
				}
			} catch (final NumberFormatException e) {

			}

			prefix.add(word);
		}

		final StringBuilder sb = new StringBuilder(StringUtils.titleize(String.join(" ", prefix)));
		if (!suffix.isEmpty()) {
			sb.append(" " + String.join("", suffix).toUpperCase());
		}
		sb.append(", level " + level);

		return sb.toString();
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
			Rectangle2D area = l.getArea();
			if (area.intersects(eArea)) {
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
			Rectangle2D area = l.getArea();
			if (area.intersects(eArea)) {
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
		Rectangle2D oeArea;
		Rectangle2D neArea;
		boolean oldIn;
		boolean newIn;

		oeArea = entity.getArea(oldX, oldY);
		neArea = entity.getArea(newX, newY);

		for (final MovementListener l : movementListeners) {
			Rectangle2D area = l.getArea();

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

	public void notifyBeforeMovement(final ActiveEntity entity, final int oldX, final int oldY,
			final int newX, final int newY) {
		Rectangle2D neArea;
		boolean newIn;

		neArea = entity.getArea(newX, newY);

		for (final MovementListener l : movementListeners) {
			Rectangle2D area = l.getArea();

			newIn = area.intersects(neArea);

			if (newIn) {
				l.beforeMove(entity, this, oldX, oldY, newX, newY);
			}

		}
	}

	public void addZoneEnterExitListener(final ZoneEnterExitListener listener) {
		zoneListeners.add(listener);
	}

	public void removeZoneEnterExitListener(final ZoneEnterExitListener listener) {
		zoneListeners.remove(listener);
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
		return "zone " + zoneid + " at (" + x + "," + y + ", " + level + ") interior: " + isInterior();
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

	private boolean accessible;

	private String noItemMoveMessage;



	@Override
	@SuppressWarnings("unused")
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
		for (final Portal portal : portals) {
		    try {
		        portal.logic();
		    } catch (final Exception e) {
		        logger.error("Error in portal logic for zone " + getID().getID(), e);
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

	/**
	 * @return is this zone accessible by the public
	 */
	public boolean isPublicAccessible() {
		return accessible;
	}

	/**
	 * Sets the public accessibility of this zone
	 *
	 * @param accessible
	 */
	public void setPublicAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	/**
	 * Mappings for the zone names that would look weird with the dynamic
	 * translation.
	 */
	private static final Map<String, String> zoneNameMappings = new HashMap<String, String>();

	static {
		zoneNameMappings.put("0_athor_ship_w2", "on Athor ferry");
		zoneNameMappings.put("-1_athor_ship_w2", "on Athor ferry");
		zoneNameMappings.put("-2_athor_ship_w2", "on Athor ferry");
		zoneNameMappings.put("hell", "in Hell");
		zoneNameMappings.put("malleus_plain", "in Malleus Plain");
	}

	/**
	 * Translate zone name into a more readable form.
	 *
	 * @param zoneName
	 * @return translated zone name
	 */
	private static String translateZoneName(final String zoneName) {

		if (zoneNameMappings.get(zoneName) != null) {
			return zoneNameMappings.get(zoneName);
		}
		String result = "";
		final Matcher m = ZONE_NAME_PATTERN.matcher(zoneName);
		int levelValue = -1;
		if (m.matches()) {
			final String level = m.group(1);
			String remainder = m.group(2);
			if ("int".equals(level)) {
				return "inside a building in " + Grammar.makeUpperCaseWord(getInteriorName(zoneName));
			} else if (level.startsWith("-")) {
				try {
					levelValue = Integer.parseInt(level);
				} catch (final NumberFormatException e) {
					levelValue = 0;
				}
				if (levelValue < -2) {
					result = "deep below ground level at ";
				} else {
					result = "below ground level at ";
				}
			} else if (level.matches("^\\d")) {
				/* positive floor */
				try {
					levelValue = Integer.parseInt(level);
				} catch (final NumberFormatException e) {
					levelValue = 0;
				}
				if (levelValue != 0) {
					if (levelValue > 1) {
						result = "high above the ground level at ";
					} else {
						result = "above the ground level at ";
					}
				}
			}
			final StringBuilder sb = new StringBuilder();
			final String[] directions = new String[] { ".+_n\\d?e\\d?($|_).*",
					"north east ", "_n\\d?e\\d?($|_)", "_",
					".+_n\\d?w\\d?($|_).*", "north west ", "_n\\d?w\\d?($|_)",
					"_", ".+_s\\d?e\\d?($|_).*", "south east ",
					"_s\\d?e\\d?($|_)", "_", ".+_s\\d?w\\d?($|_).*",
					"south west ", "_s\\d?w\\d?($|_)", "_", ".+_n\\d?($|_).*",
					"north ", "_n\\d?($|_)", "_", ".+_s\\d?($|_).*", "south ",
					"_s\\d?($|_)", "_", ".+_w\\d?($|_).*", "west ",
					"_w\\d?($|_)", "_", ".+_e\\d?($|_).*", "east ",
					"_e\\d?($|_)", "_", };
			for (int i = 0; i < directions.length; i += 4) {
				if (remainder.matches(directions[i])) {
					sb.append(directions[i + 1]);
					remainder = remainder.replaceAll(directions[i + 2],
							directions[i + 3]);
				}
			}
			String direction = sb.toString();
			if (direction.length() > 0) {
				result += direction + "of ";
			} else if (levelValue == 0)  {
				// if level 0 and no other direction we need an extra in for grammar
				result =" in ";
			}
			// here we need to capitalise the city name
			result += Grammar.makeUpperCaseWord(remainder.replaceAll("_", " "));
		} else {
			logger.warn("no match: " + zoneName);
		}
		if ("".equals(result)) {
			return zoneName;
		} else {
			return result.trim();
		}
	}

	private static String getInteriorName(final String zoneName) {
		if (zoneName == null) {
			throw new IllegalArgumentException("zoneName is null");
		}
		final int start = zoneName.indexOf('_') + 1;
		int end = zoneName.indexOf('_', start);
		if (end < 0) {
			end = zoneName.length();
		}
		if (start > 0 && end > start) {
			return zoneName.substring(start, end);
		} else {
			return zoneName;
		}
	}

	public static String describe(final String zoneName) {
		return StendhalRPZone.translateZoneName(zoneName);
	}
	public String describe() {
		return StendhalRPZone.translateZoneName(this.getName());
	}

	/**
	 * Generate a precise zone name that can be shown to players (in client
	 * minimap). For vague zone names, use {@link #describe()}.
	 *
	 * @param zoneName game internal zone name
	 * @return human readable zone name
	 */
	private String createReadableName(String zoneName) {
		StringBuilder result = new StringBuilder();
		final Matcher m = ZONE_NAME_PATTERN.matcher(zoneName);
		if (m.matches()) {
			final String level = m.group(1);
			String remainder = m.group(2);

			final String[] directions = new String[] {
					".+_n(\\d?)e(\\d?)($|_).*", "N$1E$2", "_n\\d?e\\d?($|_)", "_",
					".+_n(\\d?)w(\\d?)($|_).*", "N$1W$2", "_n\\d?w\\d?($|_)", "_",
					".+_s(\\d?)e(\\d?)($|_).*", "S$1E$2 ", "_s\\d?e\\d?($|_)", "_",
					".+_s(\\d?)w(\\d?)($|_).*", "S$1W$2", "_s\\d?w\\d?($|_)", "_",
					".+_n(\\d?)($|_).*", "N$1", "_n\\d?($|_)", "_",
					".+_s(\\d?)($|_).*", "S$1", "_s\\d?($|_)", "_",
					".+_w(\\d?)($|_).*", "W$1", "_w\\d?($|_)", "_",
					".+_e(\\d?)($|_).*", "E$1", "_e\\d?($|_)", "_", };
			StringBuilder dirBuf = new StringBuilder();
			for (int i = 0; i < directions.length; i += 4) {
				Matcher match = Pattern.compile(directions[i]).matcher(remainder);
				if (match.matches()) {
					dirBuf.append(match.replaceAll(directions[i + 1]));
					remainder = remainder.replaceAll(directions[i + 2],
							directions[i + 3]);
				}
			}

			// here we need to capitalize the city name
			result.append(Grammar.makeUpperCaseWord(remainder.replaceAll("_", " ")));
			result.append(dirBuf);
			if ("int".equals(level)) {
				result.append(", interior");
			} else if (level.matches("^-?\\d")) {
				int levelValue = MathHelper.parseInt(level);
				if (levelValue != 0) {
					result.append(", level ");
					result.append(levelValue);
				}
			}
		} else if (zoneName.endsWith("tutorial_island")) {
			result.append("Tutorial Island");
		} else {
			// As of this writing (2014-01-15), the few zone names that do not
			// match produce good results with this.
			logger.info("no match: " + zoneName);
			return Grammar.makeUpperCaseWord(zoneName.replaceAll("_", " "));
		}
		if (result.length() == 0) {
			return null;
		} else {
			return result.toString().trim();
		}
	}

	/**
	 * Disabled movement of items in this zone.
	 *
	 * @param message in game error message
	 */
	public void setNoItemMoveMessage(String message) {
		this.noItemMoveMessage = message;
	}

	/**
	 * Gets the in game error message if movement of items is disabled in this zone.
	 *
	 * @return message in game error message or <code>null</code>
	 */
	public String getNoItemMoveMessage() {
		return this.noItemMoveMessage;
	}

	/**
	 * gets the zone attributes
	 *
	 * @return zone attributes
	 */
	public ZoneAttributes getAttributes() {
		return attributes;
	}

	/**
	 * Sets other zones that should receive certain events such as knocking on door.
	 *
	 * @param zones
	 *     Comma-separated string of zone names.
	 */
	public void setAssociatedZones(final String zones) {
		associatedZones = zones;
	}

	/**
	 * Gets other zones that should receive certain events such as knocking on door.
	 */
	public String getAssociatedZones() {
		return associatedZones;
	}

	/**
	 * Gets other zones that should receive certain events such as knocking on door.
	 */
	public List<String> getAssociatedZonesList() {
		if (associatedZones == null) {
			return new ArrayList<>();
		}

		return Arrays.asList(getAssociatedZones().split(","));
	}
}
