/***************************************************************************
 *                   (C) Copyright 2013 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity;

/**
 * a potential killer
 */
public interface Killer extends Cloneable {

	/**
	 * returns the name of the killer
	 *
	 * @return name
	 */
	public String getName();

	/**
	 * clones the killer
	 *
	 * @return Killer
	 */
	public Object clone();

}
