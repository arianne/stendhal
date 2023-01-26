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


public class HasEarnedTotalMoneyConditionTest extends PlayerTestHelper {

	private final Player player;


	public HasEarnedTotalMoneyConditionTest() {
		player = createPlayer("player");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@Before
	public void checkEntities() {
		assertNotNull(player);
		assertFalse(player.hasMap("npc_sales"));
	}

	@Test
	public void testToString() {
		ChatCondition cond = new HasEarnedTotalMoneyCondition(1);
		assertEquals("HasEarnedTotalMoneyCondition: 1", cond.toString());
		cond = new HasEarnedTotalMoneyCondition(5, "foo");
		assertEquals("HasEarnedTotalMoneyCondition: 5 foo", cond.toString());
		cond = new HasEarnedTotalMoneyCondition(10, "foo", "bar");
		String st = cond.toString();
		assertTrue(st.startsWith("HasEarnedTotalMoneyCondition: 10 "));
		assertTrue(st.contains("foo"));
		assertTrue(st.contains("bar"));
		assertFalse(st.contains("baz"));
		cond = new HasEarnedTotalMoneyCondition(20, new String[] { "foo", "baz" });
		st = cond.toString();
		assertTrue(st.startsWith("HasEarnedTotalMoneyCondition: 20 "));
		assertTrue(st.contains("foo"));
		assertFalse(st.contains("bar"));
		assertTrue(st.contains("baz"));
		cond = new HasEarnedTotalMoneyCondition(25, new ArrayList<String>() {{ add("bar"); add("baz"); }});
		st = cond.toString();
		assertTrue(st.startsWith("HasEarnedTotalMoneyCondition: 25 "));
		assertFalse(st.contains("foo"));
		assertTrue(st.contains("bar"));
		assertTrue(st.contains("baz"));
	}

	@Test
	public void testTotalAny() {
		ChatCondition cond = new HasEarnedTotalMoneyCondition(1);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 1, true);
		assertTrue(cond.fire(player, null, null));
		cond = new HasEarnedTotalMoneyCondition(5);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("bar", 4, true);
		assertTrue(cond.fire(player, null, null));
		cond = new HasEarnedTotalMoneyCondition(20);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("baz", 15, true);
		assertTrue(cond.fire(player, null, null));
	}

	@Test
	public void testTotalSpecific() {
		ChatCondition cond = new HasEarnedTotalMoneyCondition(5, "foo");
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("bar", 5, true);
		player.incCommerceTransaction("baz", 5, true);
		player.incCommerceTransaction("foo", 4, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 1, true);
		assertTrue(cond.fire(player, null, null));
		cond = new HasEarnedTotalMoneyCondition(20, "foo", "bar");
		player.incCommerceTransaction("baz", 100, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 5, true);
		player.incCommerceTransaction("bar", 4, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("bar", 1, true);
		assertTrue(cond.fire(player, null, null));
	}
}
