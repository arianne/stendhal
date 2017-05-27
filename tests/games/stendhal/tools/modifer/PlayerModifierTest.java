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
package games.stendhal.tools.modifer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

public class PlayerModifierTest {

	@BeforeClass
	public static void setUp() throws Exception {
		new DatabaseFactory().initializeDatabase();
	}

	/**
	 * Tests for loadPlayer.
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testLoadPlayer() throws Exception {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			MockStendlRPWorld.get();
			final String characterName = "modifyme";
			final PlayerModifier mod = new PlayerModifier();
			Player player = mod.loadPlayer(transaction, "");
			assertThat(player, nullValue());

			player = mod.loadPlayer(transaction, characterName);
			assertThat(player, not(nullValue()));
			assertThat(player.getName(), is(characterName));
			TransactionPool.get().rollback(transaction);
		} catch (Exception e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

	/**
	 * Tests for loadPlayerNameIsNull.
	 * @throws Exception
	 */
	@Test
	public void testLoadPlayerNameIsNull() throws Exception {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			MockStendlRPWorld.get();
			final PlayerModifier mod = new PlayerModifier();
			final Player player = mod.loadPlayer(transaction, null);
			assertThat(player, nullValue());
			TransactionPool.get().rollback(transaction);
		} catch (Exception e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

	/**
	 * Tests for modifyPlayer.
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testmodifyPlayer() throws Exception {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			MockStendlRPWorld.get();
			final String characterName = "modifyme";
			final PlayerModifier mod = new PlayerModifier();
			Player player = mod.loadPlayer(transaction, characterName);
			assertThat(player, not(nullValue()));
			assertThat(player.getName(), is(characterName));
			int adminlevel;
			if (player.getAdminLevel() == 100) {
				adminlevel = 0;

			} else {
				adminlevel = 100;
			}
			assertThat(player.getAdminLevel(), not(is(adminlevel)));
			player.setAdminLevel(adminlevel);

			assertThat(mod.savePlayer(transaction, player), is(true));

			player = mod.loadPlayer(transaction, characterName);
			assertThat(player, not(nullValue()));
			assertThat(player.getName(), is(characterName));
			assertThat(player.getAdminLevel(), is(adminlevel));
			TransactionPool.get().rollback(transaction);
		} catch (Exception e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

}
