/*
 * @(#) src/games/stendhal/server/config/ZoneConfigurator.java
 *
 * $Id$
 */
package games.stendhal.server.core.config;

import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

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
