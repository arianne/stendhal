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
package games.stendhal.server.entity.item;

/**
 * this interface tags all items which are stackable.
 * 
 * @author mtotz
 * @param <T> 
 */
public interface Stackable<T> {

	/** @return the quantity */
	int getQuantity();

	/** sets the quantity. 
	 * @param amount to be set*/
	void setQuantity(int amount);

	/** Adds the quantity of the other Stackable to this .
	 * @param other 
	 * @return the previous quantity */
	int add(T other);

	/** @param other 
	 * @return true when both stackables are of the same type and can be merged */
	boolean isStackable(T other);

}
