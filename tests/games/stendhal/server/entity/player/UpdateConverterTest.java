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
package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.item.Item;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

/**
 * Test the UpdateConverter class.
 *
 * @author Martin Fuchs
 */
public class UpdateConverterTest {

	@BeforeClass
	public static void setupClass() {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
	}

	/**
	 * Tests for transformString.
	 */
	@Test
	public void testTransformString() {
		assertEquals(null, UpdateConverter.updateItemName(null));
		assertEquals("", UpdateConverter.updateItemName(""));
		assertEquals("chicken", UpdateConverter.updateItemName("chicken"));
		assertEquals("enhanced chainmail", UpdateConverter.updateItemName("chain_armor_+1"));
		assertEquals("enhanced lion shield", UpdateConverter.updateItemName("lion_shield_+1"));
		assertEquals("enhanced lion shield", UpdateConverter.updateItemName("enhanced_lion_shield"));
		assertEquals("black book", UpdateConverter.updateItemName("black_book"));
		assertEquals("black book", UpdateConverter.updateItemName("book_black"));
		assertEquals("black book", UpdateConverter.updateItemName("book black"));
	}

	/**
	 * Tests the killing slot upgrade transformation.
	 */
	@Test
	public void testTransformKillSlot() {
		final Player player = PlayerTestHelper.createPlayer("player");

		RPSlot killSlot = player.getSlot("!kills");
		RPObject killStore = killSlot.getFirst();

		killStore.put("name", "solo");
		killStore.put("monster", "shared");
		killStore.put("cave_rat", "solo");

		final String oldID = killStore.get("id");

		UpdateConverter.updatePlayerRPObject(player);

		killSlot = player.getSlot("!kills");
		killStore = killSlot.getFirst();

		final String idDot = killStore.get(oldID + ".id");
		assertEquals(null, idDot);

		assertTrue(player.hasKilled("name"));
		assertTrue(player.hasKilled("monster"));
		assertFalse(player.hasKilled("cave_rat"));
		assertTrue(player.hasKilled("cave rat"));
	}

	/**
	 * Tests the new killings slot functionality in conjunction with updatePlayerRPObject().
	 */
	@Test
	public void testKillingRecords() {
		final Player player = PlayerTestHelper.createPlayer("player");

		player.setSoloKill("name");
		player.setSharedKill("monster");
		player.setSoloKill("cave rat");

		RPSlot killSlot = player.getSlot("!kills");
		RPObject killStore = killSlot.getFirst();
		final String oldID = killStore.get("id");

		UpdateConverter.updatePlayerRPObject(player);

		killSlot = player.getSlot("!kills");
		killStore = killSlot.getFirst();

		final String idDot = killStore.get(oldID + ".id");
		assertEquals(null, idDot);

		assertTrue(player.hasKilled("name"));
		assertTrue(player.hasKilled("monster"));
		assertTrue(player.hasKilled("cave rat"));
	}

	/**
	 * Tests for renameQuest.
	 */
	@Test
	public void testRenameQuest() {
		final Player player = PlayerTestHelper.createPlayer("player");

		// First we use only the old quest slot name.
		player.setQuest("Valo_concoct_potion", "3;mega potion;1200000000000");
		UpdateConverter.updateQuests(player);
		assertNull(player.getQuest("Valo_concoct_potion"));
		assertEquals("3;mega potion;1200000000000", player.getQuest("valo_concoct_potion"));

		// Now add the old name to the existing new one and see if they are accumulated correct.
		player.setQuest("Valo_concoct_potion", "8;mega potion;1300000000000");
		UpdateConverter.updateQuests(player);
		assertNull(player.getQuest("Valo_concoct_potion"));
		assertEquals("11;mega potion;1200000000000", player.getQuest("valo_concoct_potion"));
	}

	/**
	 * Tests for renameQuest.
	 */
	@Test
	public void testfixKillQuestsSlots() {
		final Player player = PlayerTestHelper.createPlayer("player");
		player.setQuest("kill_gnomes", "start");
		player.setQuest("clean_storage", "start");
		player.setQuest("kill_dhohr_nuggetcutter", "start");
		UpdateConverter.updateQuests(player);
		assertEquals(player.getQuest("clean_storage"), "start;rat,0,1,0,0,caverat,0,1,0,0,snake,0,1,0,0");
		assertEquals(player.getQuest("kill_gnomes"), "start;gnome,0,1,0,0,infantry gnome,0,1,0,0,cavalryman gnome,0,1,0,0");
		assertEquals(player.getQuest("kill_dhohr_nuggetcutter"), "start;Dhohr Nuggetcutter,0,1,0,0,mountain dwarf,0,1,0,0,mountain elder dwarf,0,1,0,0,mountain hero dwarf,0,1,0,0,mountain leader dwarf,0,1,0,0");
	}

	/**
	 * Test updating the keyring feature.
	 */
	@Test
	public void testUpdateKeyring() {
		final Player player = PlayerTestHelper.createPlayer("player");
		// First test *not* updating
		assertNull("Sanity check", player.getFeature("keyring"));
		UpdateConverter.updateKeyring(player);
		assertNull("Updating without keyring feature should not create a keyring", player.getFirstEquipped("keyring"));
		assertNull("Sanity check", player.getFeature("keyring"));

		// The actual update checks
		player.setFeature("keyring", "2 4");
		assertNotNull("Sanity check", player.getFeature("keyring"));
		Item key = ItemTestHelper.createItem("dungeon silver key");
		player.equip("keyring", key);
		UpdateConverter.updateKeyring(player);
		Item keyring = player.getFirstEquipped("keyring");
		assertNotNull("Check creating a keyring when updating with the keyring feature", keyring);
		assertEquals("Check the keyring is bound to the owner", player.getName(), keyring.getBoundTo());
		assertEquals("Check the key got moved to the created container", keyring, key.getContainer());
		assertEquals("Check that the keyring got placed in belt slot", player.getSlot("belt"), keyring.getContainerSlot());
		assertNull("Check that the old keyring feature was turned off", player.getFeature("keyring"));
	}
}
