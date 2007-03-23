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
package games.stendhal.client;

import games.stendhal.client.entity.Blood;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityFabric;
import games.stendhal.client.entity.GoldSource;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.PassiveEntity;
import games.stendhal.client.entity.PlantGrower;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.Text;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/** This class stores the objects that exists on the World right now */
public class GameObjects implements Iterable<Entity> {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(GameObjects.class);

	private Map<RPObject.ID, Entity> objects;

	private List<Text> texts;

	private List<Text> textsToRemove;

	/**
	 * A list of all entities, sorted by the Z index, i.e. the order in which
	 * they should be drawn.
	 */
	private LinkedList<Entity> sortedObjects;

	private StaticGameLayers collisionMap;

	/**
	 * holds the reference to the singeton instance
	 */
	private static GameObjects instance = null;

	/**
	 * @param collisionMap
	 *            =layers that make floor and building
	 * @return singleton instance of Gameobjects
	 */
	public static GameObjects createInstance(StaticGameLayers collisionMap) {
		if (instance == null) {
			instance = new GameObjects(collisionMap);
		}
		return instance;
	}

	/**
	 * @return existing instance of Gameobjects
	 * @throws IllegalStateException
	 *             if instance has not been instanciated
	 */
	public static GameObjects getInstance() {
		if (instance == null) {
			throw new IllegalStateException();
		}

		return instance;
	}

	/**
	 * constructor
	 * 
	 * @param collisionMap
	 *            =layers that make floor and building
	 */
	private GameObjects(StaticGameLayers collisionMap) {
		objects = new HashMap<RPObject.ID, Entity>();

		texts = new LinkedList<Text>();
		textsToRemove = new LinkedList<Text>();
		sortedObjects = new LinkedList<Entity>();

		this.collisionMap = collisionMap;
	}

	public Iterator<Entity> iterator() {
		return sortedObjects.iterator();
	}

	public Sprite spriteType(RPObject object) {
		try {
			return EntityFabric.createEntity(object).getSprite();
		} catch (Exception e) {
			logger.error("cannot create sprite for object " + object, e);
			return null;
		}
	}

	private void sort() {
		Collections.sort(sortedObjects);
	}

	/** Add a new Entity to the game */
	public void add(RPObject object) throws AttributeNotFoundException {
		Log4J.startMethod(logger, "add");

		if (!object.has("server-only")) {
			Entity entity = EntityFabric.createEntity(object);

			entity.onAdded(object);

			objects.put(entity.getID(), entity);
			sortedObjects.add(entity);

			logger.debug("added " + entity);
		} else {
			logger.debug("Discarding object: " + object);
		}

		Log4J.finishMethod(logger, "add");
	}

	/** Modify a existing Entity so its propierties change */
	public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException {
		Log4J.startMethod(logger, "modifyAdded");
		Entity entity = objects.get(object.getID());
		if (entity != null) {
			entity.onChangedAdded(object, changes);

		}

		Log4J.finishMethod(logger, "modifyAdded");
	}

	public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException {
		Log4J.startMethod(logger, "modifyRemoved");
		Entity entity = objects.get(object.getID());
		if (entity != null) {
			entity.onChangedRemoved(object, changes);

		}

		Log4J.finishMethod(logger, "modifyRemoved");
	}

	public boolean has(Entity entity) {
		return objects.containsKey(entity.getID());
	}

	public Entity get(RPObject.ID id) {
		return objects.get(id);
	}

	/** Removes a Entity from game */
	public void remove(RPObject.ID id) {
		Log4J.startMethod(logger, "remove");
		logger.debug("removed " + id);

		Entity entity = objects.get(id);
		if (entity != null) {
			entity.onRemoved();

		}

		Entity object = objects.remove(id);
		sortedObjects.remove(object);
		Log4J.finishMethod(logger, "remove");
	}

	/** Removes all the object entities */
	public void clear() {
		Log4J.startMethod(logger, "clear");

		// invalidate all entity objects
		for (Iterator it = iterator(); it.hasNext();) {
			((Entity) it.next()).onRemoved();
		}

		objects.clear();
		sortedObjects.clear();
		texts.clear();
		Log4J.finishMethod(logger, "clear");
	}

