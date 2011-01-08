/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.office;

/**
 * An entity that will be stored by the zone to the database.
 *
 * @author hendrik
 */
public interface StorableEntity {

	/**
	 * Define this object as storable, but it doesn't in fact store the object.
	 * The object is stored on zone.finish
	 */
	void store();
}
