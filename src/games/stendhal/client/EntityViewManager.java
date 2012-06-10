/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;

/**
 * Manager for EntityViews. Several methods specify from which threads they may
 * be called. The manager takes care of synchronizing the relevant data between
 * those.
 */
class EntityViewManager implements Iterable<EntityView<IEntity>> {
	/**
	 * How long the added/removed queues may grow before requesting processing
	 * them from the EntityViewMaganer. Normally they get processed when the
	 * views are sorted, but that does not happen if the client is minimized.
	 */
	private static final int QUEUE_PROCESS_FREQUENCY = 100;

	/**
	 * Comparator used to sort entities to display.
	 */
	private static final EntityViewComparator entityViewComparator = new EntityViewComparator();

	/**
	 * The entity views. Must be modified only in the even dispatch thread.
	 */
	private final List<EntityView<IEntity>> views = new ArrayList<EntityView<IEntity>>();

	/**
	 * The entity to view map. Must be modified only in the game loop thread.
	 */
	private final Map<IEntity, EntityView<IEntity>> entities = new HashMap<IEntity, EntityView<IEntity>>();

	/** EntityViews waiting to be removed. */
	private final Queue<EntityView<IEntity>> removed = new ConcurrentLinkedQueue<EntityView<IEntity>>();

	/** EntityViews waiting to be added. */
	private final Queue<EntityView<IEntity>> added = new ConcurrentLinkedQueue<EntityView<IEntity>>();

	/** User name. Used for grabbing user owned items hack. */
	private final String userName = StendhalClient.get().getCharacter();

	private final Runnable queueCleaner = new Runnable() {
		public void run() {
			processQueues();
		}
	};

	private final Runnable viewCleaner = new Runnable() {
		public void run() {
			views.clear();
		}
	};

	/**
	 * Add an entity. Must be called only from the game loop thread.
	 * 
	 * @param entity
	 * @return view belonging to the entity, or <code>null</code>
	 */
	EntityView<IEntity> addEntity(final IEntity entity) {
		final EntityView<IEntity> view = EntityViewFactory.create(entity);

		if (view != null) {
			entities.put(entity, view);
			addEntityView(view);
		}
		return view;
	}

	/**
	 * Add an entity view.
	 * 
	 * @param view
	 */
	private void addEntityView(EntityView<IEntity> view) {
		added.add(view);
		if ((added.size() % QUEUE_PROCESS_FREQUENCY) == 0) {
			SwingUtilities.invokeLater(queueCleaner);
		}
	}

	/**
	 * Clear entity view list.
	 */
	void clear() {
		// This gets called from the game loop thread.
		SwingUtilities.invokeLater(viewCleaner);
	}

	/**
	 * Get an entity view at a specific location.
	 * 
	 * @param x world x coordinate
	 * @param y world y coordinate
	 * @param sx pixel x coordinate
	 * @param sy pixel y coordinate
	 * 
	 * @return entity view, or <code>null</code> if no entity was found at the
	 *         location
	 */
	EntityView<IEntity> getEntityViewAt(final double x, final double y,
			final int sx, final int sy) {
		// Try the physical entity areas first
		EntityView<IEntity> view = getOccupyingEntityViewAt(x, y, false);
		if (view != null) {
			return view;
		}

		return getVisibleEntityViewAt(sx, sy, false);
	}

	/**
	 * Get a movable entity view at a specific location. Looks for physical
	 * entities occupying the area first.
	 * 
	 * @param x world x coordinate
	 * @param y world y coordinate
	 * @param sx pixel x coordinate
	 * @param sy pixel y coordinate
	 * 
	 * @return moveable entity view, or <code>null</code> if none was found
	 */
	EntityView<IEntity> getMovableEntityViewAt(final double x, final double y,
			final int sx, final int sy) {
		// Try the physical entity areas first
		EntityView<IEntity> view = getOccupyingEntityViewAt(x, y, true);
		if (view != null) {
			return view;
		}

		return getVisibleEntityViewAt(sx, sy, true);
	}

