/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;

public class KillsForQuestCounterTest {

	private static final String QUEST_STATE_MONKS_TO_KILL_SOLO = "monk,25,0,0,0";
	private static final String QUEST_STATE_MONKS_TO_KILL_SHARED = "monk,0,25,0,0";

	private KillsForQuestCounter counter;
	private Player player;

	@Before
	public void setUp() {
		player = createMock(Player.class);
	}

	@Test
	public void testRemainingKillsRequiredSharedKilledShared() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SHARED);
		expect(player.getSoloKill("monk")).andReturn(0);
		expect(player.getSharedKill("monk")).andReturn(10);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(15, remainingKills);
		verify(player);
	}

	@Test
	public void testRemainingKillsRequiredSharedKilledSharedMoreThanNeeded() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SHARED);
		expect(player.getSoloKill("monk")).andReturn(0);
		expect(player.getSharedKill("monk")).andReturn(30);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(0, remainingKills);
		verify(player);
	}

	@Test
	public void testRemainingKillsRequiredSharedKilledSolo() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SHARED);
		expect(player.getSoloKill("monk")).andReturn(10);
		expect(player.getSharedKill("monk")).andReturn(0);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(15, remainingKills);
		verify(player);
	}

	@Test
	public void testRemainingKillsRequiredSharedKilledSoloAndShared() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SHARED);
		expect(player.getSoloKill("monk")).andReturn(5);
		expect(player.getSharedKill("monk")).andReturn(5);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(15, remainingKills);
		verify(player);
	}

	@Test
	public void testRemainingKillsRequiredSoloKilledShared() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SOLO);
		expect(player.getSoloKill("monk")).andReturn(0);
		expect(player.getSharedKill("monk")).andReturn(10);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(25, remainingKills);
		verify(player);
	}

	@Test
	public void testRemainingKillsRequiredSoloKilledSolo() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SOLO);
		expect(player.getSoloKill("monk")).andReturn(10);
		expect(player.getSharedKill("monk")).andReturn(0);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(15, remainingKills);
		verify(player);
	}

	@Test
	public void testRemainingKillsRequiredSoloKilledSoloMoreThanNeeded() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SOLO);
		expect(player.getSoloKill("monk")).andReturn(30);
		expect(player.getSharedKill("monk")).andReturn(0);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(0, remainingKills);
		verify(player);
	}

	@Test
	public void testRemainingKillsRequiredSoloKilledSoloAndShared() {
		counter = new KillsForQuestCounter(QUEST_STATE_MONKS_TO_KILL_SOLO);
		expect(player.getSoloKill("monk")).andReturn(5);
		expect(player.getSharedKill("monk")).andReturn(5);

		replay(player);

		int remainingKills = counter.remainingKills(player, "monk");

		assertEquals(20, remainingKills);
		verify(player);
	}
}
