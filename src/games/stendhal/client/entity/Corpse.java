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

//
//

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import games.stendhal.client.GameScreen;

/**
 * A corpse entity.
 */
public class Corpse extends PassiveEntity implements Inspectable {
	/**
	 * Content property.
	 */
	public static final Object	PROP_CONTENT		= new Object();

	/**
	 * The current content slot.
	 */
	private RPSlot		content;


	//
	// Corpse
	//

	/**
	 * Get the corpse contents.
	 *
	 * @return	The contents slot.
	 */
	public RPSlot getContent() {
		return content;
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
	 * Get the entity height.
	 *
	 * @return	The height.
	 */
	@Override
	protected double getHeight() {
		//TODO: Ugg - Don't couple visual size with logical size
		return (double) getView().getSprite().getHeight() / GameScreen.SIZE_UNIT_PIXELS;
	}


	/**
	 * Get the entity width.
	 *
	 * @return	The width.
	 */
	@Override
	protected double getWidth() {
		// TODO: Ugg - Don't couple visual size with logical size
		return (double) getView().getSprite().getWidth() / GameScreen.SIZE_UNIT_PIXELS;
	}


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
		} else {
			content = null;
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

		if (changes.hasSlot("content")) {
			content = changes.getSlot("content");
			fireChange(PROP_CONTENT);
		}
	}
}
