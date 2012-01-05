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
import games.stendhal.common.constants.Events;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;

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
	 * Query the player's events for private messages.
	 * 
	 * @param player
	 * @return message text
	 */
	private List<String> getAllPrivateReplies(Player player) {
		
		List<String> replies = new LinkedList<String>();
		
		for (RPEvent event : player.events()) {
			if (event.getName().equals(Events.PRIVATE_TEXT)) {
				replies.add(event.get("text"));
			}
		}
		
		player.clearEvents();
		
		return replies;
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
	 * Tests for wrong action.
	 */
	@Test
	public final void testWrongAction() {
		final Player pl = PlayerTestHelper.createPlayer("player");

		RPAction action = new RPAction();
		action.put("type", "anotheraction");
		action.put("target", "spell");
		new MagicExtn().onAction(pl, action);
		assertEquals(0, pl.events().size());
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
	 * Tests for "heal" spell.
	 */
	@Test
	public final void testHealSpell() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest("spells", "heal");
		pl.setHP(35); // decrease heal points

		RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "heal");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(2, pl.events().size());
		int idx = 0;
		assertEquals("Trying to cast a spell...", pl.events().get(idx++).get("text"));
		assertEquals("You do not have enough available mana to use this spell.", pl.events().get(idx++).get("text"));
		pl.clearEvents();
		assertEquals(35, pl.getHP());

		// increase Mana value to enable the player casting the raise stats spell
		pl.setMana(20);
		action = new RPAction();
		action.put("type", "spell");
		action.put("target", "heal");
		assertTrue(CommandCenter.execute(pl, action));
		assertEquals(2, pl.events().size());
		idx = 0;
		assertEquals("Trying to cast a spell...", pl.events().get(idx++).get("text"));
		assertEquals("You have been healed.", pl.events().get(idx++).get("text"));
		pl.clearEvents();
		assertEquals(100, pl.getHP()); // check health
		assertEquals(5, pl.getMana());
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
		pl.setDef(23);
		pl.setAtkXP(20);
		pl.setAtk(10);

		RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "raise stats");
		assertTrue(CommandCenter.execute(pl, action));
		List<String> replies = getAllPrivateReplies(pl);
		assertEquals(2, replies.size());
		assertEquals("Trying to cast a spell...", replies.get(0));
		assertEquals("You do not have enough mana to cast this spell.", replies.get(1));
		assertEquals(50, pl.getXP());
		assertEquals(16, pl.getLevel());
		assertEquals(23, pl.getDef());
		assertEquals(20, pl.getAtkXP());
		assertEquals(10, pl.getAtk());

		// increase Mana value to enable the player casting the raise stats spell
		pl.setMana(120);
		pl.setBaseMana(120);
		action = new RPAction();
		action.put("type", "spell");
		action.put("target", "raise stats");
		assertTrue(CommandCenter.execute(pl, action));
		replies = getAllPrivateReplies(pl);
		assertEquals(2, replies.size());
		assertEquals("Trying to cast a spell...", replies.get(0));
		assertEquals("Your stats have been raised.", replies.get(1));
		pl.clearEvents();
		assertEquals(44950, pl.getXP());
		assertEquals(17, pl.getLevel());
		assertEquals(24, pl.getDef());
		assertEquals(24720, pl.getAtkXP());
		assertEquals(24, pl.getAtk());
		assertEquals(10, pl.getMana());
	}
}
