/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.piedpiper.ITPPQuestConstants;
import games.stendhal.server.maps.quests.thepiedpiper.InvasionPhaseTest;
import games.stendhal.server.maps.quests.thepiedpiper.TPPTestHelper;

public class ThePiedPiperTest implements ITPPQuestConstants {

	protected final static ThePiedPiper quest = new ThePiedPiper();
	private static Logger logger = Logger.getLogger(ThePiedPiperTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TPPTestHelper.setUpBeforeClass();
	}


	/**
	 * Tests for quest phases changing engine.
	 */
	@Test
	public void testPhaseChanging() {
		ThePiedPiper.setPhase(INACTIVE);
		assertEquals(TPP_Phase.TPP_INACTIVE, ThePiedPiper.getDefaultPhaseClass().getPhase());
		logger.info("current phase: "+ThePiedPiper.getPhase().name());
		int sz=ThePiedPiper.getPhases().size();
		for(int i=0; i<sz; i++) {
			ThePiedPiper.switchToNextPhase();
			logger.info("current phase: "+ThePiedPiper.getPhase().name());
		}
		assertEquals(TPP_Phase.TPP_INACTIVE, ThePiedPiper.getPhase());
	}

	private void doQuest(final InvasionPhaseTest phase) {
		phase.startInvasion();
		phase.killRats(15);
		phase.endInvasion();
		phase.collectReward();
		phase.resetReward();
	}

	@Test
	public void testCompletions() {
		final String questSlot = quest.getSlotName();
		final Player player = TPPTestHelper.getPlayer();
		assertThat(player, notNullValue());
		final SpeakerNPC npc = TPPTestHelper.getNPC();
		assertThat(npc, notNullValue());

		final InvasionPhaseTest phaseTest = new InvasionPhaseTest();
		for (int count = 0; count < 5; count++) {
			assertThat(MathHelper.parseIntDefault(player.getQuest(questSlot, 1), 0), is(count));
			// run quest
			doQuest(phaseTest);
		}
		assertThat(player.getQuest(questSlot), is("done;5"));
	}
}
