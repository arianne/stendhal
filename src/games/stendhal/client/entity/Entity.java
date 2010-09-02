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

import games.stendhal.client.listener.RPObjectChangeListener;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

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
	protected int resistance;

	/**
	 * The entity visibility.
	 */
	protected int visibility;

	/**
	 * Change listeners.
	 */
	protected EntityChangeListener[] changeListeners;

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
	protected String subclazz;

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



	public Entity() {
		clazz = null;
		name = null;
		subclazz = null;
		title = null;
		type = null;
		x = 0.0;
		y = 0.0;

		changeListeners = new EntityChangeListener[0];
	}

	//
	// Entity
	//

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#addChangeListener(games.stendhal.client.entity.EntityChangeListener)
	 */
	public void addChangeListener(final EntityChangeListener listener) {
		EntityChangeListener[] newListeners;

		final int len = changeListeners.length;

		newListeners = new EntityChangeListener[len + 1];
		System.arraycopy(changeListeners, 0, newListeners, 0, len);
		newListeners[len] = listener;

		changeListeners = newListeners;
	}



	/**
	 * Fire change to all registered listeners.
	 * 
	 * @param property
	 *            The changed property.
	 */
	protected void fireChange(final  Property property) {
		final EntityChangeListener[] listeners = changeListeners;
		
		for (final EntityChangeListener l : listeners) {
			l.entityChanged(this, property);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#isUser()
	 */
	public boolean isUser() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getArea()
	 */
	public Rectangle2D getArea() {
		return new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getVisibility()
	 */
	public int getVisibility() {
		return visibility;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getHeight()
	 */
	public double getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getID()
	 */
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
	public String getEntityClass() {
		return clazz;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getEntitySubClass()
	 */
	public String getEntitySubClass() {
		return subclazz;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getTitle()
	 */
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
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getX()
	 */
	public double getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getY()
	 */
	public double getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getRPObject()
	 */
	public RPObject getRPObject() {
		return rpObject;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getWidth()
	 */
	public double getWidth() {
		return width;
	}

	
	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#isOnGround()
	 */
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
	public int getResistance() {
		return resistance;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getResistance(games.stendhal.client.entity.IEntity)
	 */
	public int getResistance(final IEntity entity) {
		return ((getResistance() * entity.getResistance()) / 100);
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#getSlot(java.lang.String)
	 */
	public RPSlot getSlot(final String name) {
		if (rpObject.hasSlot(name)) {
			return rpObject.getSlot(name);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#initialize(marauroa.common.game.RPObject)
	 */
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
		 * Type
		 */
		if (object.has("type")) {
			type = object.get("type");
		} else {
			type = null;
		}

		/*
		 * Resistance
		 */
		if (object.has("resistance")) {
			resistance = object.getInt("resistance");
		} else {
			resistance = 0;
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

		inAdd = true;
		onChangedAdded(new RPObject(), object);
		inAdd = false;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#isObstacle(games.stendhal.client.entity.IEntity)
	 */
	public boolean isObstacle(final IEntity entity) {
		return ((entity != this) && (getResistance(entity) > 95));
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#release()
	 */
	public void release() {
		// ignored
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#removeChangeListener(games.stendhal.client.entity.EntityChangeListener)
	 */
	public void removeChangeListener(final EntityChangeListener listener) {
		EntityChangeListener[] newListeners;
		int idx;

		idx = changeListeners.length;

		while (idx-- != 0) {
			if (changeListeners[idx] == listener) {
				newListeners = new EntityChangeListener[changeListeners.length - 1];

				if (idx != 0) {
					System.arraycopy(changeListeners, 0, newListeners, 0, idx);
				}

				if (++idx != changeListeners.length) {
					System.arraycopy(changeListeners, idx, newListeners,
							idx - 1, changeListeners.length - idx);
				}

				changeListeners = newListeners;
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.entity.IEntity#update(int)
	 */
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
		 * Type
		 */
		if (changes.has("type")) {
			type = changes.get("type");
			fireChange(PROP_TYPE);
			fireChange(PROP_TITLE);
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
		if (!changes.slots().isEmpty()) {
			fireChange(PROP_CONTENT);
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
		 * Type
		 */
		if (changes.has("type")) {
			type = null;
			fireChange(PROP_TYPE);
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
		if (!changes.slots().isEmpty()) {
			fireChange(PROP_CONTENT);
		}
	}

	/**
	 * An object was removed.
	 * 
	 * @param object
	 *            The object.
	 * @deprecated Moving to different listener. Use release().
	 */
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

	
}
