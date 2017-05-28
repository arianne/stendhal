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
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.mithrilbourgh.throne_room.BuyerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class KillEnemyArmyTest {

	private Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;
	final static KillEnemyArmy quest = new KillEnemyArmy();
	private static StendhalRPZone playerzone;
	private final static int Xpos = 10;
	private final static int Ypos = 10;
	private static Logger logger = Logger.getLogger(KillEnemyArmyTest.class);
	private final String QUEST_SLOT = quest.getSlotName();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		playerzone = new StendhalRPZone("int_semos_guard_house",100,100);
		SingletonRepository.getRPWorld().addRPZone(playerzone);

		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new BuyerNPC().configureZone(zone, null);
		npc = SingletonRepository.getNPCList().get("Despot Halb Errvl");
		en = npc.getEngine();
		quest.addToWorld();
	}

	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		PlayerTestHelper.registerPlayer(player);
		PlayerTestHelper.equipWithItem(player, "money");

		player.teleport(playerzone, Xpos, Ypos, null, player);
	}

	/**
	 * function for emulating killing of quest monsters by player.
	 * @param player - killer
	 * @param numb - number of creatures for killing
	 */
	public void KillRandomMonsters(final Player player, int numb) {
		List<String> monsters = quest.enemys.get(player.getQuest(QUEST_SLOT, 1));
		for(int i=0; i<numb; i++) {
			if(Rand.throwCoin()==0) {
				player.setSoloKill(monsters.get(Rand.rand((monsters.size()-1))));
			} else {
				player.setSharedKill(monsters.get(Rand.rand((monsters.size()-1))));
			}
		}
		logger.debug("killed "+ numb + " creatures.");
	}

	@Test
	public void TestChatting() {
		LinkedList<String> questHistory = new LinkedList<String>();
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");
		// we have to write here which enemy type player got.
		final String monstersType=player.getQuest(QUEST_SLOT, 1);
		final int killsnumb=quest.enemyForces.get(monstersType).first();
		final String expectingAnswer = quest.enemyForces.get(monstersType).second();
		questHistory.add("Despot Halb Errvl asked me to kill "+
				killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType));
        questHistory.add("Currently I have killed no "+ Grammar.plnoun(0, monstersType));
        questHistory.add(killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType)+" left to kill.");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("I need help to defeat #enemy "+monstersType+
				" armies. They are a grave concern. Kill at least "+killsnumb+
				" of any "+monstersType+
				" soldiers and I will reward you.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I already explained to you what I need. Are you an idiot, as you can't remember this simple thing about the #enemy "+monstersType+" armies?", getReply(npc));
		en.step(player, "enemy");
		assertEquals(expectingAnswer, getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void TestKilling() {
		LinkedList<String> questHistory = new LinkedList<String>();
		int killed=0;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");

		// we have to write here which enemy type player got.
		final String monstersType=player.getQuest(QUEST_SLOT, 1);
		final int killsnumb=quest.enemyForces.get(monstersType).first();
		//final String expectingAnswer = quest.enemyForces.get(monstersType).second();
		questHistory.add("Despot Halb Errvl asked me to kill "+
				killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType));
        questHistory.add("Currently I have killed no "+ Grammar.plnoun(0, monstersType));
        questHistory.add(killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType)+" left to kill.");
		assertEquals(questHistory, quest.getHistory(player));
		assertEquals("I need help to defeat #enemy "+monstersType+
				" armies. They are a grave concern. Kill at least "+killsnumb+
				" of any "+monstersType+
				" soldiers and I will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

        // kill 1 creature.
        killed=1;
		KillRandomMonsters(player, killed);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("You killed only "+killed+" "+Grammar.plnoun(killed, player.getQuest(QUEST_SLOT, 1))+
		". You have to kill at least "+killsnumb+" "+Grammar.plnoun(killed, player.getQuest(QUEST_SLOT, 1)), getReply(npc));
		questHistory.clear();
		questHistory.add("Despot Halb Errvl asked me to kill "+
				killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType));
        questHistory.add("Currently I have killed "+
				killed+" "+
				Grammar.plnoun(killed, monstersType));
        questHistory.add((killsnumb-killed)+" "+
				Grammar.plnoun(killsnumb-killed, monstersType)+" left to kill.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// kill creatures to have full number of killed ones.
		KillRandomMonsters(player, killsnumb-killed);
		questHistory.clear();
		questHistory.add("Despot Halb Errvl asked me to kill "+
				killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType));
        questHistory.add("Currently I have killed "+
				killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType));
        questHistory.add("I have killed enough creatures to get my reward now.");
		assertEquals(questHistory, quest.getHistory(player));

		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		int tempxp = 1000000;
		player.setXP(tempxp);
		int tempmoneys = player.getEquippedItemClass("bag", "money").getQuantity();
		double tempkarma = player.getKarma();
		en.step(player, "quest");
        int moneys = player.getEquippedItemClass("bag", "money").getQuantity();
        int moneysdiff = moneys - tempmoneys;
        assertEquals("Good work! Take these " + moneysdiff + " coins. And if you need an assassin job again, ask me in one week. My advisors tell me they may try to fight me again.", getReply(npc));
        assertEquals(tempxp, player.getXP()-100000);
        assertTrue((moneysdiff>9999)&&(moneysdiff<60001));
        assertTrue((moneysdiff-(moneysdiff/10000)*10000)==0);
        assertEquals(tempkarma, player.getKarma()-5, 0.000001);
        questHistory.clear();
        questHistory.add("I completed Despot's Halb Errvl task and got my reward!");
        questHistory.add("I've bloodthirstily slain 1 whole army for Despot Halb Errvl.");
		assertEquals(questHistory, quest.getHistory(player));

        en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void TestExtraKilling() {
		LinkedList<String> questHistory = new LinkedList<String>();
		int killed=0;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");

		// we have to write here which enemy type player got.
		final String monstersType=player.getQuest(QUEST_SLOT, 1);
		final int killsnumb=quest.enemyForces.get(monstersType).first();

		assertEquals("I need help to defeat #enemy "+monstersType+
				" armies. They are a grave concern. Kill at least "+killsnumb+
				" of any "+monstersType+
				" soldiers and I will reward you.", getReply(npc));
		questHistory.add("Despot Halb Errvl asked me to kill "+
				killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType));
        questHistory.add("Currently I have killed no " + Grammar.plnoun(0, monstersType));
        questHistory.add(killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType)+" left to kill.");
		assertEquals(questHistory, quest.getHistory(player));

		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// will kill 2x monsters for get 15 karma in total
		killed=killsnumb*2;

		double tempkarma = player.getKarma();
		KillRandomMonsters(player, killed);
		questHistory.clear();
		questHistory.add("Despot Halb Errvl asked me to kill "+
				killsnumb+" "+
				Grammar.plnoun(killsnumb, monstersType));
        questHistory.add("Currently I have killed "+
				killed+" "+
				Grammar.plnoun(killed, monstersType));
        questHistory.add("I have killed enough creatures to get my reward now.");
		assertEquals(questHistory, quest.getHistory(player));

		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		int tempmoneys = player.getEquippedItemClass("bag", "money").getQuantity();

		en.step(player, "quest");
        int moneys = player.getEquippedItemClass("bag", "money").getQuantity();
        int moneysdiff = moneys - tempmoneys;
		assertEquals("Pretty good! You killed "+(killed-killsnumb)+
				" extra " +	Grammar.plnoun(killed-killsnumb, "soldier")+
				"! Take these " + moneysdiff + " coins, and remember, I may wish you to do this job again in one week!", getReply(npc));
		assertEquals(tempkarma, player.getKarma()-15.0, 0.000001);
		questHistory.clear();
        questHistory.add("I completed Despot's Halb Errvl task and got my reward!");
        questHistory.add("I've bloodthirstily slain 1 whole army for Despot Halb Errvl.");
		assertEquals(questHistory, quest.getHistory(player));

		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void TestRequestQuestAgain() {
		player.setQuest(QUEST_SLOT, "done;"+System.currentTimeMillis());
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");
		String reply = getReply(npc);
		assertTrue(reply.equals("You have to check again in 7 days.") ||
				   reply.equals("You have to check again in 1 week."));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
