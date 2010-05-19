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
/*
 * DropTarget.java
 *
 * Created on 19. Oktober 2005, 20:45
 */

package games.stendhal.client.gui.wt.core;

/**
 * Each Panel wanting to receive events when an object is dropped over them must
 * implement this interface.
 * 
 * @author mtotz
 */
public interface WtDropTarget {
	/**
	 * called when an object is dropped.
	 * 
	 * @param x
	 * @param y
	 * @param droppedObject
	 * @return if operation succeeded
	 */
	boolean onDrop(int x, int y, WtDraggable droppedObject);
}
