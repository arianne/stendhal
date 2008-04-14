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

import games.stendhal.client.events.RPObjectChangeListener;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.client.update.Version;


import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class Entity implements RPObjectChangeListener {
	/**
	 * Animated property.
	 */
	public static final Property PROP_ANIMATED = new Property();

	/**
	 * Entity class/subclass property.
	 */
	public static final Property PROP_CLASS = new Property();

	/**
	 * Name property.
	 */
	public static final Property PROP_NAME = new Property();

	/**
	 * Position property.
	 */
	public static final Property PROP_POSITION = new Property();

	/**
	 * Size property.
	 */
	public static final Property PROP_SIZE = new Property();

	/**
	 * Title property.
	 */
	public static final Property PROP_TITLE = new Property();

	/**
	 * Type property.
	 */
	public static final Property PROP_TYPE = new Property();

	/**
	 * Visibility property.
	 */
	public static final Property PROP_VISIBILITY = new Property();

	/**
	 * an array of sounds. out of these randomnly chosen sounds are played while
	 * moving.
	 */
	protected String[] moveSounds;

	/**
	 * session wide instance identifier for this class.
	 * TODO: get rid of this only used by Soundsystem
	 * 
	 */
	public final byte[] ID_Token = new byte[0];

	/** The current x location of this entity. */
	protected double x;

	/** The current y location of this entity. */
	protected double y;

	/**
	 * The entity width.
	 */
	private double width;

	/**
	 * The entity height.
	 */
	private double height;

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
	 * Defines the distance in which the entity is heard by Player.
	 */
	protected double audibleRange = Double.POSITIVE_INFINITY;

	/**
	 * Quick work-around to prevent fireMovementEvent() from calling in
	 * onChangedAdded() from other initialize() hack. 
	 * <p> TODO: remove watch variable inAdd
	 */
	protected boolean inAdd;

	Entity() {
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

	/**
	 * Add a change listener.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addChangeListener(final EntityChangeListener listener) {
		EntityChangeListener[] newListeners;

		int len = changeListeners.length;

		newListeners = new EntityChangeListener[len + 1];
		System.arraycopy(changeListeners, 0, newListeners, 0, len);
		newListeners[len] = listener;

		changeListeners = newListeners;
	}

	/**
	 * Fill the action with the entity's target info. This will set the
	 * <code>baseobject</code>, <code>baseslot</code> and
	 * <code>baseitem</code> respective the <code>target</code> attributes
	 * for uncontained objects.
	 * 
	 * @param action
	 *            The RP action.
	 */
	public void fillTargetInfo(RPAction action) {
		int id = rpObject.getID().getObjectID();

		if (rpObject.isContained()) {
			action.put("baseobject",
					rpObject.getContainer().getID().getObjectID());
			action.put("baseslot", rpObject.getContainerSlot().getName());
			action.put("baseitem", id);
		} else {
			StringBuilder target;
			User user = User.get();

			String release = user != null ? User.get().getServerVersion()
					: null;

			// query the server version to see if it understands the new command
			// syntax with leading "#"
			if (release != null && Version.compare(release, "0.65.5") >= 0) {
				target = new StringBuilder("#");
			} else {
				target = new StringBuilder();
			}

			target.append(Integer.toString(id));

			action.put("target", target.toString());
		}
	}

	/**
	 * Fire change to all registered listeners.
	 * 
	 * @param property
	 *            The changed property.
	 */
	protected void fireChange(final  Property property) {
		EntityChangeListener[] listeners = changeListeners;

		for (EntityChangeListener l : listeners) {
			l.entityChanged(this, property);
		}
	}

	/**
	 * Get the area the entity occupies.
	 * 
	 * @return A rectange (in world coordinate units).
	 */
	public Rectangle2D getArea() {
		return new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
	}

	/**
	 * Get the entity visibility.
	 * 
	 * @return The entity visibility (0 - 100).
	 */
	public int getVisibility() {
		return visibility;
	}

	/**
	 * Get the entity height.
	 * 
	 * @return The height.
	 */
	public double getHeight() {
		return height;
	}

	/** @return the represented arianne object id. */
	public final RPObject.ID getID() {
		if (rpObject == null) {
			return null;
		} else {
			return rpObject.getID();
		}
	}

	/**
	 * Get the entity class.
	 * 
	 * @return The entity class.
	 */
	public String getEntityClass() {
		return clazz;
	}

	/**
	 * Get the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the entity sub-class.
	 * 
	 * @return The entity sub-class.
	 */
	public String getEntitySubClass() {
		return subclazz;
	}

	/**
	 * Get the nicely formatted entity title.
	 * 
	 * This searches the follow attribute order: title, name (w/o underscore),
	 * type (w/o underscore).
	 * 
	 * @return The title, or <code>null</code> if unknown.
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

	/**
	 * Get the entity type.
	 * 
	 * @return The type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the X coordinate.
	 * 
	 * @return The X coordinate.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get the Y coordinate.
	 * 
	 * @return The Y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get the RPObject this represents.
	 * 
	 * @return The RPObject.
	 */
	public RPObject getRPObject() {
		return rpObject;
	}

	/**
	 * Get the entity width.
	 * 
	 * @return The width.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Determine if this entity represents an instance of an RPClass.
	 * 
	 * @param clazz
	 *            The class name.
	 * 
	 * @return <code>true</code> if the entity represents that class, or a
	 *         subclass.
	 */
	public boolean isInstanceOf(String clazz) {
		return rpObject.getRPClass().subclassOf(clazz);
	}

	/**
	 * Determine if this entity is on the ground.
	 * 
	 * @return <code>true</code> if the entity is on the ground.
	 */
	public boolean isOnGround() {
		return !rpObject.isContained();
	}

	/**
	 * @return a double value representing the square of the distance in tiles
	 *         or Double.Positiveinfinity if User is null
	 */
	public double distanceToUser() {
		if (User.isNull()) {
			return Double.POSITIVE_INFINITY;
		}
		return (User.get().getX() - getX()) * (User.get().getX() - getX())
				+ (User.get().getY() - getY()) * (User.get().getY() - getY());
	}

	/**
	 * @return the absolute world area (coordinates) to which audibility of
	 * entity sounds is confined. Returns <b>null</b> if confines do not exist
	 * (audible everywhere).
	 */
	public Rectangle2D getAudibleArea() {
		if (audibleRange == Double.POSITIVE_INFINITY) {
			return null;
		}

		double tempWidth = audibleRange * 2;
		return new Rectangle2D.Double(getX() - audibleRange, getY()
				- audibleRange, tempWidth, tempWidth);
	}

	/**
	 * Sets the audible range as radius distance from this entity's position,
	 * expressed in coordinate units. This reflects an abstract capacity of this
	 * unit to emit sounds and influences the result of
	 * <code>getAudibleArea()</code>.
	 * 
	 * @param range
	 *            double audibility area radius in coordinate units
	 */
	public void setAudibleRange(final double range) {
		audibleRange = range;
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
			int nx = diff.getInt("x");

			if (nx != x) {
				x = nx;
				moved = true;
			}
		}

		if (diff.has("y")) {
			int ny = diff.getInt("y");

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

	/**
	 * Get the resistance this has on other entities (0-100).
	 * 
	 * @return The resistance.
	 */
	public int getResistance() {
		return resistance;
	}

	/**
	 * Get the amount of resistance between this and another entity (0-100).
	 * 
	 * @param entity
	 *            The entity to check against.
	 * 
	 * @return The effective resistance.
	 */
	public int getResistance(final Entity entity) {
		return ((getResistance() * entity.getResistance()) / 100);
	}

	/**
	 * Gets the slot specified by name.
	 * @param name of the slot
	 * 
	 * @return    the specified slot or <code>null</code> if the entity does not
	 * have this slot
	 */
	public RPSlot getSlot(final String name) {
		if (rpObject.hasSlot(name)) {
			return rpObject.getSlot(name);
		}

		return null;
	}

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @see-also #release()
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

	/**
	 * Determine if this is an obstacle for another entity.
	 * 
	 * @param entity
	 *            The entity to check against.
	 * 
	 * @return <code>true</code> the entity can not enter this entity's area.
	 */
	public boolean isObstacle(final Entity entity) {
		// >= 30% resistance = stall on client (simulates resistance)
		return ((entity != this) && (getResistance(entity) >= 30));
	}

	/**
	 * Release this entity. This should clean anything that isn't automatically
	 * released (such as unregister callbacks, cancel external operations, etc).
	 * 
	 * @see-also #initialize(RPObject)
	 */
	public void release() {
		SoundSystem.stopSoundCycle(ID_Token);
	}

	/**
	 * Remove a change listener.
	 * 
	 * @param listener
	 *            The listener.
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

	/**
	 * Update cycle.
	 * 
	 * @param delta
	 *            The time (in ms) since last call.
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
		StringBuilder sbuf = new StringBuilder();

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
