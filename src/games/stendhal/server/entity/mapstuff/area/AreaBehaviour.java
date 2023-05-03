/***************************************************************************
 *                    Copyright © 2012-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.area;

public interface AreaBehaviour {

	/**
	 * activates the behaviour by adding it to an area entity
	 *
	 * @param parentAreaEntity area entity
	 */
	public abstract void addToWorld(AreaEntity parentAreaEntity);

	/**
	 * sets the area entity
	 */
	public abstract void removeFromWorld();

}
