/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.puzzle;

import java.util.List;

/**
 * a puzzle building block
 *
 * @author hendrik
 */
public interface PuzzleBuildingBlock {

	/**
	 * gets the name of the zone
	 *
	 * @return zone
	 */
	public String getZoneName();

	/**
	 * gets the name of building block
	 *
	 * @return name
	 */
	public String getName();

	/**
	 * listens to input change events
	 */
	public void onInputChanged();

	/**
	 * gets a list of dependencies that this entity needs to listen to
	 *
	 * @return list of dependencies
	 */
	public List<String> getDependencies();
}
