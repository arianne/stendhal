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

import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import games.stendhal.common.Direction;

/**
 * A door entity.
 */
public class Door extends Entity {
	/**
	 * Open state property.
	 */
	public final static Object	PROP_OPEN		= new Object();

	/**
	 * Orientation property.
	 */
	public final static Object	PROP_ORIENTATION	= new Object();

	/**
	 * Whether the door is open.
	 */
	private boolean open;

	/**
	 * The walk-through direction.
	 */
	private Direction orientation;


	/**
	 * Create a door entity.
	 */
	public Door() {
	}


	//
	// Door
	//

	/**
	 * Get the walk-through direction.
	 *
	 * @return	The orientation direction, or <code>null</code>.
	 */
	public Direction getOrientation() {
		return orientation;
	}


	/**
	 * Check if the door is open.
	 *
	 * @return	<code>true</code> if the door is open.
	 */
	public boolean isOpen() {
		return open;
	}


	//
	// Entity
	//

	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		/*
		 * Open state
		 */
		open = object.has("open");

		/*
		 * Orientation direction
		 */
		if (object.has("dir")) {
			orientation = Direction.build(object.getInt("dir"));
		} else {
			orientation = Direction.STOP;
		}
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		/*
		 * Open state
		 */
		if (changes.has("open")) {
			open = true;
			fireChange(PROP_OPEN);
		}

		/*
		 * Orientation direction
		 */
		if (changes.has("dir")) {
			orientation = Direction.build(changes.getInt("dir"));
			fireChange(PROP_ORIENTATION);
		}
	}


	/**
	 * The object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		/*
		 * Open state
		 */
		if (changes.has("open")) {
			open = false;
			fireChange(PROP_OPEN);
		}

		/*
		 * Orientation direction
		 */
		if (object.has("dir")) {
			orientation = Direction.STOP;
			fireChange(PROP_ORIENTATION);
		}
	}

	//
	//

	@Override
	public ActionType defaultAction() {
		if (open) {
			return ActionType.CLOSE;
		} else {
			return ActionType.OPEN;

		}
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at =handleAction(action);
		switch (at) {
			case OPEN:
			case CLOSE:
				RPAction rpaction = new RPAction();
				rpaction.put("type", at.toString());
				int id = getID().getObjectID();
				rpaction.put("target", id);
				at.send(rpaction);
				break;

			default:
				super.onAction(at, params);
				break;
		}
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);

		if (open) {
			list.add(ActionType.CLOSE.getRepresentation());
		} else {
			list.add(ActionType.OPEN.getRepresentation());

		}
	}
}
