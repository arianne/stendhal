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

package games.stendhal.server.entity.mapstuff.useable;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Events;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPEvent;
import utilities.PlayerTestHelper;


/**
 * Tests for the ViewChangeEntity
 */
public class ViewChangeEntityTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
	}

	/**
	 * Test description string.
	 */
	@Test
	public void testDescribe() {
		ViewChangeEntity entity = new ViewChangeEntity(42, 99);

		assertEquals("Description", "You see a scrying orb. A note on it says \"Using costs 5 money. Stay still and concentrate while viewing\".",
				entity.describe());
	}

	/**
	 * Test trying to use the entity from too far away.
	 */
	@Test
	public void testUseFromTooFar() {
		ViewChangeEntity entity = new ViewChangeEntity(42, 99);
		Player player = PlayerTestHelper.createPlayer("spy");
		player.setPosition(1, 2);
		StendhalRPZone zone = new StendhalRPZone("testzone");
		zone.add(entity);
		zone.add(player);

		entity.onUsed(player);
		assertEquals(player.events().size(), 1);
		RPEvent event = player.events().get(0);
		assertEquals("Correct event type", Events.PRIVATE_TEXT, event.getName());
		assertEquals("You cannot reach that from here.", event.get("text"));
	}

	/**
	 * Test trying to use the orb without having completed the required quest.
	 */
	@Test
	public void testUseWithoutQuestDone() {
		ViewChangeEntity entity = new ViewChangeEntity(42, 99);
		Player player = PlayerTestHelper.createPlayer("spy");
		entity.onUsed(player);
		assertEquals(player.events().size(), 1);
		RPEvent event = player.events().get(0);
		assertEquals("Correct event type", Events.PRIVATE_TEXT, event.getName());
		assertEquals("You don't know how to use the strange device.", event.get("text"));
	}

	/**
	 * Test using the orb with close enough, and with the quest done, but
	 * without any money.
	 */
	@Test
	public void testUseWithoutMoney() {
		ViewChangeEntity entity = new ViewChangeEntity(42, 99);
		Player player = PlayerTestHelper.createPlayer("spy");
		player.setQuest("learn_scrying", "done");
		entity.onUsed(player);
		assertEquals(player.events().size(), 1);
		RPEvent event = player.events().get(0);
		assertEquals("Correct event type", Events.PRIVATE_TEXT, event.getName());
		assertEquals("You do not have enough money.", event.get("text"));
	}

	/**
	 * Test getting the view change event when all the usage conditions are met.
	 */
	@Test
	public void testAllOk() {
		ViewChangeEntity entity = new ViewChangeEntity(42, 99);
		Player player = PlayerTestHelper.createPlayer("spy");
		player.setQuest("learn_scrying", "done");
		PlayerTestHelper.equipWithMoney(player, 13);
		entity.onUsed(player);
		assertEquals(player.events().size(), 1);
		RPEvent event = player.events().get(0);
		assertEquals("Correct event type", Events.VIEW_CHANGE, event.getName());
		assertEquals("X coordinate of the event", 42, event.getInt("x"));
		assertEquals("Y coordinate of the event", 99, event.getInt("y"));
		assertEquals("Money properly substracted", 8, player.getFirstEquipped("money").getQuantity());
	}
}
