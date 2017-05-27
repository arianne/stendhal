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
package games.stendhal.server.actions.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import utilities.PlayerTestHelper;

public class AlterQuestActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		Log4J.init();
		MockStendlRPWorld.get();
		AlterQuestAction.register();
	}

	@Test
	public void alterQuestActionPerform() throws Exception {
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final MockStendhalRPRuleProcessor rules = MockStendhalRPRuleProcessor.get();
		rules.addPlayer(bob);
		final RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");
		action.put("state", "queststate");
		final AlterQuestAction aq = new AlterQuestAction();
		aq.perform(bob, action);
		assertTrue(bob.hasQuest("questname"));
		assertEquals("queststate", bob.getQuest("questname"));

	}

	@Test
	public void alterQuestActionPerformTarget() throws Exception {

		final Player bob = PlayerTestHelper.createPlayer("bob");
		final Player james = PlayerTestHelper.createPlayer("james");
		final MockStendhalRPRuleProcessor rules = MockStendhalRPRuleProcessor.get();
		rules.addPlayer(james);
		final RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "james");
		action.put("name", "questname");
		action.put("state", "queststate");
		final AlterQuestAction aq = new AlterQuestAction();
		aq.perform(bob, action);
		assertTrue(james.hasQuest("questname"));
		assertEquals("queststate", james.getQuest("questname"));
	}

	@Test
	public void alterQuestActionPerformthroughCommandcenter() throws Exception {
		final Player pl = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);

		pl.put("adminlevel", 5000);

		RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");
		action.put("state", "queststate");
		CommandCenter.execute(pl, action);
		assertTrue(bob.hasQuest("questname"));
		assertEquals("queststate", bob.getQuest("questname"));
		action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");

		CommandCenter.execute(pl, action);
		assertFalse(bob.hasQuest("questname"));

	}
	@Test
	public void alterQuestActionCastersLeveltoLow() throws Exception {
		final Player pl = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);

		pl.put("adminlevel", 0);

		final RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");
		action.put("state", "queststate");
		CommandCenter.execute(pl, action);
		assertFalse(bob.hasQuest("questname"));
	}
}
