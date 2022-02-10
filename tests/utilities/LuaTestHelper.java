/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
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

import org.junit.After;
import org.junit.BeforeClass;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.scripting.ScriptInLua;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;


public class LuaTestHelper {

	protected static StendhalRPWorld world;
	protected static MockStendhalRPRuleProcessor mrp;
	protected static StendhalQuestSystem qs;
	protected static ScriptInLua luaEngine;

	protected StendhalRPZone zone;
	protected Player player;


	@BeforeClass
	public static void setUpBeforeClass() {
		world = MockStendlRPWorld.get();
		mrp = MockStendhalRPRuleProcessor.get();
		qs = StendhalQuestSystem.get();
		luaEngine = ScriptInLua.get();
		luaEngine.init();
	}

	@After
	public void tearDown() {
		if (zone != null) {
			for (final Player p: zone.getPlayers()) {
				zone.remove(p);
			}

			world.removeZone(zone);
		}

		mrp.clearPlayers();
	}

	/**
	 * Initializes scripting system & loads a script.
	 *
	 * @param script
	 *     Path to script to be loaded.
	 */
	public static void load(final String script) {
		if (script != null) {
			luaEngine.load(script, null, null);
		}
	}

	/**
	 * This is executed separately so testing can be done
	 * before quests are loaded.
	 */
	public static void loadCachedQuests() {
		qs.loadCachedQuests();
	}

	/**
	 * Creates a new zone & adds it to mock world.
	 *
	 * @param zoneName
	 *     String name of new zone.
	 */
	protected void setUpZone(final String zoneName) {
		if (zone != null) {
			for (final Player p: zone.getPlayers()) {
				zone.remove(p);
			}

			world.removeZone(zone);
		}

		zone = new StendhalRPZone(zoneName);
		world.addRPZone("dummy", zone);
	}

	/**
	 * Initializes a player.
	 */
	protected void setUpPlayer() {
		if (player != null) {
			mrp.clearPlayers();
		}

		player = PlayerTestHelper.createPlayer("player");
		mrp.addPlayer(player);
	}

	/**
	 * Adds an initialized player to mock world.
	 */
	protected void addPlayerToWorld() {
		if (zone == null) {
			System.out.println("ERROR: cannot add player to world, zone not initialized");
		}

		if (player == null) {
			setUpPlayer();
		}

		player.setPosition(0, 0);
		zone.add(player);
	}
}
