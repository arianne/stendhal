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

	/**
	 * gets the current quantity
	 *
	 * @return the quantity
	 */
	int getQuantity();

	/**
	 * sets the quantity.
	 *
	 * @param amount to be set
	 */
	void setQuantity(int amount);

	/**
	 * gets the maximum amount
	 *
	 * @return the maximum amount
	 */
	int getCapacity();

	/**
	 * sets the maximum amount
	 *
	 * @param capacity
	 */
	void setCapacity(int capacity);

	/**
	 * Adds the quantity of the other Stackable to this.
	 *
	 * @param other other object to merge in
	 * @return the previous quantity
	 */
	int add(T other);

	/**
	 * checks if the other object can be stacked onto this one
	 *
	 * @param other other object
	 * @return true when both stackables are of the same type and can be merged */
	boolean isStackable(T other);

}
