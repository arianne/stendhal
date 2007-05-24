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

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Entity2DView;
import games.stendhal.client.entity.EntityFactory;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.events.RPObjectChangeListener;

import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import marauroa.common.Logger;

/** This class stores the objects that exists on the World right now */
public class GameObjects implements RPObjectChangeListener, Iterable<Entity> {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(GameObjects.class);

	private static final Comparator<Entity> entityComparator = new EntityComparator();

	private Map<FQID, Entity> objects;

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
		objects = new HashMap<FQID, Entity>();

		sortedObjects = new LinkedList<Entity>();

		this.collisionMap = collisionMap;
	}

	public Iterator<Entity> iterator() {
		return sortedObjects.iterator();
	}


	private void sort() {
		Collections.sort(sortedObjects, entityComparator);
	}

	public Entity get(RPObject object) {
		return objects.get(FQID.create(object));
	}

	public Entity get(RPObject.ID id) {
		return objects.get(new FQID(new RPObject.ID[] { id }));
	}

	/** Removes all the object entities */
	public void clear() {
		Log4J.startMethod(logger, "clear");

		// invalidate all entity objects
		Iterator<Entity> it = iterator();

		while(it.hasNext()) {
			Entity entity = it.next();
			entity.release();
		}

		objects.clear();
		sortedObjects.clear();

		GameScreen.get().clear();

		Log4J.finishMethod(logger, "clear");
	}

	public boolean collides(Entity entity) {
		Rectangle2D area = entity.getArea();

		// TODO: Ugly, use similar method that server uses
		if (collisionMap.collides(area)) {
			return true;
		}

		for (Entity other : sortedObjects) {
			if(other.isObstacle(entity) && area.intersects(other.getArea())) {
				return true;
			}
		}

		return false;
	}

	/** Move objects based on the lapsus of time ellapsed since the last call. */
	public void move(long delta) {
		for (Entity entity : sortedObjects) {
			entity.update(delta);
		}
	}

	public Entity at(double x, double y) {
		try {
			ListIterator<Entity> it = sortedObjects.listIterator(sortedObjects
					.size());
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

				if (entity.getView().getDrawnArea().contains(x, y)) {
					return entity;
				}
			}
		} catch (ConcurrentModificationException e) {
			// TODO: make a more failsafe/sophosticated solution 
			return null;
		}		
		return null;
	}

	public Entity at_undercreature(double x, double y) {
		ListIterator<Entity> it = sortedObjects.listIterator(sortedObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getArea().contains(x, y)) {
				if (entity.getType().equals("creature")) {
					continue;
				}
				return entity;
			}
		}

		// Maybe user clicked outside char but on the drawed area of it
		it = sortedObjects.listIterator(sortedObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getView().getDrawnArea().contains(x, y)) {
				if (entity.getType().equals("creature")) {
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
	}

	/** Draw the creature's Name/HP Bar */
	public void drawHPbar(GameScreen screen) {
		for (Entity entity : sortedObjects) {
			if (entity instanceof RPEntity) {
				RPEntity rpentity = (RPEntity) entity;
				rpentity.drawHPbar(screen);
			}
		}
	}


	/**
	 * Create an add an Entity. This does not add to the screen list.
	 *
	 * @param	object		The object.
	 *
	 * @return	An entity.
	 */
	protected Entity add(final RPObject object) {
		Entity entity = EntityFactory.createEntity(object);

		// Discard view for now - Just force it's creation
		entity.getView();

		objects.put(FQID.create(object), entity);

		return entity;
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * An object was added.
	 *
	 * @param	object		The object.
	 */
	public void onAdded(final RPObject object) {
		Log4J.startMethod(logger, "onAdded");

		if(object.getRPClass().subclassOf("entity")) {
			if(!object.has("type")) {
				logger.error("Entity without type: " + object);
				return;
			}

			if (!object.has("server-only")) {
				Entity entity = add(object);

				/*
				 * Only non-contained objects are on screen
				 */
				if(!object.isContained()) {
					sortedObjects.add(entity);
				}

				logger.debug("added " + entity);
			} else {
				logger.debug("Discarding object: " + object);
			}
		} else {
			logger.warn("Non-entity object added: " + object);
		}

		Log4J.finishMethod(logger, "onAdded");
	}


	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		Log4J.startMethod(logger, "onChangedAdded");

		Entity entity = objects.get(FQID.create(object));

		if (entity != null) {
			entity.onChangedAdded(object, changes);
		}

		Log4J.finishMethod(logger, "onChangedAdded");
	}


	/**
	 * A slot object added/changed attribute(s).
	 *
	 * @param	container	The base container object.
	 * @param	slotName	The container's slot name.
	 * @param	object		The base slot object.
	 * @param	changes		The slot changes.
	 */
	public void onChangedAdded(final RPObject container, final String slotName, final RPObject object, final RPObject changes) {
		Entity entity = objects.get(FQID.create(container));

		if (entity != null) {
			entity.onChangedAdded(container, slotName, object, changes);
		}
	}


	/**
	 * An object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		Log4J.startMethod(logger, "onChangedRemoved");

		Entity entity = objects.get(FQID.create(object));

		if (entity != null) {
			entity.onChangedRemoved(object, changes);
		}

		Log4J.finishMethod(logger, "onChangedRemoved");
	}


	/**
	 * A slot object removed attribute(s).
	 *
	 * @param	container	The base container object.
	 * @param	slotName	The container's slot name.
	 * @param	object		The base slot object.
	 * @param	changes		The slot changes.
	 */
	public void onChangedRemoved(final RPObject container, final String slotName, final RPObject object, final RPObject changes) {
		Entity entity = objects.get(FQID.create(container));

		if (entity != null) {
			entity.onChangedRemoved(container, slotName, object, changes);
		}
	}


	/**
	 * An object was removed.
	 *
	 * @param	object		The object.
	 */
	public void onRemoved(final RPObject object) {
		Log4J.startMethod(logger, "onRemoved");

		RPObject.ID id = object.getID();

		logger.debug("removed " + id);

		Entity entity = objects.remove(FQID.create(object));

		if (entity != null) {
			entity.release();
			sortedObjects.remove(entity);
		}

		Log4J.finishMethod(logger, "onRemoved");
	}

	//
	//

	/**
	 * A fully qualified ID. This will make an nested ID unique, even
	 * when in a slot tree.
	 */
	protected static class FQID {
		/**
		 * The ID path.
		 */
		protected RPObject.ID []	path;


		/**
		 * Create a fully qualified ID.
		 *
		 * @param	path		An ID path.
		 */
		public FQID(final RPObject.ID [] path) {
			this.path = path;
		}


		//
		// FQID
		//

		/**
		 * Create a FQID from an object tree.
		 *
		 * @param	object		The leaf object.
		 *
		 * @return	A FQID.
		 */
		public static FQID create(final RPObject object) {
			RPObject node;

			int len = 0;

			node = object;

			while(node != null) {
				len++;
				node = node.getContainer();
			}

			RPObject.ID [] path = new RPObject.ID[len];

			node = object;

			while(node != null) {
				path[--len] = node.getID();
				node = node.getContainer();
			}

			return new FQID(path);
		}


		/**
		 * Get the tree path of IDs.
		 *
		 * @return	The ID path.
		 */
		public RPObject.ID [] getPath() {
			return path;
		}


		//
		// Object
		//

		/**
		 * Check if this equals another object.
		 *
		 * @param	obj		The object to compare to.
		 */
		@Override
		public boolean equals(final Object obj) {
			if(!(obj instanceof FQID)) {
				return false;
			}

			RPObject.ID [] opath = ((FQID) obj).getPath();

			if(path.length != opath.length) {
				return false;
			}

			int i = path.length;

			while(i-- != 0) {
				if(!path[i].equals(opath[i])) {
					return false;
				}
			}

			return true;
		}


		/**
		 * Get the hash code.
		 *
		 * @return	The hash code.
		 */
		@Override
		public int hashCode() {
			int value = 0;

			for(RPObject.ID id : path) {
				value ^= id.hashCode();
			}

			return value;
		}
	}

	//
	//

	protected static class EntityComparator implements Comparator<Entity> {
		//
		// EntityComparator
		//

		public int compare(Entity2DView view1, Entity2DView view2) {
			int	rv;


			rv = view1.getZIndex() - view2.getZIndex();

			if(rv == 0) {
				Rectangle2D area1 = view1.getDrawnArea();
				Rectangle2D area2 = view2.getDrawnArea();

				rv = (int) ((area1.getMaxY() - area2.getMaxY()) * 10.0);

				if(rv == 0) {
					rv = (int) ((area1.getMinX() - area2.getMinX()) * 10.0);
				}
			}

			return rv;
		}


		//
		// Comparator
		//

		public int compare(Entity entity1, Entity entity2) {
			return compare(entity1.getView(), entity2.getView());
		}
	}
}