	/**
	 * Look for a physical entity at a specific location.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param movable if <code>true</code>, look for a movable entity
	 * 
	 * @return EntityView of an entity occupying (x, y), or <code>null</code> if
	 *         no suitable entity was found
	 */
	private EntityView<IEntity> getOccupyingEntityViewAt(final double x,
			final double y, boolean movable) {
		ListIterator<EntityView<IEntity>> it;

		it = views.listIterator(views.size());

		// A hack to grab bound items if they are under another player
		boolean deepFind = false;
		EntityView<IEntity> foundEntity = null;

		while (it.hasPrevious()) {
			final EntityView<IEntity> view = it.previous();
			IEntity entity = view.getEntity();

			if (movable && (entity instanceof Player)
					&& (!((Player) entity).isUser())
					&& entity.getArea().contains(x, y)) {
				// Looking for a moveable entity under another player. Try to
				// find an item belonging to the user
				deepFind = true;
			}

			if (!movable || view.isMovable()) {
				if (entity.getArea().contains(x, y)) {
					if (deepFind) {
						if (foundEntity == null) {
							// Store the first candidate in case we do not
							// find bound items
							foundEntity = view;
						}
						if (entity instanceof Item) {
							if (userName.equals(entity.getRPObject().get(
									"bound"))) {
								// Found an item bound to the user. This is
								// what we want to grab.
								return view;
							}
						}
					} else {
						return view;
					}
				}
			}
		}
		return foundEntity;
	}

	/**
	 * Get topmost EntityView whose visual area contains pixel coordinates (sx,
	 * sy)
	 * 
	 * @param sx x coordinate
	 * @param sy y coordinate
	 * @param movable if <code>true</code>, look only for movable entities
	 * 
	 * @return EntityView, or <code>null</code> if suitable view was not found
	 */
	private EntityView<IEntity> getVisibleEntityViewAt(final int sx,
			final int sy, boolean movable) {
		ListIterator<EntityView<IEntity>> it = views.listIterator(views.size());

		while (it.hasPrevious()) {
			final EntityView<IEntity> view = it.previous();

			if (view.getArea().contains(sx, sy)) {
				if (!movable || view.isMovable()) {
					return view;
				}
			}
		}

		return null;
	}

	/**
	 * Get EntityView iterator. This must be called only from the event dispatch
	 * thread. To have an up to date iterator, sort() must have been called at
	 * an appropriate point before retrieving the iterator.
	 */
	public Iterator<EntityView<IEntity>> iterator() {
		return views.iterator();
	}

	/**
	 * Process removed and added view queues. Must be called only from the EDT.
	 */
	private void processQueues() {
		/*
		 * Grab a snapshot of removed entities before processing added, to avoid
		 * a situation where an entity remove gets processed before the entity
		 * is added, leaving it as a ghost.
		 */
		Object[] toRemove = null;
		int removedSize = removed.size();
		if (removedSize > 0) {
			toRemove = new Object[removedSize];
			for (int i = 0; i < removedSize; i++) {
				EntityView<IEntity> view = removed.poll();
				view.release();
				toRemove[i] = view;
			}
			// There may be more now, but they get removed next time
		}

		EntityView<IEntity> toAdd = added.poll();
		while (toAdd != null) {
			views.add(toAdd);
			toAdd = added.poll();
		}
		if (toRemove != null) {
			for (Object view : toRemove) {
				views.remove(view);
			}
		}
	}

	/**
	 * Remove an entity. Must be called only from the game loop thread.
	 * 
	 * @param entity
	 */
	void removeEntity(final IEntity entity) {
		final EntityView<IEntity> view = entities.remove(entity);

		if (view != null) {
			removeEntityView(view);
		}
	}

	/**
	 * Remove an entity view.
	 * 
	 * @param view
	 */
	private void removeEntityView(EntityView<IEntity> view) {
		removed.add(view);
		if ((removed.size() % QUEUE_PROCESS_FREQUENCY) == 0) {
			SwingUtilities.invokeLater(queueCleaner);
		}
	}

	/**
	 * Reinitialize the views of all entities. Should be called only from the
	 * game loop thread.
	 */
	void resetViews() {
		// * Update the coloring of the entity views. *
		for (Entry<IEntity, EntityView<IEntity>> entry : entities.entrySet()) {
			// initialize() should trigger making a new image
			entry.getValue().initialize(entry.getKey());
		}
	}

	/**
	 * Sort the entity views. Must be called only from the event dispatch
	 * thread.
	 */
	void sort() {
		processQueues();
		Collections.sort(views, entityViewComparator);
	}

	/**
	 * Comparator for sorting the views.
	 */
	private static class EntityViewComparator implements
			Comparator<EntityView<IEntity>> {
		public int compare(final EntityView<IEntity> view1,
				final EntityView<IEntity> view2) {
			int rv;

			rv = view1.getZIndex() - view2.getZIndex();

			if (rv == 0) {
				final Rectangle area1 = view1.getArea();
				final Rectangle area2 = view2.getArea();

				rv = (area1.y + area1.height) - (area2.y + area2.height);

				if (rv == 0) {
					/*
					 * Quick workaround to stack items in the same order they
					 * were added.
					 * 
					 * TODO: stack items in the same order they were added on
					 * server side.
					 */
					rv = view1.getEntity().getID().getObjectID()
							- view2.getEntity().getID().getObjectID();
				}
			}

			return rv;
		}
	}
}