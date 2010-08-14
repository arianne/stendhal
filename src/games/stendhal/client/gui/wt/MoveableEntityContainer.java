/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.wt;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.wt.core.WtDraggable;

import java.awt.Graphics;
import java.awt.Point;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/** 
 * A container for packing dragged entities so that WtDropTargets can receive
 * them 
 */
public class MoveableEntityContainer implements WtDraggable {
	/** The moved object. */
	private final IEntity entity;

	/**
	 * Create an entity drag container.
	 * 
	 * @param entity
	 *            The entity being moved.
	 */
	public MoveableEntityContainer(final IEntity entity) {
		this.entity = entity;
	}

	//
	// MoveableEntityContainer
	//

	/**
	 * Fills the action with appropriate 'move from' parameters.
	 * 
	 * @param action
	 *            to be filled
	 */
	protected void fillRPAction(final RPAction action) {
		final RPObject rpObject = entity.getRPObject();

		if (rpObject.isContained()) {
			// the item is inside a container
			action.put("baseobject", rpObject.getContainer().getID()
					.getObjectID());
			action.put("baseslot", rpObject.getContainerSlot().getName());
		}

		action.put("baseitem", rpObject.getID().getObjectID());
	}

	/**
	 * Get the entity being moved.
	 * 
	 * @return The entity.
	 */
	public IEntity getEntity() {
		return entity;
	}

	/**
	 * Determine if this is in a container slot.
	 * 
	 * @return <code>true</code> if the item is in a container.
	 */
	public boolean isContained() {
		return entity.getRPObject().isContained();
	}

	//
	// WtDraggable
	//

	/**
	 * drag started.
	 * 
	 * @return true
	 */
	public boolean dragStarted() {
		return true;
	}

	/**
	 * drag finished.
	 * 
	 * @param p
	 * @return true
	 */
	public boolean dragFinished(final Point p) {
		return true;
	}

	/**
	 * moved.
	 * 
	 * @param p
	 * @return true
	 */
	public boolean dragMoved(final Point p) {
		return true;
	}

	/**
	 * draws the entity.
	 * 
	 * @param g
	 *            the Graphic context to draw to.
	 */
	public void drawDragged(final Graphics g) {
		// Drawing not needed; it is handled by the drag layer
	}
}
