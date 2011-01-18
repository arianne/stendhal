/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import static org.junit.Assert.assertNotNull;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;

/**
 * Base class for quest tests.
 *
 * @author Martin Fuchs
 */
public abstract class ZoneAndPlayerTestImpl extends QuestHelper {

	/** Zone used in the test. */
	protected StendhalRPZone zone;

	/** Player object to be used in the test. */
	protected Player player;

	/**
	 * Protected constructor to initialize the zone member variable.
	 *
	 * @param zoneName
	 */
	protected ZoneAndPlayerTestImpl(final String zoneName) {
		zone = SingletonRepository.getRPWorld().getZone(zoneName);
		assertNotNull(zone);
	}

	/**
	 * Setup and configure zone with the configurators given as parameters before starting the test.
	 *
	 * @param zoneName
	 * @param zoneConfigurators
	 */
	protected static void setupZone(final String zoneName, final ZoneConfigurator... zoneConfigurators) {
		final StendhalRPZone zone = setupZone(zoneName);

		for (final ZoneConfigurator zoneConfigurator : zoneConfigurators) {
			zoneConfigurator.configureZone(zone, null);
		}
    }

	/**
	 * Creates zone and adds it to RPWorld.
	 *
	 * @param zoneName
	 * @return the new created zone
	 */
	protected static StendhalRPZone setupZone(final String zoneName) {
		return setupZone(zoneName, true);
    }

	/**
	 * Creates zone and adds it to RPWorld.
	 *
	 * @param zoneName
	 * @param collisions
	 * @return the new created zone
	 */
	protected static StendhalRPZone setupZone(final String zoneName, boolean collisions) {
		final StendhalRPZone zone;

		if (collisions) {
			zone = new StendhalRPZone(zoneName);
		} else {
			zone = new StendhalRPZone(zoneName, 1000, 1000); // disable collision detection
		}

		SingletonRepository.getRPWorld().addRPZone(zone);

		return zone;
    }

	/**
	 * Create the player to be used in the test.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		player = createPlayer("player");

		registerPlayer(player, zone);
	}

	/**
	 * Reset all involved NPCs.
	 *
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		removePlayer(player);
	}

}
