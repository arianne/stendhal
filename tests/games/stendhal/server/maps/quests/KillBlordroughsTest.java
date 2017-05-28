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
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.PoisonStatus;
import games.stendhal.server.entity.status.StatusType;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.mithrilbourgh.throne_room.BuyerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class KillBlordroughsTest {

	private Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;
	final static KillBlordroughs quest = new KillBlordroughs();
	private static StendhalRPZone playerzone;
	private final static int Xpos = 10;
	private final static int Ypos = 10;
	private static Logger logger = Logger.getLogger(KillBlordroughsTest.class);

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
		PlayerTestHelper.equipWithItem(player, "golden blade");
		PlayerTestHelper.equipWithItem(player, "mithril cloak");
		PlayerTestHelper.equipWithItem(player, "mithril boots");
		PlayerTestHelper.equipWithItem(player, "mithril legs");
		PlayerTestHelper.equipWithItem(player, "mithril armor");
		PlayerTestHelper.equipWithItem(player, "black helmet");
		PlayerTestHelper.equipWithItem(player, "money");


		player.setAdminLevel(1000);
		player.addXP(2000000000);
		player.setAtkXP(100000000);
		player.setDefXP(100000000);
		player.setXP(100000000);
		player.setHP(10000);
		player.addKarma(10000);
		//player.setInvisible(true);
		player.teleport(playerzone, Xpos, Ypos, null, player);
	}

	public void KillRandomBlordrough() {
		final LinkedList<Creature> blrs = quest.getBlordroughs();
		Creature blr = blrs.get(Rand.rand(blrs.size()));
		// cheat! :-)
		blr.setHP(1);
		StendhalRPAction.placeat(playerzone, blr, Xpos+1, Ypos);
		player.setTarget(blr);
		do {
			// prevent player killing
			player.setHP(10000);
			if(player.hasStatus(StatusType.POISONED)) {
				player.getStatusList().removeAll(PoisonStatus.class);
			}
			player.teleport(blr.getZone(), blr.getX(), blr.getY(), null, player);
			player.setTarget(blr);
			MockStendlRPWorld.get().nextTurn();
			MockStendhalRPRuleProcessor.get().beginTurn();
			MockStendhalRPRuleProcessor.get().endTurn();
		} while (player.isAttacking());
		MockStendhalRPRuleProcessor.get().beginTurn();
		MockStendhalRPRuleProcessor.get().endTurn();
		logger.debug("killed creature ("+blr.getName()+").");
	}

	/**
	 * function for emulating killing of blordrough soldiers by player.
	 * @param numb - number of creatures for killing
	 */
	public void KillRandomBlordroughs(int numb) {
		for(int i=0; i<numb; i++) {
			KillRandomBlordrough();
		}
		logger.debug("killed "+ numb + " creatures.");
	}

	@Test
	public void TestChatting() {
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");
		assertEquals("I need help in battles with #Blordrough warriors. "+
				"They really annoying me. Kill at least 100 of any "+
				"blordrough soldiers and i will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I already explained to you what i need. Are you an idiot, as you cant remember this simple thing about #blordroughs?", getReply(npc));
		en.step(player, "blordrough");
		assertEquals("My Mithrilbourgh army have great losses in battles with Blordrough soldiers. They coming from side of Ados tunnels.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void TestKilling() {
		int killed;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I need help in battles with #Blordrough warriors. "+
				"They really annoying me. Kill at least 100 of any "+
				"blordrough soldiers and i will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		killed = quest.killsnumber-1;
		KillRandomBlordroughs(killed);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("You killed only "+killed+" blordrough "+
				Grammar.plnoun(killed, "soldier")+".", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// make it full number.
		KillRandomBlordroughs(1);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		int tempxp = player.getXP();
		int tempmoneys = player.getEquippedItemClass("bag", "money").getQuantity();
		double tempkarma = player.getKarma();
		en.step(player, "quest");
		assertEquals("Good work! Take this moneys. And if you will need assassin job again, ask me in one week. I think they will try to fight me again.", getReply(npc));
        assertEquals(tempxp, player.getXP()-500000);
        assertEquals(tempmoneys, player.getEquippedItemClass("bag", "money").getQuantity()-50000);
        assertEquals(tempkarma, player.getKarma()-5, 0.000001);
        en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void TestExtraKilling() {
		int killed;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");
		assertEquals("I need help in battles with #Blordrough warriors. "+
				"They really annoying me. Kill at least 100 of any "+
				"blordrough soldiers and i will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// killing 351 creature
		killed = quest.killsnumber*3+quest.killsnumber/2+1;
		KillRandomBlordroughs(killed);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		double tempkarma = player.getKarma();
		en.step(player, "quest");
		assertEquals("Pretty good! You killed "+(killed-quest.killsnumber)+" extra "+
				Grammar.plnoun(killed-quest.killsnumber, "soldier")+
				"! Take this moneys, and remember, i may wish you to do this job again in one week!",
				getReply(npc));
		assertEquals(tempkarma, player.getKarma()-30, 0.000001);
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
