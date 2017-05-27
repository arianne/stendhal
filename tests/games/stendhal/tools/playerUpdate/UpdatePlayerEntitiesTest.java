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
package games.stendhal.tools.playerUpdate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.tools.modifer.PlayerModifier;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.DatabaseFactory;

public class UpdatePlayerEntitiesTest {

	@BeforeClass
	public static void setUp() throws Exception {
		new DatabaseFactory().initializeDatabase();
	}

	@After
	public void tearDown() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for doUpdate.
	 * @throws Throwable
	 */
	@Test
	public void testDoUpdate() throws Throwable {
		MockStendlRPWorld.get();
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			PlayerModifier pm = new PlayerModifier();
			Player loaded = pm.loadPlayer(transaction, "george");
			// "pm can only handle existing players, so if this fails first create a player called george in db by login"
			assumeThat(loaded, notNullValue());
			if (loaded.getSlot("bag").size() > 0) {
				loaded.getSlot("bag").remove(loaded.getSlot("bag").getFirst().getID());
			}
			//assertEquals(null, loaded.getSlot("bag").getFirst());

			EntityManager em = SingletonRepository.getEntityManager();
			Item item = em.getItem("leather armor");
			item.put("name", "leather_armor_+1");
			loaded.equipToInventoryOnly(item);
			assertTrue(loaded.getSlot("bag").has(item.getID()));

			assertTrue(pm.savePlayer(transaction, loaded));
			UpdatePlayerEntities updatePlayerEntities = new UpdatePlayerEntities();
			Player changing = updatePlayerEntities.createPlayerFromRPO(loaded);
			updatePlayerEntities.savePlayer(transaction, changing);


			Player secondLoaded = pm.loadPlayer(transaction, "george");
			assertNotNull(secondLoaded);

			assertNotNull(secondLoaded.getSlot("bag"));
			assertNotNull(secondLoaded.getSlot("bag").getFirst());
			assertThat(secondLoaded.getSlot("bag").getFirst().get("name"), not(is("leather_armor_+1")));

			TransactionPool.get().rollback(transaction);
		} catch (Throwable e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

}
