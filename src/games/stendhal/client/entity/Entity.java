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
package games.stendhal.client.entity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import games.stendhal.client.listener.RPObjectChangeListener;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class Entity implements RPObjectChangeListener, IEntity {
	/** The current x location of this entity. */
	protected double x;

	/** The current y location of this entity. */
	protected double y;

	/**
	 * Amount of entity-to-entity resistance (0-100).
	 */
	private int resistance;

	/**
	 * Completely blocks walking over if set to <code>true</code>.
	 */
	private boolean walkBlocker = false;

	/**
	 * The entity visibility.
	 */
	private int visibility;

	/**
	 * Change listeners.
	 */
	private final List<EntityChangeListener<? extends IEntity>> changeListeners = new CopyOnWriteArrayList<EntityChangeListener<? extends IEntity>>();
	private final List<ContentChangeListener> contentChangeListeners = new CopyOnWriteArrayList<ContentChangeListener>();

	/** The arianne object associated with this game entity. */
	protected RPObject rpObject;

	/**
	 * The entity class.
	 */
	protected String clazz;

	/**
	 * The entity name.
	 */
	protected String name;

	/**
	 * The entity sub-class.
	 */
	private String subclazz;

	/**
	 * The entity title.
	 */
	protected String title;

	/**
	 * The entity type.
	 */
	protected String type;

	/**
	 * Quick work-around to prevent fireMovementEvent() from calling in
	 * onChangedAdded() from other initialize() hack.
	 * TODO: get rid off inAdd variable.
	 */
	protected boolean inAdd;

	/**
	 * The entity width.
	 */
	private double width;

	/**
	 * The entity height.
	 */
	private double height;

	/**
	 * Entity area rectangle. Kept in memory because it is the most allocated
	 * resource after the Graphics2D objects we can do nothing about.
	 */
	private Rectangle2D.Double area;

	public Entity() {
		clazz = null;
		name = null;
		subclazz = null;
		title = null;
		type = null;
		x = 0.0;
		y = 0.0;
	}

	//
	// Entity
	//

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#addChangeListener(games.stendhal.client.entity.EntityChangeListener)
	 */
	@Override
	public void addChangeListener(final EntityChangeListener<?> listener) {
		changeListeners.add(listener);
	}

	@Override
	public void addContentChangeListener(final ContentChangeListener listener) {
		contentChangeListeners.add(listener);
	}

	@Override
	public void removeContentChangeListener(final ContentChangeListener listener) {
		contentChangeListeners.remove(listener);
	}

	/**
	 * Fire change to all registered listeners.
	 *
	 * @param property
	 *            The changed property.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fireChange(final  Property property) {
		for (final EntityChangeListener l : changeListeners) {
			l.entityChanged(this, property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#isUser()
	 */
	@Override
	public boolean isUser() {
		return false;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getArea()
	 */
	@Override
	public Rectangle2D getArea() {
		if (area == null) {
			area = new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
		} else {
			area.x = getX();
			area.y = getY();
			area.width = getWidth();
			area.height = getHeight();
		}
		return area;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getVisibility()
	 */
	@Override
	public int getVisibility() {
		return visibility;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getHeight()
	 */
	@Override
	public double getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getID()
	 */
	@Override
	public final RPObject.ID getID() {
		if (rpObject == null) {
			return null;
		} else {
			return rpObject.getID();
		}
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getID()
	 */
	public final int getObjectID() {
		if (rpObject == null) {
			return -1;
		} else {
			return rpObject.getInt("id");
		}
	}


	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getEntityClass()
	 */
	@Override
	public String getEntityClass() {
		return clazz;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getEntitySubclass()
	 */
	@Override
	public String getEntitySubclass() {
		return subclazz;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getTitle()
	 */
	@Override
	public String getTitle() {
		if (title != null) {
			return title;
		} else if (name != null) {
			return name;
		} else if (type != null) {
			return type;
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getX()
	 */
	@Override
	public double getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getY()
	 */
	@Override
	public double getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getRPObject()
	 */
	@Override
	public RPObject getRPObject() {
		return rpObject;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getWidth()
	 */
	@Override
	public double getWidth() {
		return width;
	}


	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#isOnGround()
	 */
	@Override
	public boolean isOnGround() {
		return !rpObject.isContained();
	}

	/**
	 * Process attribute changes that may affect positioning. This is needed
	 * because different entities may want to process coordinate changes more
	 * gracefully.
	 *
	 * @param base
	 *            The previous values.
	 * @param diff
	 *            The changes.
	 */
	protected void processPositioning(final RPObject base, final RPObject diff) {
		boolean moved = false;

		if (diff.has("x")) {
			final int nx = diff.getInt("x");

			if (nx != x) {
				x = nx;
				moved = true;
			}
		}

		if (diff.has("y")) {
			final int ny = diff.getInt("y");

			if (ny != y) {
				y = ny;
				moved = true;
			}
		}

		if (moved) {
			onPosition(x, y);
		}
	}

	/**
	 * When the entity's position changed.
	 *
	 * @param x
	 *            The new X coordinate.
	 * @param y
	 *            The new Y coordinate.
	 */
	protected void onPosition(final double x, final double y) {
		fireChange(PROP_POSITION);
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getResistance()
	 */
	@Override
	public int getResistance() {
		return resistance;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getResistance(games.stendhal.client.entity.IEntity)
	 */
	@Override
	public int getResistance(final IEntity entity) {
		return ((getResistance() * entity.getResistance()) / 100);
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getSlot(java.lang.String)
	 */
	@Override
	public RPSlot getSlot(final String name) {
		if (rpObject.hasSlot(name)) {
			return rpObject.getSlot(name);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#initialize(marauroa.common.game.RPObject)
	 */
	@Override
	public void initialize(final RPObject object) {
		rpObject = object;

		/*
		 * Class
		 */
		if (object.has("class")) {
			clazz = object.get("class");
		} else {
			clazz = null;
		}

		/*
		 * Name
		 */
		if (object.has("name")) {
			name = object.get("name");
		} else {
			name = null;
		}

		/*
		 * Sub-Class
		 */
		if (object.has("subclass")) {
			subclazz = object.get("subclass");
		} else {
			subclazz = null;
		}

		/*
		 * Size
		 */
		if (object.has("height")) {
			height = object.getDouble("height");
		} else {
			height = 1.0;
		}

		if (object.has("width")) {
			width = object.getDouble("width");
		} else {
			width = 1.0;
		}

		/*
		 * Title
		 */
		if (object.has("title")) {
			title = object.get("title");
		} else {
			title = null;
		}

		/*
		 * Resistance
		 */
		if (object.has("resistance")) {
			resistance = object.getInt("resistance");
		} else {
			resistance = 0;
		}

		if (object.has("walk_blocker")) {
			walkBlocker = true;
		}

		/*
		 * Visibility
		 */
		if (object.has("visibility")) {
			visibility = object.getInt("visibility");
		} else {
			visibility = 100;
		}

		/*
		 * Coordinates
		 */
		if (object.has("x")) {
			x = object.getInt("x");
		} else {
			x = 0.0;
		}

		if (object.has("y")) {
			y = object.getInt("y");
		} else {
			y = 0.0;
		}

		/*
		 * Notify placement
		 */
		onPosition(x, y);
		/*
		 * Type
		 */
		type = object.getRPClass().getName();

		inAdd = true;
		onChangedAdded(new RPObject(), object);
		inAdd = false;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#isObstacle(games.stendhal.client.entity.IEntity)
	 */
	@Override
	public boolean isObstacle(final IEntity entity) {
		if (walkBlocker) {
			return true;
		}

		return ((entity != this) && (getResistance(entity) > 95));
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#release()
	 */
	@Override
	public void release() {
		// ignored
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#removeChangeListener(games.stendhal.client.entity.EntityChangeListener)
	 */
	@Override
	public void removeChangeListener(final EntityChangeListener<?> listener) {
		changeListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#update(int)
	 */
	@Override
	public void update(final int delta) {
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
	public final void onAdded(final RPObject object) {
		// DEPRECATED - Moving to different listener. Use initialize().
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
		if (inAdd) {
			return;
		}

		/*
		 * Class
		 */
		if (changes.has("class")) {
			clazz = changes.get("class");
			fireChange(PROP_CLASS);
		}

		/*
		 * Name
		 */
		if (changes.has("name")) {
			name = changes.get("name");
			fireChange(PROP_NAME);
			fireChange(PROP_TITLE);
		}

		/*
		 * Sub-Class
		 */
		if (changes.has("subclass")) {
			subclazz = changes.get("subclass");
			fireChange(PROP_CLASS);
		}

		/*
		 * Size
		 */
		boolean sizeChange = false;

		if (changes.has("width")) {
			width = changes.getDouble("width");
			sizeChange = true;
		}

		if (changes.has("height")) {
			height = changes.getDouble("height");
			sizeChange = true;
		}

		if (sizeChange) {
			fireChange(PROP_SIZE);
		}

		/*
		 * Title
		 */
		if (changes.has("title")) {
			title = changes.get("title");
			fireChange(PROP_TITLE);
		}


		/*
		 * State
		 */
		// TODO: move into child class?
		if (changes.has("state")) {
			fireChange(PROP_STATE);
		}

		/*
		 * Resistance
		 */
		if (changes.has("resistance")) {
			resistance = changes.getInt("resistance");
		}

		/*
		 * Entity visibility
		 */
		if (changes.has("visibility")) {
			visibility = changes.getInt("visibility");
			fireChange(PROP_VISIBILITY);
		}

		/*
		 * Content changes
		 */
		for (RPSlot slot : changes.slots()) {
			for (ContentChangeListener listener : contentChangeListeners) {
				listener.contentAdded(slot);
			}
		}

		/*
		 * Position changes
		 */
		processPositioning(object, changes);
	}

	/**
	 * The object removed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		/*
		 * Class
		 */
		if (changes.has("class")) {
			clazz = null;
			fireChange(PROP_CLASS);
		}

		/*
		 * Name
		 */
		if (changes.has("name")) {
			name = null;
			fireChange(PROP_NAME);
			fireChange(PROP_TITLE);
		}

		/*
		 * Sub-Class
		 */
		if (changes.has("subclass")) {
			subclazz = null;
			fireChange(PROP_CLASS);
		}

		/*
		 * Size
		 */
		boolean sizeChange = false;

		if (changes.has("width")) {
			width = 1.0;
			sizeChange = true;
		}

		if (changes.has("height")) {
			height = 1.0;
			sizeChange = true;
		}

		if (sizeChange) {
			fireChange(PROP_SIZE);
		}

		/*
		 * Title
		 */
		if (changes.has("title")) {
			title = null;
			fireChange(PROP_TITLE);
		}

		/*
		 * Resistance
		 */
		if (changes.has("resistance")) {
			resistance = 0;
		}

		/*
		 * Visibility
		 */
		if (changes.has("visibility")) {
			visibility = 100;
			fireChange(PROP_VISIBILITY);
		}

		/*
		 * Content changes
		 */
		for (RPSlot slot : changes.slots()) {
			for (ContentChangeListener listener : contentChangeListeners) {
				listener.contentRemoved(slot);
			}
		}
	}

	/**
	 * An object was removed.
	 *
	 * @param object
	 *            The object.
	 * @deprecated Moving to different listener. Use {@link #release()}.
	 */
	@Override
	@Deprecated
	public final void onRemoved(final RPObject object) {
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
	// Object
	//

	@Override
	public String toString() {
		final StringBuilder sbuf = new StringBuilder();

		sbuf.append(getClass().getName());

		/*
		 * Technically not all entities have 'name', but enough to debug most
		 * when used
		 */
		if ((rpObject != null) && rpObject.has("name")) {
			sbuf.append('[');
			sbuf.append(rpObject.get("name"));
			sbuf.append(']');
		}

		sbuf.append('@');
		sbuf.append(System.identityHashCode(this));

		return sbuf.toString();
	}


	/**
	 * gets the name of the cursor to use for this entity
	 *
	 * @return name of cursor
	 */
	@Override
	public String getCursor() {
		if (!rpObject.has("cursor")) {
			return null;
		}
		return rpObject.get("cursor");
	}

	/**
	 * Get identifier path for the entity.
	 *
	 * @return List of object identifiers and slot names that make up the
	 * 	complete path to the entity
	 */
	@Override
	public List<String> getPath() {
		LinkedList<String> path = new LinkedList<String>();
		RPObject object = getRPObject();
		while (object != null) {
			// Prepend the items; They'll be in a nice container-slot-content
			// order for the server
			path.add(0, Integer.toString(object.getID().getObjectID()));
			RPSlot slot = object.getContainerSlot();
			if (slot != null) {
				path.add(0, slot.getName());
			}
			object = object.getContainer();
		}
		return path;
	}
}