	/** Removes all the text entities */
	public void clearTexts() {
		Log4J.startMethod(logger, "clearText");

		for (Iterator it = texts.iterator(); it.hasNext();) {
			textsToRemove.add((Text) it.next());
		}
		Log4J.finishMethod(logger, "clearText");

	}

	private boolean collides(Entity entity) {
		// TODO: Ugly, use similar method that server uses
		Rectangle2D area = entity.getArea();

		for (Entity other : sortedObjects) {
			if (!(other instanceof PassiveEntity || other instanceof Blood || other instanceof PlantGrower
			        || other instanceof GrainField || other instanceof Portal || other instanceof GoldSource)) {
				if (area.intersects(other.getArea()) && !entity.getID().equals(other.getID())) {
					entity.onCollideWith(other);
					return true;
				}
			}
		}

		return false;
	}

	/** Move objects based on the lapsus of time ellapsed since the last call. */
	public void move(long delta) {
		for (Entity entity : sortedObjects) {
			if (!entity.stopped()) {
				entity.move(delta);
				if (collisionMap.collides(entity.getArea())) {
					entity.onCollide((int) entity.getX(), (int) entity.getY());
					entity.move(-delta); // Undo move
				} else if (collides(entity)) {
					entity.move(-delta); // Undo move
				}
			}
		}
	}

	public void addText(Entity speaker, String text, Color color, boolean isTalking) {
		double x = speaker.getX();
		double y = speaker.getY();

		boolean found = true;

		while (found == true) {
			found = false;
			for (Text item : texts) {
				if ((item.getX() == x) && (item.getY() == y)) {
					found = true;
					y += 0.5;
					break;
				}
			}
		}

		Text entity = new Text(this, text, x, y, color, isTalking);
		texts.add(entity);
	}

	public void addText(Entity speaker, Sprite sprite, long persistTime) {
		Text entity = new Text(this, sprite, speaker.getX(), speaker.getY(), persistTime);
		texts.add(entity);
	}

	public void removeText(Text entity) {
		textsToRemove.add(entity);
	}

	public Entity at(double x, double y) {
		ListIterator<Entity> it = sortedObjects.listIterator(sortedObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getArea().contains(x, y)) {
				return entity;
			}
		}

		// Maybe user clicked outside char but on the drawed area of it
		it = sortedObjects.listIterator(sortedObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getDrawedArea().contains(x, y)) {
				return entity;
			}
		}

		return null;
	}

	public Entity at_undercreature(double x, double y) {
		ListIterator<Entity> it = sortedObjects.listIterator(sortedObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getArea().contains(x, y)) {
				if (entity.getType().compareTo("creature") == 0) {
					continue;
				}
				return entity;
			}
		}

		// Maybe user clicked outside char but on the drawed area of it
		it = sortedObjects.listIterator(sortedObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getDrawedArea().contains(x, y)) {
				if (entity.getType().compareTo("creature") == 0) {
					continue;
				}
				return entity;
			}
		}

		return null;
	}

	/** Draw all the objects in game */
	public void draw(GameScreen screen) {
		sort();

		for (Entity entity : sortedObjects) {
			entity.draw(screen);
		}
		// System.err.println(temp);
	}

	/** Draw the creature's Name/HP Bar */
	public void drawHPbar(GameScreen screen) {
		//sort();

		for (Entity entity : sortedObjects) {
			if (entity instanceof RPEntity) {
				RPEntity rpentity = (RPEntity) entity;
				rpentity.drawHPbar(screen);
			}
		}
		// System.err.println(temp);
	}

	public void drawText(GameScreen screen) {
		texts.removeAll(textsToRemove);
		textsToRemove.clear();

		try {
			for (Text entity : texts) {
				entity.draw(screen);
			}
		} catch (ConcurrentModificationException e) {
			logger.error("cannot draw text", e);
		}
	}

}
