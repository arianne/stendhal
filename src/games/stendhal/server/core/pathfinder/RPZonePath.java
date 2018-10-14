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
package games.stendhal.server.core.pathfinder;

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import marauroa.common.Pair;

/**
 * class is wrapper around zone name and local path inside this zone
 * @author yoriy
 */
public class RPZonePath {

	private final Pair<StendhalRPZone, List<Node>> route;

	/**
	 * constructor
	 * @param zone_name - zone name
	 * @param localpath - list of path nodes
	 */
	public RPZonePath(String zone_name, List<Node> localpath) {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zone_name);
		if(zone==null) {
			Logger.getLogger(getClass()).warn("");
		}
		route = new Pair<StendhalRPZone, List<Node>>(zone, localpath);
	}

	/**
	 *
	 * @return zone route
	 */
	public Pair<StendhalRPZone, List<Node>> get() {
		return route;
	}

	/**
	 *
	 * @return zone
	 */
	public StendhalRPZone getZone() {
		return route.first();
	}

	/**
	 *
	 * @return local path
	 */
	public List<Node> getPath() {
		return route.second();
	}

	/**
	 *
	 * @return zone name
	 */
	public String getZoneName() {
		return route.first().getName();
	}
}
