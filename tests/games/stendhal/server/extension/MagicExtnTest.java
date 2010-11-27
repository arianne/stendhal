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
package games.stendhal.server.extension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * JUnit Tests for MagicExtn.
 * @author Martin Fuchs
 */
public class MagicExtnTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
		new MagicExtn();
	}

	@After
	public void tearDown() {
		PlayerTestHelper.removePlayer("player");
	}

	/**
	 * Tests for missing spell.
	 */
	@Test
	public final void testNoSpell() {
		final Player pl = PlayerTestHelper.createPlayer("player");

		RPAction action = new RPAction();
		action.put("type", "spell");
		// no target
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(1, pl.events().size());
		assertEquals("Usage: #/spell <spellname>", pl.events().get(0).get("text"));
		pl.clearEvents();

		action = new RPAction();
		action.put("type", "spell");
		action.put("target", "");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(1, pl.events().size());
		assertEquals("You did not enter a spell to cast.", pl.events().get(0).get("text"));
		pl.clearEvents();
	}

	/**
	 * Tests for missing quest.
	 */
	@Test
	public final void testNoQuest() {
		final Player pl = PlayerTestHelper.createPlayer("player");

		final RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "spell");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(2, pl.events().size());
		assertEquals("Trying to cast a spell...", pl.events().get(0).get("text"));
		assertEquals("You can not cast this spell.", pl.events().get(1).get("text"));
		pl.clearEvents();
	}

	/**
	 * Tests for wrong spell name.
	 */
	@Test
	public final void testWrongSpell() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest("spells", "spell1;spell2");

		final RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "spellX");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(2, pl.events().size());
		int idx = 0;
		assertEquals("Trying to cast a spell...", pl.events().get(idx++).get("text"));
		assertEquals("You can not cast this spell.", pl.events().get(idx++).get("text"));
		pl.clearEvents();
	}

	/**
	 * Tests for correct, but unhandled spell.
	 */
	@Test
	public final void testUnhandledSpell() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest("spells", "spell1;spell2");

		final RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "spell1");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(2, pl.events().size());
		int idx = 0;
		assertEquals("Trying to cast a spell...", pl.events().get(idx++).get("text"));
		assertEquals("The spell you tried to cast doesn't exist!", pl.events().get(idx++).get("text"));
		pl.clearEvents();
	}

	/**
	 * Tests for "raise stats" spell.
	 */
	@Test
	public final void testRaiseStatsSpell() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest("spells", "raise stats");
		pl.setXP(50);
		pl.setLevel(16);
		pl.setDEF(23);
		pl.setATKXP(20);
		pl.setATK(10);

		RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "raise stats");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(2, pl.events().size());
		int idx = 0;
		assertEquals("Trying to cast a spell...", pl.events().get(idx++).get("text"));
		assertEquals("You do not have enough mana to cast this spell.", pl.events().get(idx++).get("text"));
		pl.clearEvents();
		assertEquals(50, pl.getXP());
		assertEquals(16, pl.getLevel());
		assertEquals(23, pl.getDEF());
		assertEquals(20, pl.getATKXP());
		assertEquals(10, pl.getATK());

		// increase Mana value to enable the player casting the raise stats spell
		pl.setMana(101);
		action = new RPAction();
		action.put("type", "spell");
		action.put("target", "raise stats");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(2, pl.events().size());
		idx = 0;
		assertEquals("Trying to cast a spell...", pl.events().get(idx++).get("text"));
		assertEquals("Your stats have been raised.", pl.events().get(idx++).get("text"));
		pl.clearEvents();
		assertEquals(44950, pl.getXP());
		assertEquals(17, pl.getLevel());
		assertEquals(24, pl.getDEF());
		assertEquals(24720, pl.getATKXP());
		assertEquals(24, pl.getATK());
	}
}
