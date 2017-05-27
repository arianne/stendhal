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
package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;

public class ToysCollectorTest {
	private ToysCollector quest;

	@BeforeClass
	public static void setupFixture() {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		PlayerTestHelper.generateNPCRPClasses();
	}

	@Before
	public void setUp() throws Exception {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Anna"));
		quest = new ToysCollector();

		quest.addToWorld();
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove("Anna");
	}

	/**
	 * Tests for getNeededItems.
	 */
	@Test
	public final void testGetNeededItems() {
		assertEquals(Arrays.asList(new String[] { "teddy", "dice", "dress" }),
				quest.getNeededItems());
	}

	/**
	 * Tests for getSlotName.
	 */
	@Test
	public final void testGetSlotName() {
		assertEquals("toys_collector", quest.getSlotName());
	}

	/**
	 * Tests for getTriggerPhraseToEnumerateMissingItems.
	 */
	@Test
	public final void testGetTriggerPhraseToEnumerateMissingItems() {
		assertEquals(ConversationPhrases.EMPTY,
				quest.getTriggerPhraseToEnumerateMissingItems());
	}

	/**
	 * Tests for getAdditionalTriggerPhraseForQuest.
	 */
	@Test
	public final void testGetAdditionalTriggerPhraseForQuest() {
		assertEquals(Arrays.asList(new String[] { "toys" }),
				quest.getAdditionalTriggerPhraseForQuest());
	}

	/**
	 * Tests for shouldWelcomeAfterQuestIsCompleted.
	 */
	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		assertTrue(quest.shouldWelcomeAfterQuestIsCompleted());
	}

}
