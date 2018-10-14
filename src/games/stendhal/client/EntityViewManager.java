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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;

/**
 * Manager for EntityViews. Several methods specify from which threads they may
 * be called. The manager takes care of synchronizing the relevant data between
 * those.
 */
class EntityViewManager {
	private static final Logger logger = Logger.getLogger(EntityViewManager.class);

	/**
	 * Comparator used to sort entities to display.
	 */
	private static final EntityViewComparator entityViewComparator = new EntityViewComparator();

	/**
	 * The entity views. Modified in the game loop and read in the EDT.
	 * Remember to synchronize.
	 */
	private final List<EntityView<IEntity>> views = new ArrayList<EntityView<IEntity>>();
	/** Entities on the screen. */
	private final List<EntityView<IEntity>> visibleViews = new ArrayList<EntityView<IEntity>>();

	/**
	 * The entity to view map. May be accessed only in the game loop thread.
	 */
	private final Map<IEntity, EntityView<IEntity>> entities = new HashMap<IEntity, EntityView<IEntity>>();

	/** User name. Used for grabbing user owned items hack. */
	private final String userName = StendhalClient.get().getCharacter();

	/**
	 * Add an entity. Must be called only from the game loop thread.
	 *
	 * @param entity new entity
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
	 * @param view new view
	 */
	private void addEntityView(EntityView<IEntity> view) {
		synchronized (views) {
			views.add(view);
		}
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
		EntityView<IEntity> foundEntity = null;

		it = visibleViews.listIterator(visibleViews.size());

		// A hack to grab bound items if they are under another player
		boolean deepFind = false;

		while (it.hasPrevious()) {
			final EntityView<IEntity> view = it.previous();
			IEntity entity = view.getEntity();

			if (movable && (entity instanceof Player)
					&& (!((Player) entity).isUser())
					&& entity.getArea().contains(x, y)) {
				// Looking for a movable entity under another player. Try to
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
	 * sy).
	 *
	 * @param sx x coordinate
	 * @param sy y coordinate
	 * @param movable if <code>true</code>, look only for movable entities
	 *
	 * @return EntityView, or <code>null</code> if suitable view was not found
	 */
	private EntityView<IEntity> getVisibleEntityViewAt(final int sx,
			final int sy, boolean movable) {
		ListIterator<EntityView<IEntity>> it = visibleViews.listIterator(visibleViews.size());

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
	 * Remove an entity. Must be called only from the game loop thread.
	 *
	 * @param entity removed entity
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
	 * @param view removed view
	 */
	private void removeEntityView(EntityView<IEntity> view) {
		synchronized (views) {
			views.remove(view);
			view.release();
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
	 * Prepare the entity views for drawing. Must be called only from the event
	 * dispatch thread.
	 *
	 * @param area visible area
	 * @param setVisibleArea inform the entities about the visible area. This
	 * 	should be only done when the whole screen is drawn
	 */
	void prepareViews(Rectangle area, boolean setVisibleArea) {
		visibleViews.clear();
		synchronized (views) {
			for (EntityView<IEntity> view : views) {
				view.applyChanges();
				if (area.intersects(view.getArea())) {
					visibleViews.add(view);
					if (setVisibleArea) {
						view.setVisibleScreenArea(area);
					}
				}
			}
		}

		Collections.sort(visibleViews, entityViewComparator);
	}

	/**
	 * Draw entities.
	 *
	 * @param g graphics
	 */
	void draw(Graphics2D g) {
		for (final EntityView<IEntity> view : visibleViews) {
			try {
				view.draw(g);
			} catch (RuntimeException e) {
				logger.error(e, e);
			}
		}
	}

	/**
	 * Draw the top parts of the entities.
	 *
	 * @param g graphics
	 */
	void drawTop(Graphics2D g) {
		for (final EntityView<IEntity> view : visibleViews) {
			try {
				view.drawTop(g);
			} catch (RuntimeException e) {
				logger.error(e, e);
			}
		}
	}

	/**
	 * Comparator for sorting the views.
	 */
	private static class EntityViewComparator implements
			Comparator<EntityView<IEntity>> {
		@Override
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
