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

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.events.RPObjectChangeListener;
import games.stendhal.client.sound.SoundSystem;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public abstract class Entity implements RPObjectChangeListener, Comparable<Entity> {

	String[] moveSounds=null;
	/** session wide instance identifier for this class
	 * TODO: get rid of this only used by Soundsystem
	 *  
	**/
public final byte[] ID_Token = new byte[0];

	/** The current x location of this entity */
	protected double x;

	/** The current y location of this entity */
	protected double y;

	/**
	 * The current change serial.
	 */
	protected int	changeSerial;

	/**
	 * The entity visibility.
	 */
	protected int		visibility;


	/** The arianne object associated with this game entity */
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
	 * defines the distance in which the entity is heard by Player
	 */
	protected double audibleRange = Double.POSITIVE_INFINITY;

	/**
	 * The "view" portion of an entity.
	 */
	protected Entity2DView	view;

	/**
	 * Quick work-around to prevent fireMovementEvent() from calling
	 * in onChangedAdded() from other initialize() hack.
	 * TODO: Need to fix it all to work right, but not now.
	 */
	protected boolean inAdd = false;


	Entity() {
		clazz = null;
		name = null;
		subclazz = null;
		title = null;
		type = null;
		x = 0.0;
		y = 0.0;

		changeSerial = 0;
	}


	/**
	 * Get the on-screen view representation.
	 * This will create the view if needed.
	 *
	 * @return	The view.
	 */
	public Entity2DView getView() {
		if(view == null) {
			view = createView();
			view.buildRepresentation();
		}

		return view;
	}


	/**
	 * Mark this entity changed in some way that might effect it's
	 * observed state .
	 */
	protected void changed() {
		changeSerial++;
	}


	/**
	 * Get the area the entity occupies.
	 *
	 * @return	A rectange (in world coordinate units).
	 */
	public Rectangle2D getArea() {
		return new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
	}


	/**
	 * Get the entity visibility.
	 *
	 * @return	The entity visibility (0 - 100).
	 */
	public int getVisibility() {
		return visibility;
	}


	/**
	 * Get an opaque value (using equality compare) to determine if
	 * the entity has changed. This will do until real notification
	 * can be implemented.
	 *
	 * @return	A value that should only be compared to other calls
	 *		to this function (for this entity instance).
	 */
	public int getChangeSerial() {
		return changeSerial;
	}


	/**
	 * Get the entity height.
	 *
	 * @return	The height.
	 */
	protected double getHeight() {
		return 1.0;
	}


	/** Returns the represented arianne object id */
	public RPObject.ID getID() {
		return rpObject != null ? rpObject.getID() : null;
	}


	/**
	 * Get the entity class.
	 *
	 * @return	The entity class.
	 */
	public String getEntityClass() {
		return clazz;
	}


	/**
	 * Get the name.
	 *
	 * @return	The name.
	 */
	public String getName() {
		return name;
	}


	/**
	 * Get the entity sub-class.
	 *
	 * @return	The entity sub-class.
	 */
	public String getEntitySubClass() {
		return subclazz;
	}


	/**
	 * Get the nicely formatted entity title.
	 *
	 * This searches the follow attribute order:
	 *	title, name (w/o underscore), type (w/o underscore).
	 *
	 * @return	The title, or <code>null</code> if unknown.
	 */
	public String getTitle() {
		if(title != null) {
			return title;
		} else if(name != null) {
			return name.replace('_', ' ');
		} else if(type != null) {
			return type.replace('_', ' ');
		} else {
			return null;
		}
	}


	/**
	 * Get the entity type.
	 *
	 * @return	The type.
	 */
	public String getType() {
		return type;
	}


	/**
	 * Get the X coordinate.
	 *
	 * @return	The X coordinate.
	 */
	public double getX() {
		return x;
	}


	/**
	 * Get the Y coordinate.
	 *
	 * @return	The Y coordinate.
	 */
	public double getY() {
		return y;
	}


	/**
	 * Get the RPObject this represents.
	 *
	 * @return	The RPObject.
	 */
	public RPObject getRPObject() {
		return rpObject;
	}


	/**
	 * Get the entity width.
	 *
	 * @return	The width.
	 */
	protected double getWidth() {
		return 1.0;
	}
	
	
	/**
	 * @param user the current player's character
	 * @return a double value representing the square of the distance in tiles
	 *  or Double.Positiveinfinity if User is null
	 */
	public double distanceToUser() {
		if (User.isNull()) return Double.POSITIVE_INFINITY;
		return (User.get().getX() - getX()) * (User.get().getX() - getX())
			+ (User.get().getY() - getY()) * (User.get().getY() - getY());
	}
	

	/**
	 * Returns the absolute world area (coordinates) to which audibility of
	 * entity sounds is confined. Returns <b>null</b> if confines do not exist
	 * (audible everywhere).
	 */
	public Rectangle2D getAudibleArea() {
		if (audibleRange == Double.POSITIVE_INFINITY) {
			return null;
		}

		double width = audibleRange * 2;
		return new Rectangle2D.Double(getX() - audibleRange, getY() - audibleRange, width, width);
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
	 * Process attribute changes that may affect positioning. This is
	 * needed because different entities may want to process coordinate
	 * changes more gracefully.
	 *
	 * @param	base		The previous values.
	 * @param	diff		The changes.
	 */
	protected void processPositioning(final RPObject base, final RPObject diff) {
		boolean moved = false;

		if (diff.has("x")) {
			int nx = diff.getInt("x");

			if(nx != x) {
				x = nx;
				moved = true;
			}
		}

		if (diff.has("y")) {
			int ny = diff.getInt("y");

			if(ny != y) {
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
	 * @param	x		The new X coordinate.
	 * @param	y		The new Y coordinate.
	 */
	protected void onPosition(double x, double y) {
	}


	/**
	 * DEPRECATED - Eventually will go away (when view used directly),
	 * but still needed for now.
	 */
	public void draw(final GameScreen screen) {
		view.draw(screen);
	}


	/**
	 * DEPRECATED - Eventually will go away (when moved to sub-class).
	 * but still needed for now.
	 */
	public boolean stopped() {
		return true;
	}


	/**
	 * returns the slot with the specified name or null if the entity does not
	 * have this slot
	 */
	public RPSlot getSlot(final String name) {
		if (rpObject.hasSlot(name)) {
			return rpObject.getSlot(name);
		}

		return null;
	}


	public ActionType defaultAction() {
		return ActionType.LOOK;
	}

	public final String[] offeredActions() {
		List<String> list = new ArrayList<String>();
		buildOfferedActions(list);
		if (defaultAction() != null) {
			list.remove(defaultAction().getRepresentation());
			list.add(0, defaultAction().getRepresentation());
		}

		/*
		 * Special admin options
		 */
		if (User.isAdmin()) {
			list.add(ActionType.ADMIN_INSPECT.getRepresentation());
			list.add(ActionType.ADMIN_DESTROY.getRepresentation());
			list.add(ActionType.ADMIN_ALTER.getRepresentation());
		}

		return list.toArray(new String[list.size()]);
	}

	protected void buildOfferedActions(final List<String> list) {
		list.add(ActionType.LOOK.getRepresentation());

	}

	public void onAction(final ActionType at, final String... params) {
		int id;
		RPAction rpaction;
		switch (at) {
			case LOOK:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				id = getID().getObjectID();

				if (params.length > 0) {
					rpaction.put("baseobject", params[0]);
					rpaction.put("baseslot", params[1]);
					rpaction.put("baseitem", id);
				} else {
					rpaction.put("target", id);
				}
				at.send(rpaction);
				break;
			case ADMIN_INSPECT:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				id = getID().getObjectID();
				rpaction.put("targetid", id);
				at.send(rpaction);
				break;
			case ADMIN_DESTROY:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				id = getID().getObjectID();
				rpaction.put("targetid", id);
				at.send(rpaction);
				break;
			case ADMIN_ALTER:
				id = getID().getObjectID();
				StendhalUI.get().setChatLine("/alter #" + id + " ");
				break;
			default:

				Log4J.getLogger(Entity.class).error(at.toString() + ": Action not processed");
				break;
		}

	}

	/**
	 * Checks if this entity should be drawn on top of the given entity, if the
	 * given entity should be drawn on top, or if it doesn't matter.
	 * 
	 * In the first case, this method returns a positive integer. In the second
	 * case, it returns a negative integer. In the third case, it returns 0.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * Note: this comparator imposes orderings that are inconsistent with
	 * equals().
	 * 
	 * @param other
	 *            another entity to compare this one to
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public int compareTo(final Entity other) {
		if (this.getY() < other.getY()) {
			// this entity is standing behind the other entity
			return -1;
		} else if (this.getY() > other.getY()) {
			// this entity is standing in front of the other entity
			return 1;
		} else {
			// one of the two entities is standing on top of the other.
			// find out which one.
			return getView().getZIndex() - other.getView().getZIndex();
		}
	}


	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected abstract Entity2DView createView();


	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
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
		 * Visibility
		 */
		if(object.has("visibility")) {
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

		// BUG: Work around for Bugs at 0.45
		inAdd = true;
		onChangedAdded(new RPObject(), object);
		inAdd = false;
	}


	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>true</code> the entity can not enter this
	 *		entity's area.
	 */
	public boolean isObstacle(Entity entity) {
		return (entity != this);
	}


	/**
	 * Release this entity. This should clean anything that isn't
	 * automatically released (such as unregister callbacks, cancel
	 * external operations, etc).
	 *
	 * @see-also	#initialize(RPObject)
	 */
	public void release() {
		SoundSystem.stopSoundCycle(ID_Token);
	}


	/**
	 * Update cycle.
	 *
	 * @param	delta		The time (in ms) since last call.
	 */
	public void update(final long delta) {
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * An object was added.
	 *
	 * @param	object		The object.
	 */
	public final void onAdded(final RPObject object) {
		// DEPRECATED - Moving to different listener. Use initialize().
	}


	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
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
			changed();
		}

		/*
		 * Name
		 */
		if (changes.has("name")) {
			name = changes.get("name");
			changed();
		}

		/*
		 * Sub-Class
		 */
		if (changes.has("subclass")) {
			subclazz = changes.get("subclass");
			changed();
		}

		/*
		 * Title
		 */
		if (changes.has("title")) {
			title = changes.get("title");
			changed();
		}

		/*
		 * Type
		 */
		if (changes.has("type")) {
			type = changes.get("type");
			changed();
		}

		/*
		 * Entity visibility
		 */
		if(changes.has("visibility")) {
			visibility = changes.getInt("visibility");
			changed();
		}


		/*
		 * Position changes
		 */
		processPositioning(object, changes);
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
	}


	/**
	 * The object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		/*
		 * Class
		 */
		if (changes.has("class")) {
			clazz = null;
			changed();
		}

		/*
		 * Name
		 */
		if (changes.has("name")) {
			name = null;
			changed();
		}

		/*
		 * Sub-Class
		 */
		if (changes.has("subclass")) {
			subclazz = null;
			changed();
		}

		/*
		 * Title
		 */
		if (changes.has("title")) {
			title = null;
			changed();
		}

		/*
		 * Type
		 */
		if (changes.has("type")) {
			type = null;
			changed();
		}

		/*
		 * Visibility
		 */
		if(changes.has("visibility")) {
			visibility = 100;
			changed();
		}
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
	}


	/**
	 * An object was removed.
	 *
	 * @param	object		The object.
	 */
	public final void onRemoved(final RPObject object) {
		// DEPRECATED - Moving to different listener. Use release().
	}
}
