/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.Point;

import games.stendhal.client.entity.IEntity;

public interface DropTarget {
	/**
	 * Drop an entity at a given location. Called when dragging ends.
	 *
	 * @param entity dropped entity
	 * @param amount number of dropped entities. -1 in case everything in the stack
	 * 	should be dropped
	 * @param point location within the DropTarget
	 */
	void dropEntity(IEntity entity, int amount, Point point);
	/**
	 * Check if the DropTarget can accept a certain entity.
	 *
	 * @param entity entity to be checked
	 * @return <code>true</code>, if the DropTarget can process the entity in
	 * 	question, <code>false</code> otherwise
	 */
	boolean canAccept(IEntity entity);
}
