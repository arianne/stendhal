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
package games.stendhal.server.entity.item.consumption;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.RPClass.ConsumableTestHelper;

public class ImmunizerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Before
	public void setUp() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for feed.
	 */
	@Test
	public void testFeed() {
		int startTurn = TurnNotifier.get().getCurrentTurnForDebugging();
		if (startTurn < 0) {
			startTurn = 0;
		}

		TurnNotifier.get().logic(startTurn + 1);
		assertEquals(startTurn + 1, TurnNotifier.get().getCurrentTurnForDebugging());


		Immunizer immu = new Immunizer();

		ConsumableItem item = ConsumableTestHelper.createImmunizer("antidote");
		item.put("id", 1);
		Player player = PlayerTestHelper.createPlayer("herrkules");
		assertFalse(player.getStatusList().isImmune(StatusType.POISONED));
		assertTrue(immu.feed(item, player));
		assertTrue(player.getStatusList().isImmune(StatusType.POISONED));
		ConsumableItem item2 = ConsumableTestHelper.createImmunizer("antidote");
		item2.put("id", 2);

		assertEquals(2, TurnNotifier.get().getRemainingTurns(new StatusHealerEater(player, StatusType.POISONED)));

		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);

		assertEquals(1, TurnNotifier.get().getRemainingTurns(new StatusHealerEater(player, StatusType.POISONED)));
		assertThat(player.events().size(), is(0));
		assertTrue(immu.feed(item2, player));

		assertThat(player.events().size(), is(0));
		assertTrue(player.getStatusList().isImmune(StatusType.POISONED));
		assertEquals(2, TurnNotifier.get().getRemainingTurns(new StatusHealerEater(player, StatusType.POISONED)));

		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);
		assertTrue(player.getStatusList().isImmune(StatusType.POISONED));
		assertEquals(1, TurnNotifier.get().getRemainingTurns(new StatusHealerEater(player, StatusType.POISONED)));

		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);
		assertFalse(player.getStatusList().isImmune(StatusType.POISONED));
		assertEquals(-1, TurnNotifier.get().getRemainingTurns(new StatusHealerEater(player, StatusType.POISONED)));
		assertThat(player.events().size(), is(1));
		assertThat(player.events().get(0).get("text"), is("You are not immune to being poisoned anymore."));

		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);
		assertFalse(player.getStatusList().isImmune(StatusType.POISONED));
		assertEquals(-1, TurnNotifier.get().getRemainingTurns(new StatusHealerEater(player, StatusType.POISONED)));

	}

}
