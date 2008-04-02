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

import games.stendhal.client.GameScreen;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.IDraggable;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.j2d.entity.StackableItem2DView;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/** this container is used to drag the entities around .*/
public class MoveableEntityContainer implements IDraggable {

	/** current x-pos of the dragged item. */
	private int x;

	/** current y-pos of the dragged item. */
	private int y;

	/** The moved object. */
	private Entity entity;

	/**
	 * The entity view.
	 */
	private Entity2DView view;

	/**
	 * Create an entity drag container.
	 * 
	 * @param entity
	 *            The entity being moved.
	 */
	public MoveableEntityContainer(final Entity entity) {
		this.entity = entity;
	}

	//
	// MoveableEntityContainer
	//

	/** Fills the action with appropriate 'move from' parameters. 
	 * @param action to be filled*/
	public void fillRPAction(RPAction action) {
		RPObject rpObject = entity.getRPObject();

		if (rpObject.isContained()) {
			// the item is inside a container
			action.put("baseobject", rpObject.getContainer().getID().getObjectID());
			action.put("baseslot", rpObject.getContainerSlot().getName());
		}

		action.put("baseitem", rpObject.getID().getObjectID());
	}

	/**
	 * Get the entity being moved.
	 * 
	 * @return The entity.
	 */
	public Entity getEntity() {
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

	/** drag started. */
	public boolean dragStarted() {
		view = GameScreen.get().createView(entity);

		if (view != null) {
			view.setContained(true);

			/*
			 * Hide quantity until it can be made context sensitive to drag
			 * modifiers.
			 * 
			 * TODO: Find a better way
			 */
			if (view instanceof StackableItem2DView) {
				((StackableItem2DView) view).setShowQuantity(false);
			}
		}

		return true;
	}

	/** drag finished. */
	public boolean dragFinished(Point p) {
		if (view != null) {
			view.release();
			view = null;
		}

		return true;
	}

	/** moved. */
	public void dragMoved(Point p) {
		x = p.x;
		y = p.y;
	}

	/**
	 * Get the image size of the dragged object.
	 * 
	 * @return The image size.
	 */
	public Dimension getSize() {
		if (view != null) {
			return new Dimension(view.getWidth(), view.getHeight());
		} else {
			return null;
		}
	}

	/**
	 * draws the entity.
	 * @param g the Graphic context to draw to.
	 */
	public void drawDragged(Graphics g, Dimension size) {
		if (view != null) {
			Graphics2D cg = (Graphics2D) g.create();
			Rectangle area = view.getArea();

			cg.translate(x, y);

			if (area.height > size.width || area.height > size.height) {
				 // scale the image down to cursor size
				int xMax = Math.max(area.height, size.width);
				int yMax = Math.max(area.width, size.width);

				cg.scale((double)size.width/xMax, (double)size.height/yMax);
			}

			view.draw(cg);

			cg.dispose();
		}
	}

}
