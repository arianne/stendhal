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

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import games.stendhal.server.entity.Entity;

/**
 * a puzzle building block
 *
 * @author hendrik
 */
public class PuzzleBuildingBlock {
	private String zoneName;
	private String name;
	private Entity entity;
	private List<String> dependencies;

	/**
	 * creates a PuzzleBuildingBlock
	 *
	 * @param zoneName name of zone
	 * @param name unique name of entity in zone
	 * @param entity Entity
	 */
	public PuzzleBuildingBlock(String zoneName, String name, Entity entity) {
		this.zoneName = zoneName;
		this.name = name;
		this.entity = entity;
		this.dependencies = new LinkedList<>();
	}

	/**
	 * gets the name of the zone
	 *
	 * @return zone
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * gets the name of building block
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * listens to input change events
	 */
	public void onInputChanged() {
		entity.update();
	}

	/**
	 * gets a list of dependencies that this entity needs to listen to
	 *
	 * @return list of dependencies
	 */
	public List<String> getDependencies() {
		return ImmutableList.copyOf(dependencies);
	}
}
