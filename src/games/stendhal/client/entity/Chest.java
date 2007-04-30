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

import games.stendhal.client.gui.wt.EntityContainer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A chest entity.
 */
public class Chest extends AnimatedStateEntity implements Inspectable {
	/**
	 * Whether the chest is currently open.
	 */
	private boolean open;

	/**
	 * The slot content inspector.
	 */
	private Inspector inspector;

	/**
	 * The current content slot.
	 */
	private RPSlot content;

	/**
	 * The current content inspector.
	 */
	private EntityContainer wtEntityContainer;

	/** true means the user requested to open this chest */
	private boolean requestOpen;


	/**
	 * Create a chest entity.
	 */
	Chest() {
	}


	//
	// Inspectable
	//

	/**
	 * Set the content inspector for this entity.
	 *
	 * @param	inspector	The inspector.
	 */
	public void setInspector(final Inspector inspector) {
		this.inspector = inspector;
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new Chest2DView(this);
	}


	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	public void initialize(final RPObject object) {
		super.initialize(object);

		if (object.hasSlot("content")) {
			content = object.getSlot("content");
		}

		if (object.has("open")) {
			open = true;
			state = "open";
		} else {
			open = false;
			state = "close";
		}

		requestOpen = false;
	}


	/**
	 * Release this entity. This should clean anything that isn't
	 * automatically released (such as unregister callbacks, cancel
	 * external operations, etc).
	 *
	 * @see-also	#initialize(RPObject)
	 */
	public void release() {
		if (wtEntityContainer != null) {
			wtEntityContainer.destroy();
			wtEntityContainer = null;
		}

		super.release();
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

		if (changes.has("open")) {
			open = true;
			state = "open";

			// we're wanted to open this?
			if (requestOpen) {
				wtEntityContainer = inspector.inspectMe(this, content, wtEntityContainer);
				requestOpen = false;
			}

			changed();
		}

		if (changes.hasSlot("content")) {
			content = changes.getSlot("content");
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

		if (changes.has("open")) {
			open = false;
			state = "close";
			requestOpen = false;

			if (wtEntityContainer != null) {
				wtEntityContainer.destroy();
				wtEntityContainer = null;
			}

			changed();
		}
	}

	//
	//

	@Override
	public ActionType defaultAction() {
		return ActionType.LOOK;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);

		if (open) {
			list.add(ActionType.INSPECT.getRepresentation());
			list.add(ActionType.CLOSE.getRepresentation());
		} else {
			list.add(ActionType.OPEN.getRepresentation());
		}
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at =handleAction(action);
		switch (at) {
			case INSPECT:
				wtEntityContainer = inspector.inspectMe(this, content, wtEntityContainer);// inspect(this, content, 4, 5);
				break;
			case OPEN:
			case CLOSE:
				if (!open) {
					// If it was closed, open it and inspect it...
					requestOpen = true;
				}

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
}
