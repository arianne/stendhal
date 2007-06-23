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

	/** parent(container) of the moved object, may be null */
	private Entity parent;

	/** the slot this item is in. makes only sense when parent is != null */
	private String slot;

	/** constuctor to use when the item is on the ground */
	public MoveableEntityContainer(Entity entity) {
		this(entity, null, null);
	}

	/** constuctor to use when the item is inside a container */
	public MoveableEntityContainer(Entity entity, Entity parent, String slot) {
		this.entity = entity;
		this.parent = parent;
		this.slot = slot;
	}

	/** fills the action with the appropiate 'move from' parameters */
	public void fillRPAction(RPAction action) {
		if (parent != null) {
			// the item is inside a container
			action.put("baseobject", parent.getID().getObjectID());
			action.put("baseslot", slot);
		}

		action.put("baseitem", entity.getID().getObjectID());
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
		return (parent != null);
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
