/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package scripts.quest;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.LuaTestHelper;


public class LostEngagementRingTest extends LuaTestHelper {

	private SpeakerNPC ari;
	private SpeakerNPC emma;

	private final String slot = "lost_engagement_ring";


	@Before
	public void setUp() {
		setUpZone("0_fado_city");
		load("data/script/region/fado/city/EngagedCouple.lua");
		load("data/script/quest/LostEngagementRing.lua");
	}

	@Test
	public void init() {
		initEntities();
		testWithoutQuestLoaded();
		testWithQuestLoaded();
	}

	private void initEntities() {
		final NPCList npcs = SingletonRepository.getNPCList();
		ari = npcs.get("Ari");
		emma = npcs.get("Emma");

		assertNotNull(ari);
		assertNotNull(emma);

		addPlayerToWorld();
		assertNotNull(player);
		assertEquals("0_fado_city", player.getZone().getName());
		assertFalse(player.hasQuest(slot));
	}

	private void testWithoutQuestLoaded() {
		Engine en;
		for (final SpeakerNPC npc: Arrays.asList(ari, emma)) {
			assertEquals("love", npc.getIdea());

			en = npc.getEngine();
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
			en.step(player, "hi");
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
		}
	}

	private void testWithQuestLoaded() {
		/* TODO: when quest is finished
		loadCachedQuests();

		Engine en = ari.getEngine();

		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		*/
	}
}
