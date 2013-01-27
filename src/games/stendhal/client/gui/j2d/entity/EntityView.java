/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.Inspectable;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.awt.Graphics2D;
import java.awt.Rectangle;

//
//

/**
 * The view of an entity.
 * 
 * @param <T> type of the entity
 */
public interface EntityView<T extends IEntity> extends Inspectable {
	/**
	 * Get the list of actions.
	 * 
	 * @return The list of actions.
	 */
	public String[] getActions();

	/**
	 * Get the view's entity.
	 * 
	 * @return The view's entity.
	 */
	public T getEntity();

	/**
	 * Determine if this entity can be moved (e.g. via dragging).
	 * 
	 * @return <code>true</code> if the entity is movable.
	 */
	public boolean isMovable();

	/**
	 * Perform the default action.
	 */
	public void onAction();

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	public void onAction(ActionType at);

	/**
	 * Perform the default action unless it is not safe.
	 *
	 * @return <code>true</code> if the action was performed, <code>false</code> if nothing was done
	 */
	public boolean onHarmlessAction();

	/**
	 * Release any view resources. This view should not be used after this is
	 * called.
	 */
	public void release();

	public Rectangle getArea();
	
	public void draw(final Graphics2D g2d);

	public int getZIndex();

	public void drawTop(Graphics2D g2d);

	public void setContained(boolean b);

	public void initialize(T entity);

	/**
	 * is this entity interactive so that the player can click or move it?
	 * 
	 * @return true if the player can interact with it, false otherwise.
	 */
	boolean isInteractive();

	/**
	 * gets the mouse cursor image to use for this entity.
	 *
	 * @return StendhalCursor
	 */
	StendhalCursor getCursor();
	/**
	 * Update the view with the changes in entity.
	 */
	void applyChanges();
}
