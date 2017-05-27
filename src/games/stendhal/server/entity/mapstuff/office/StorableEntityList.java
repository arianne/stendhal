/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.office;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPObject;

/**
 * a list of storable entities that can be accessed by a unique
 * identifier like a name.
 *
 * @author hendrik
 * @param <T> type of the storable entities to be managed by this list
 */
public abstract class StorableEntityList<T extends Entity> implements TurnListener {
	private StendhalRPZone zone;
	private Class<T> clazz;
	private Shape shape;
	private int notifyDelta;

	/**
	 * Creates a new StoreableEntityList.
	 *
	 * @param zone  zone to store the entities in
	 * @param clazz class object of the entities to manage
	 */
	StorableEntityList(final StendhalRPZone zone, final Class<T> clazz) {
		this.zone = zone;
		this.clazz = clazz;
	}

	public StorableEntityList(final StendhalRPZone zone, final Shape shape, final Class<T> clazz) {
	    this(zone, clazz);
	    this.shape = shape;
    }

	/**
     * Adds a storable entity.
     *
     * @param entity storable entity
     * @return true in case the entity was added successfully;
     * 				false in case no free spot for it was found
     */
	public boolean add(final T entity) {
		final boolean success = calculatePosition(entity);
		if (!success) {
			return false;
		}
		zone.add(entity);
		zone.storeToDatabase();
		return true;
	}

	/**
	 * calculates a free spot to place this entity into.
	 *
	 * @param entity entity
	 * @return true, in case a spot was found or this entity should
	 * 				not be place in the zone; false otherwise
	 */
	private boolean calculatePosition(final T entity) {
		if (shape == null) {
			return true;
		}

		final Rectangle rect = shape.getBounds();
		for (int x = rect.x; x < rect.x + rect.width; x++) {
			for (int y = rect.y; y < rect.y + rect.height; y++) {
				if (shape.contains(x, y)) {
					if (!zone.collides(entity, x, y)) {
						entity.setPosition(x, y);
						return true;
					}
				}
			}
		}

		return false;
    }

	/**
     * Returns the storable entity for the specified identifier.
     *
     * @param identifier name of entity
     * @return storable entity or <code>null</code> in case there is none
     */
    public T getByName(final String identifier) {
    	final List<T> entities = getList();
    	for (final T entity : entities) {
    		if (getName(entity).equals(identifier)) {
    			return entity;
    		}
    	}
    	return null;
    }

	/**
     * Removes all storable entities for this identifier.
     *
     * @param identifier name of entity
	 * @return if removed successfully
     */
    public boolean removeByName(final String identifier) {
    	final List<T> entities = getList();
    	boolean changed = false;
    	for (final T entity : entities) {
    		if (getName(entity).equals(identifier)) {
    			zone.remove(entity);
    			zone.storeToDatabase();
    			changed = true;
    		}
    	}
    	return changed;
    }

	/**
     * gets a list of storable entities from the zone storage. Note: This is only a
     * temporary snapshot, do not save it outside the scope of a method.
     *
     * @return List of storable entities.
     */
    protected List<T> getList() {
    	final List<T> res = new LinkedList<T>();
    	for (final RPObject object : zone) {
    		if (clazz.isInstance(object)) {
    			final T entity = clazz.cast(object);
    			res.add(entity);
    		}
    	}
    	return res;
    }

	protected void setupTurnNotifier(final int notifyDelta) {
		this.notifyDelta = notifyDelta;
		SingletonRepository.getTurnNotifier().notifyInSeconds(notifyDelta, this);
	}

    @Override
	public void onTurnReached(final int currentTurn) {
		boolean modified = false;
    	final List<T> entities = getList();
    	for (final T entity : entities) {
    		if (shouldExpire(entity)) {
    			zone.remove(entity);
    			modified = true;
    		}
    	}

    	if (modified) {
    		zone.storeToDatabase();
    	}

		SingletonRepository.getTurnNotifier().notifyInSeconds(notifyDelta, this);
    }

	protected abstract String getName(T entity);

	protected boolean shouldExpire(final T entity) {
		return false;
	}
}
