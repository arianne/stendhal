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
package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.BASEOBJECT;
import static games.stendhal.common.constants.Actions.BASESLOT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import utilities.PlayerTestHelper;
import utilities.RPClass.ChestTestHelper;

public class UseActionTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
		Log4J.init();
	}

	/**
	 * Tests for onActionItemInBag.
	 */
	@Test
	public void testOnActionItemInBag() {
		MockStendlRPWorld.get();
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		player.equip("bag", cheese);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, player.getID().getObjectID());
		action.put(BASESLOT, "bag");
		assertTrue(player.isEquipped("cheese"));
		ua.onAction(player, action);
		assertFalse(player.isEquipped("cheese"));

		// Same using item path
		cheese = SingletonRepository.getEntityManager().getItem("cheese");
		player.equip("bag", cheese);
		action = new RPAction();
		action.put(Actions.TARGET_PATH, Arrays.asList(Integer.toString(player.getID().getObjectID()),
				"bag", Integer.toString(cheese.getID().getObjectID())));
		assertTrue(player.isEquipped("cheese"));
		ua.onAction(player, action);
		assertFalse(player.isEquipped("cheese"));
	}

	/**
	 * Tests for onActionItemInBagWithTwoCheese.
	 */
	@Test
	public void testOnActionItemInBagWithTwoCheese() {
		MockStendlRPWorld.get();
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final StackableItem cheese = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		cheese.setQuantity(2);
		player.equip("bag", cheese);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, player.getID().getObjectID());
		action.put(BASESLOT, "bag");
		assertTrue(player.isEquipped("cheese"));
		ua.onAction(player, action);
		assertTrue(player.isEquipped("cheese"));
		assertEquals(1, cheese.getQuantity());

		// The same using item path
		cheese.setQuantity(2);
		action = new RPAction();
		action.put(Actions.TARGET_PATH, Arrays.asList(Integer.toString(player.getID().getObjectID()),
				"bag", Integer.toString(cheese.getID().getObjectID())));
		assertTrue(player.isEquipped("cheese"));
		ua.onAction(player, action);
		assertTrue(player.isEquipped("cheese"));
		assertEquals(1, cheese.getQuantity());
	}

	/**
	 * Tests for onActionIteminChest.
	 */
	@Test
	public void testOnActionIteminChest() {
		MockStendlRPWorld.get();
		ChestTestHelper.generateRPClasses();
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Chest chest = new Chest();
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		chest.add(cheese);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.collisionMap.clear();
		player.setPosition(1, 1);
		chest.setPosition(1, 2);
		zone.add(player);
		zone.add(chest);
		chest.open();
		RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, chest.getID().getObjectID());
		action.put(BASESLOT, "content");
		assertFalse(player.has("eating"));
		ua.onAction(player, action);
		assertTrue(player.has("eating"));

		// Same using item paths
		cheese = SingletonRepository.getEntityManager().getItem("cheese");
		chest.add(cheese);
		action = new RPAction();
		action.put(Actions.TARGET_PATH, Arrays.asList(Integer.toString(chest.getID().getObjectID()),
				"content", Integer.toString(cheese.getID().getObjectID())));
		assertTrue(chest.getContent().hasNext());
		ua.onAction(player, action);
		assertFalse(chest.getContent().hasNext());
	}

	/**
	 * Tests for isItemBoundToOtherPlayer.
	 */
	@Test
	public void testIsItemBoundToOtherPlayer() {
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		assertFalse(ua.isItemBoundToOtherPlayer(player, null));
		assertFalse(ua.isItemBoundToOtherPlayer(player, cheese));
		cheese.setBoundTo("jack");

		assertFalse(ua.isItemBoundToOtherPlayer(player, null));
		assertTrue(ua.isItemBoundToOtherPlayer(player, cheese));

		cheese.setBoundTo("bob");

		assertFalse(ua.isItemBoundToOtherPlayer(player, null));
		assertFalse(ua.isItemBoundToOtherPlayer(player, cheese));
	}

	/**
	 * Test trying to use an item in possession of another player.
	 */
	@Test
	public void testItemOwnedByOtherPlayer() {
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Player player2 = PlayerTestHelper.createPlayer("croesus");
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");

		player2.equip("bag", cheese);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		zone.add(player2);
		RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, player2.getID().getObjectID());
		action.put(BASESLOT, "bag");
		assertTrue(player2.isEquipped("cheese"));
		ua.onAction(player, action);
		assertTrue(player2.isEquipped("cheese"));

		// Same using item path
		action = new RPAction();
		action.put(Actions.TARGET_PATH, Arrays.asList(Integer.toString(player2.getID().getObjectID()),
				"bag", Integer.toString(cheese.getID().getObjectID())));
		ua.onAction(player, action);
		assertTrue(player2.isEquipped("cheese"));
	}

	/**
	 * Test using an item that is on ground.
	 */
	@Test
	public void testItemOnGround() {
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");

		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		zone.add(cheese);
		RPAction action = new RPAction();
		action.put(Actions.TARGET, "#" + cheese.getID().getObjectID());
		ua.onAction(player, action);
		assertEquals(0, cheese.getQuantity());

		// Same using item path
		cheese = SingletonRepository.getEntityManager().getItem("cheese");
		zone.add(cheese);
		action = new RPAction();
		action.put(Actions.TARGET_PATH, Arrays.asList(Integer.toString(cheese.getID().getObjectID())));
		assertEquals(1, cheese.getQuantity());
		ua.onAction(player, action);
		assertEquals(0, cheese.getQuantity());
	}

	/**
	 * Test using an item that is in a corpse
	 */
	@Test
	public void testItemFromCorpse() {
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		Corpse corpse = new Corpse("rat", 0, 0);

		final StendhalRPZone zone = new StendhalRPZone("zone");
		corpse.add(cheese);
		zone.add(player);
		zone.add(corpse);
		RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, corpse.getID().getObjectID());
		action.put(BASESLOT, "content");
		ua.onAction(player, action);
		assertEquals(0, cheese.getQuantity());

		// Same using item path
		cheese = SingletonRepository.getEntityManager().getItem("cheese");
		corpse.add(cheese);
		action = new RPAction();
		action.put(Actions.TARGET_PATH, Arrays.asList(Integer.toString(corpse.getID().getObjectID()),
				"content", Integer.toString(cheese.getID().getObjectID())));
		ua.onAction(player, action);
		assertEquals(0, cheese.getQuantity());
	}
}
