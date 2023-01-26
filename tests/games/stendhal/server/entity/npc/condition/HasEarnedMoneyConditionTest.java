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


public class HasEarnedMoneyConditionTest extends PlayerTestHelper {

	private final Player player;


	public HasEarnedMoneyConditionTest() {
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
		ChatCondition cond = new HasEarnedMoneyCondition(1);
		assertEquals("HasEarnedMoneyCondition: 1", cond.toString());
		cond = new HasEarnedMoneyCondition(5);
		assertEquals("HasEarnedMoneyCondition: 5", cond.toString());
		cond = new HasEarnedMoneyCondition(10, "foo");
		assertEquals("HasEarnedMoneyCondition: foo=10", cond.toString());
		cond = new HasEarnedMoneyCondition(15, "foo", "bar");
		String st = cond.toString();
		assertTrue(st.contains("foo=15"));
		assertTrue(st.contains("bar=15"));
		assertFalse(st.contains("baz=15"));
		cond = new HasEarnedMoneyCondition(20, new String[] { "foo", "bar" });
		st = cond.toString();
		assertTrue(st.contains("foo=20"));
		assertTrue(st.contains("bar=20"));
		assertFalse(st.contains("baz=20"));
		cond = new HasEarnedMoneyCondition(25, new ArrayList<String>() {{ add("foo"); add("bar"); }});
		st = cond.toString();
		assertTrue(st.contains("foo=25"));
		assertTrue(st.contains("bar=25"));
		assertFalse(st.contains("baz=25"));
	}

	@Test
	public void testAnyNPC() {
		final ChatCondition cond = new HasEarnedMoneyCondition(50);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 49, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 1, true);
		assertTrue(cond.fire(player, null, null));
	}

	@Test
	public void testSpecificNPC() {
		final ChatCondition cond = new HasEarnedMoneyCondition(50, "foo");
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 49, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 1, true);
		assertTrue(cond.fire(player, null, null));
	}

	@Test
	public void testAllNPCs() {
		final ChatCondition cond = new HasEarnedMoneyCondition(50, "foo", "bar", "baz");
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("foo", 50, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("bar", 50, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("baz", 49, true);
		assertFalse(cond.fire(player, null, null));
		player.incCommerceTransaction("baz", 1, true);
		assertTrue(cond.fire(player, null, null));
	}
}
