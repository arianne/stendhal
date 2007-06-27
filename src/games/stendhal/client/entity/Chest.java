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

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A chest entity.
 */
public class Chest extends Entity implements Inspectable {
	/**
	 * Content property.
	 */
	public static final Object	PROP_CONTENT		= new Object();

	/**
	 * Open state property.
	 */
	public static final Object	PROP_OPEN		= new Object();

	/**
	 * Whether the chest is currently open.
	 */
	private boolean open;

	/**
	 * The current content slot.
	 */
	private RPSlot content;


	/**
	 * Create a chest entity.
	 */
	Chest() {
	}


	//
	// Chest
	//

	/**
	 * Get the chest contents.
	 *
	 * @return	The contents slot.
	 */
	public RPSlot getContent() {
		return content;
	}


	/**
	 * Determine if the chest is open.
	 *
	 * @return	<code>true</code> if the chest is open.
	 */
	public boolean isOpen() {
		return open;
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
		((Inspectable) getView()).setInspector(inspector);
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

		if (object.hasSlot("content")) {
			content = object.getSlot("content");
		}

		open = object.has("open");
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
			fireChange(PROP_OPEN);
		}

		if (changes.hasSlot("content")) {
			content = changes.getSlot("content");
			fireChange(PROP_CONTENT);
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
			fireChange(PROP_OPEN);
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
}
