/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;


public class HasSpentTotalMoneyConditionTest extends PlayerTestHelper {

	private final Player player;


	public HasSpentTotalMoneyConditionTest() {
		player = createPlayer("player");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@Before
	public void checkEntities() {
		assertNotNull(player);
		assertFalse(player.hasMap("npc_purchases"));
	}

	@Test
	public void testToString() {
		ChatCondition cond = new HasSpentTotalMoneyCondition(1);
		assertEquals("HasSpentTotalMoneyCondition: 1", cond.toString());
		cond = new HasSpentTotalMoneyCondition(5, "foo");
		assertEquals("HasSpentTotalMoneyCondition: 5 foo", cond.toString());
		cond = new HasSpentTotalMoneyCondition(10, "foo", "bar");
		String st = cond.toString();
		assertTrue(st.startsWith("HasSpentTotalMoneyCondition: 10 "));
		assertTrue(st.contains("foo"));
		assertTrue(st.contains("bar"));
		assertFalse(st.contains("baz"));
		cond = new HasSpentTotalMoneyCondition(20, new String[] { "foo", "baz" });
		st = cond.toString();
		assertTrue(st.startsWith("HasSpentTotalMoneyCondition: 20 "));
		assertTrue(st.contains("foo"));
		assertFalse(st.contains("bar"));
		assertTrue(st.contains("baz"));
		cond = new HasSpentTotalMoneyCondition(25, new ArrayList<String>() {{ add("bar"); add("baz"); }});
		st = cond.toString();
		assertTrue(st.startsWith("HasSpentTotalMoneyCondition: 25 "));
		assertFalse(st.contains("foo"));
		assertTrue(st.contains("bar"));
		assertTrue(st.contains("baz"));
	}

	@Test
	public void testTotalAny() {
		ChatCondition cond = new HasSpentTotalMoneyCondition(1);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 1, false);
		assertTrue(cond.fire(player, null, null));
		cond = new HasSpentTotalMoneyCondition(5);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("bar", 4, false);
		assertTrue(cond.fire(player, null, null));
		cond = new HasSpentTotalMoneyCondition(20);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("baz", 15, false);
		assertTrue(cond.fire(player, null, null));
	}

	@Test
	public void testTotalSpecific() {
		ChatCondition cond = new HasSpentTotalMoneyCondition(5, "foo");
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("bar", 5, false);
		player.incCommerceTransaction("baz", 5, false);
		player.incCommerceTransaction("foo", 4, false);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 1, false);
		assertTrue(cond.fire(player, null, null));
		cond = new HasSpentTotalMoneyCondition(20, "foo", "bar");
		player.incCommerceTransaction("baz", 100, false);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 5, false);
		player.incCommerceTransaction("bar", 4, false);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("bar", 1, false);
		assertTrue(cond.fire(player, null, null));
	}
}
