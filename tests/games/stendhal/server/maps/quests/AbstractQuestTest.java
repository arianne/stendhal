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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class AbstractQuestTest {
	private static final class Mockquest extends AbstractQuest {
		@Override
		public String getSlotName() {
			return QUESTSlotSTRING;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public List<String> getHistory(final Player player) {
			return new ArrayList<String>();
		}

		@Override
		public String getNPCName() {
			return QUESTNPCNAME;
		}

		@Override
		public void addToWorld() {
			// do nothing
		}
	}
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}


	private static String QUESTSlotSTRING = "TESTQUEST";

	private static String QUESTNAMESTRING = "test quest name";

	private static String QUESTNPCNAME = "test npc name";


	/**
	 * Tests for getHintGetHistory.
	 */
	@Test
	public final void testGetHintGetHistory() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest(QUESTSlotSTRING, null);
		final AbstractQuest quest = new Mockquest();
		assertTrue(quest.getHint(pl).isEmpty());
		assertTrue(quest.getHistory(pl).isEmpty());
	}

	/**
	 * Tests for getLevel.
	 */
	@Test
	public final void testGetMinLevel() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest(QUESTSlotSTRING, null);
		final AbstractQuest quest = new Mockquest();
		assertTrue(quest.getMinLevel()==0);
	}

	/**
	 * Tests for isCompleted.
	 */
	@Test
	public final void testIsCompleted() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest(QUESTSlotSTRING, null);
		final AbstractQuest quest = new Mockquest();

		assertFalse(quest.isCompleted(pl));

		pl.setQuest(QUESTSlotSTRING, "done");
		assertTrue(pl.hasQuest(QUESTSlotSTRING));
		assertTrue(pl.isQuestCompleted(QUESTSlotSTRING));
		assertTrue(quest.isCompleted(pl));

		pl.setQuest(QUESTSlotSTRING, "rejected");
		assertTrue(pl.hasQuest(QUESTSlotSTRING));
		assertFalse(pl.isQuestCompleted(QUESTSlotSTRING));
		assertFalse(quest.isCompleted(pl));

		pl.setQuest(QUESTSlotSTRING, "failed");
		assertTrue(pl.hasQuest(QUESTSlotSTRING));
		assertFalse(pl.isQuestCompleted(QUESTSlotSTRING));
		assertFalse(quest.isCompleted(pl));

	}

	/**
	 * Tests for isRepeatable.
	 */
	@Test
	public final void testIsRepeatable() {
		final AbstractQuest quest = new Mockquest();
		assertFalse("abstract quests are not repeatable by default",
				quest.isRepeatable(null));
	}

	/**
	 * Tests for isStarted.
	 */
	@Test
	public final void testIsStarted() {
		final AbstractQuest quest = new Mockquest();
		final Player pl = PlayerTestHelper.createPlayer("player");
		assertFalse(quest.isStarted(pl));
		pl.setQuest(QUESTSlotSTRING, "whatever");
	}

	/**
	 * Tests for isStartedthrowsNPEwithnullArgument.
	 */
	@Test(expected = NullPointerException.class)
	public final void testIsStartedthrowsNPEwithnullArgument() {
		final AbstractQuest quest = new Mockquest();
		assertFalse(quest.isStarted(null));
	}

	/**
	 * Tests for getName.
	 */
	@Test
	public final void testGetName() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest(QUESTNAMESTRING, null);
		final AbstractQuest quest = new AbstractQuest() {

			@Override
			public String getSlotName() {
				return null;
			}

			@Override
			public String getName() {
				return QUESTNAMESTRING;
			}

			@Override
			public List<String> getHistory(final Player player) {
				return new ArrayList<String>();
			}

			@Override
			public void addToWorld() {
				// do nothing
			}
		};


		assertEquals(QUESTNAMESTRING, quest.getName());
	}

	/**
	 * Tests for isVisibleOnQuestStatus.
	 */
	@Test
	public final void testIsVisibleOnQuestStatus() {
		final AbstractQuest quest = new Mockquest();
		assertTrue("abstract quests are visible by default",
				quest.isVisibleOnQuestStatus());
	}

	/**
	 * Tests for getNPCName.
	 */
	@Test
	public final void testGetNPCName() {
		final AbstractQuest quest = new Mockquest();

		assertEquals(QUESTNPCNAME, quest.getNPCName());
	}

}
