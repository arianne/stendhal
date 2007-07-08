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

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.wt.core.WtDraggable;

import java.awt.Graphics;
import java.awt.Point;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/** this container is used to drag the entities around */
public class MoveableEntityContainer implements WtDraggable {

	/** current x-pos of the dragged item */
	private int x;

	/** current y-pos of the dragged item */
	private int y;

	/** The moved object */
	private Entity entity;


	/**
	 * Create an entity drag container.
	 *
	 * @param	entity		The entity being moved.
	 */
	public MoveableEntityContainer(final Entity entity) {
		this.entity = entity;
	}


	//
	// MoveableEntityContainer
	//

	/** fills the action with the appropiate 'move from' parameters */
	protected void fillRPAction(RPAction action) {
		RPObject rpObject = entity.getRPObject();

		if(rpObject.isContained()) {
			// the item is inside a container
			action.put("baseobject", rpObject.getContainer().getID().getObjectID());
			action.put("baseslot", rpObject.getContainerSlot().getName());
		}

		action.put("baseitem", rpObject.getID().getObjectID());
	}


	/**
	 * Get the entity being moved.
	 *
	 * @return	The entity.
	 */
	public Entity getEntity() {
		return entity;
	}


	/**
	 * Determine if this is in a container slot.
	 *
	 * @return	<code>true</code> if the item is in a container.
	 */
	public boolean isContained() {
		return entity.getRPObject().isContained();
	}


	//
	// WtDraggable
	//

	/** drag started */
	public boolean dragStarted() {
		return true;
	}

	/** drag finished */
	public boolean dragFinished(Point p) {
		return true;
	}

	/** moved */
	public boolean dragMoved(Point p) {
		x = p.x;
		y = p.y;
		return true;
	}

	/**
	 * draws the entity
	 */
	public void drawDragged(Graphics g) {
		entity.getView().getSprite().draw(g, x, y);
	}
}
