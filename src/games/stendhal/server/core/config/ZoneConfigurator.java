/*
 * @(#) src/games/stendhal/server/config/ZoneConfigurator.java
 *
 * $Id$
 */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config;

import java.util.Map;

import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * Zone post-configuration.
 */
public interface ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	void configureZone(StendhalRPZone zone, Map<String, String> attributes);
}
