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

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.factory.EntityFactory;
import games.stendhal.client.events.EventDispatcher;
import games.stendhal.client.listener.RPObjectChangeListener;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * stores the objects that exists on the World right now.
 *
 */
public class GameObjects implements RPObjectChangeListener, Iterable<IEntity> {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(GameObjects.class);

	private final Map<FQID, IEntity> objects;

	private final StaticGameLayers collisionMap;

	/**
	 * holds the reference to the singleton instance.
	 */
	private static GameObjects instance;

	/**
	 * Objects to be notified about added and removed top level entities. The
	 * list is modified rarely, so it uses copy-on-write for thread safety.
	 */
	private final List<GameObjectListener> gameObjectListeners = new CopyOnWriteArrayList<GameObjectListener>();

	/**
	 * @param collisionMap
	 *            =layers that make floor and building
	 * @return singleton instance of GameOjects
	 */
	public static GameObjects createInstance(final StaticGameLayers collisionMap) {
		synchronized(GameObjects.class) {
			if (instance == null) {
				instance = new GameObjects(collisionMap);
			}
		}
		return instance;
	}

	/**
	 * @return existing instance of GameObjects
	 */
	public static GameObjects getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					"GameObject has not been initialized");
		}

		return instance;
	}

	/**
	 * Add a new game GameObjectListener.
	 *
	 * @param listener
	 */
	public void addGameObjectListener(GameObjectListener listener) {
		gameObjectListeners.add(listener);
	}

	/**
	 * Remove a GameObjectListener.
	 *
	 * @param listener
	 */
	public void removeGameObjectListener(GameObjectListener listener) {
		gameObjectListeners.remove(listener);
	}

	/**
	 * constructor.
	 *
	 * @param collisionMap
	 *            =layers that make floor and building
	 */
	private GameObjects(final StaticGameLayers collisionMap) {
		objects = new HashMap<FQID, IEntity>();

		this.collisionMap = collisionMap;
	}

	@Override
	public Iterator<IEntity> iterator() {
		return objects.values().iterator();
	}

	public IEntity get(final RPObject object) {
		return objects.get(FQID.create(object));
	}

	public IEntity get(final RPObject.ID id) {
		return objects.get(new FQID(id));
	}

	/**
	 * Removes all the object entities.
	 */
	void clear() {
		if (!objects.isEmpty()) {
			logger.debug("Game objects not empty!");

			// invalidate all entity objects
			final Iterator<IEntity> it = iterator();

			while (it.hasNext()) {
				final IEntity entity = it.next();
				logger.debug("Residual entity: " + entity);
				entity.release();
			}

			objects.clear();
		}
	}

	public boolean collides(final IEntity entity) {
		if (entity instanceof Player) {
			final Player player = (Player) entity;

			if (player.isGhostMode() || player.ignoresCollision()) {
				return false;
			}
		}

		if (entity instanceof NPC) {
			final NPC npc = (NPC) entity;

			if (npc.ignoresCollision()) {
				return false;
			}
		}

		final Rectangle2D area = entity.getArea();

		if (collisionMap.collides(area)) {
			return true;
		}

		for (final IEntity other : objects.values()) {
			if (other.isObstacle(entity) && area.intersects(other.getArea())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Update objects based on the lapsus of time ellapsed since the last call.
	 *
	 * @param delta
	 *            The time since last update (in ms).
	 */
	public void update(final int delta) {
		for (final IEntity entity : objects.values()) {
			entity.update(delta);
		}
	}

	/**
	 * Create an add an Entity. This does not add to the screen list.
	 *
	 * @param object
	 *            The object.
	 *
	 * @return An entity.
	 */
	private IEntity add(final RPObject object) {
		final IEntity entity = EntityFactory.createEntity(object);

		if (entity != null) {
			objects.put(FQID.create(object), entity);
		}

		return entity;
	}

	//
	// RPObjectChangeListener
	//

	/**
	 * An object was added.
	 *
	 * @param object
	 *            The object.
	 */
	@Override
	public void onAdded(final RPObject object) {
		if (!object.getRPClass().subclassOf("entity")) {
			logger.debug("Skipping non-entity object: " + object);
			return;
		}

		final IEntity entity = add(object);

		if (entity == null) {
			logger.error("No entity for: " + object);
		} else {
			if (entity.isOnGround()) {
				for (GameObjectListener listener : gameObjectListeners) {
					listener.addEntity(entity);
				}
			}

			logger.debug("added " + entity);
		}
	}

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		final IEntity iEntity = objects.get(FQID.create(object));

		if (iEntity instanceof Entity) {
			final Entity entity = (Entity) iEntity;

			entity.onChangedAdded(object, changes);

			EventDispatcher.dispatchEvents(changes, entity);
		}
	}


	/**
	 * An object removed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		final IEntity entity = objects.get(FQID.create(object));

		if (entity instanceof Entity) {
			((Entity) entity).onChangedRemoved(object, changes);
		}
	}

	/**
	 * An object was removed.
	 *
	 * @param object
	 *            The object.
	 */
	@Override
	public void onRemoved(final RPObject object) {
		final RPObject.ID id = object.getID();

		logger.debug("removed " + id);

		final IEntity entity = objects.remove(FQID.create(object));

		if (entity != null) {
			for (GameObjectListener listener : gameObjectListeners) {
				listener.removeEntity(entity);
			}
			entity.release();
		}
	}

	/**
	 * A slot object was added.
	 *
	 * @param object
	 *            The container object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	@Override
	public void onSlotAdded(final RPObject object, final String slotName,
			final RPObject sobject) {
	}

	/**
	 * A slot object added/changed attribute(s).
	 *
	 * @param object
	 *            The base container object.
	 * @param slotName
	 *            The container's slot name.
	 * @param sobject
	 *            The slot object.
	 * @param schanges
	 *            The slot object changes.
	 */
	@Override
	public void onSlotChangedAdded(final RPObject object,
			final String slotName, final RPObject sobject,
			final RPObject schanges) {
		final IEntity entity = objects.get(FQID.create(object));

		if (entity instanceof Entity) {
			((Entity) entity).onSlotChangedAdded(object, slotName, sobject, schanges);
		}
	}

	/**
	 * A slot object removed attribute(s).
	 *
	 * @param object
	 *            The base container object.
	 * @param slotName
	 *            The container's slot name.
	 * @param sobject
	 *            The slot object.
	 * @param schanges
	 *            The slot object changes.
	 */
	@Override
	public void onSlotChangedRemoved(final RPObject object,
			final String slotName, final RPObject sobject,
			final RPObject schanges) {
		final IEntity entity = objects.get(FQID.create(object));

		if (entity instanceof Entity) {
			((Entity) entity).onSlotChangedRemoved(object, slotName, sobject, schanges);
		}
	}

	/**
	 * A slot object was removed.
	 *
	 * @param object
	 *            The container object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	@Override
	public void onSlotRemoved(final RPObject object, final String slotName,
			final RPObject sobject) {
	}

	//
	//

	/**
	 * A fully qualified ID. This will make an nested ID unique, even when in a
	 * slot tree.
	 */
	private static class FQID {
		/**
		 * The object identification path.
		 */
		private Object[] path;

		/**
		 * Create a fully qualified ID.
		 *
		 * @param id
		 *            And object ID.
		 */
		private FQID(final RPObject.ID id) {
			this(new Object[] { Integer.valueOf(id.getObjectID()) });
		}

		/**
		 * Create a fully qualified ID.
		 * marked as private because of security reasons to avoid
		 * storing an array of externally mutable objects
		 *
		 * @param path
		 *            An identification path.
		 */
		private FQID(final Object[] path) { // NOPMD by martin on 24.11.10
			this.path = path;
		}

		//
		// FQID
		//

		/**
		 * Create a FQID from an object tree.
		 *
		 * @param object
		 *            An object.
		 *
		 * @return A FQID.
		 */
		private static FQID create(final RPObject object) {
			final LinkedList<Object> path = new LinkedList<Object>();
			RPObject node = object;

			while (true) {
				path.addFirst(Integer.valueOf(node.getID().getObjectID()));

				final RPSlot slot = node.getContainerSlot();

				if (slot == null) {
					break;
				}

				path.addFirst(slot.getName());
				node = node.getContainer();
			}

			return new FQID(path.toArray());
		}

		/**
		 * Get the tree path of object identifiers.
		 * marked as private because of security reasons to avoid
		 * exposing an array of mutable objects
		 *
		 * @return The identifier path.
		 */
		private Object[] getPath() {
			return path; // NOPMD by martin on 24.11.10
		}

		//
		// Object
		//

		/**
		 * Check if this equals another object.
		 *
		 * @param obj
		 *            The object to compare to.
		 */
		@Override
		public boolean equals(final Object obj) {
			boolean ret;

			if (obj instanceof FQID) {
				ret = Arrays.equals(getPath(), ((FQID) obj).getPath());
			} else {
				ret = false;
			}

			return ret;
		}

		/**
		 * Get the hash code.
		 *
		 * @return The hash code.
		 */
		@Override
		public int hashCode() {
			int value = 0;

			for (final Object obj : getPath()) {
				value ^= obj.hashCode();
			}

			return value;
		}

		/**
		 * Get the string representation.
		 *
		 * @return The string representation.
		 */
		@Override
		public String toString() {
			final StringBuilder sbuf = new StringBuilder();

			sbuf.append('[');
			sbuf.append(path[0]);

			for (int i = 1; i < path.length; i++) {
				sbuf.append(':');
				sbuf.append(path[i]);
			}

			sbuf.append(']');

			return sbuf.toString();
		}
	}

	/**
	 * Interface for objects that need to follow new top level entities being
	 * added to, or removed from the current zone.
	 */
	public interface GameObjectListener {
		/**
		 * Called when a top level entity is added to the user's zone.
		 *
		 * @param entity
		 */
		void addEntity(IEntity entity);

		/**
		 * Called when a top level entity is removed from the user's zone.
		 *
		 * @param entity
		 */
		void removeEntity(IEntity entity);
	}
}
