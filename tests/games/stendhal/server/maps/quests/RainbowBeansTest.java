/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.scroll.RainbowBeansScroll;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.pad.DealerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class RainbowBeansTest {

	private Player player;
	private SpeakerNPC npc;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new DealerNPC().configureZone(zone, null);

		AbstractQuest quest = new RainbowBeans();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("bob");
		zone.add(player);
	}

	@After
	public void tearDown() {
		PlayerTestHelper.removeNPC("Pdiddi");
	}

	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Pdiddi");
		en = npc.getEngine();

		assertNull(player.getQuest(questSlot));

		en.step(player, "hi");
		assertEquals("SHHH! Don't want all n' sundry knowin' wot I #deal in.", getReply(npc));
		en.step(player, "deal");
		assertEquals("It's not stuff you're ready for, pal. Now get out of 'ere! An don't you come back till you've got more hairs on that chest!", getReply(npc));
		en.step(player, "yes");
		assertEquals("That stuff's too strong for you. No chance mate!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// player was too low level last time. make them at leastlevel 50
		player.addXP(248800);
		assertThat(player.getLevel(), greaterThanOrEqualTo(30));

		// not interested at first
		en.step(player, "hi");
		assertEquals("SHHH! Don't want all n' sundry knowin' wot I #deal in.", getReply(npc));
		en.step(player, "deal");
		assertEquals("Nosy, aint yer? I deal in rainbow beans. You take some, and who knows where the trip will take yer. It'll cost you 2000 money. And remember pal, it can end up faster than ya wanted! Risky business ya know! So, want to buy some?", getReply(npc));
		en.step(player, "no");
		assertEquals("Aight, ain't for everyone. Anythin else you want, you say so.", getReply(npc));
		en.step(player, "help");
		assertEquals("To be honest mate I can't help you with much, you're better off in the city for that.", getReply(npc));
		en.step(player, "quest");
		assertEquals("Haven't got anything for you, pal.", getReply(npc));
		en.step(player, "job");
		assertEquals("I think you already know what I do.", getReply(npc));
		en.step(player, "offer");
		assertEquals("Ha! The sign on the door's a cover! This is no inn. If you want a drink, you better go back into town.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertFalse(player.isEquipped("rainbow beans"));
		assertFalse(player.isEquipped("money"));

		// don't have money
		en.step(player, "hi");
		assertEquals("SHHH! Don't want all n' sundry knowin' wot I #deal in.", getReply(npc));
		en.step(player, "deal");
		assertEquals("Nosy, aint yer? I deal in rainbow beans. You take some, and who knows where the trip will take yer. It'll cost you 2000 money. And remember pal, it can end up faster than ya wanted! Risky business ya know! So, want to buy some?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Scammer! You don't have the cash.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		PlayerTestHelper.equipWithMoney(player, 2000);

		en.step(player, "hi");
		assertEquals("SHHH! Don't want all n' sundry knowin' wot I #deal in.", getReply(npc));
		en.step(player, "deal");
		assertEquals("Nosy, aint yer? I deal in rainbow beans. You take some, and who knows where the trip will take yer. It'll cost you 2000 money. And remember pal, it can end up faster than ya wanted! Risky business ya know! So, want to buy some?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Alright, here's the beans. Once you take them, you come down in about 30 minutes. And if you get nervous up there, hit one of the green panic squares to take you back here.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		assertTrue(player.isEquipped("rainbow beans"));
		assertFalse(player.isEquipped("money"));

		assertNotNull(player.getQuest(questSlot));

		// return too soon
		en.step(player, "hi");
		assertEquals("Alright? I hope you don't want more beans. You can't take more of that stuff for at least another 6 hours.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		player.setQuest(questSlot,"bought;0;taken;0");

		// [11:36] Admin superkym changed your state of the quest 'rainbow_beans' from 'bought;1289129695296;taken;-1' to 'bought;0;taken;0'
		en.step(player, "hi");
		assertEquals("Oi, you. Back for more rainbow beans?", getReply(npc));
		en.step(player, "no");
		assertEquals("Aight, ain't for everyone. Anythin else you want, you say so.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// repeat quest
		PlayerTestHelper.equipWithMoney(player, 2000);
		en.step(player, "hi");
		assertEquals("Oi, you. Back for more rainbow beans?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Alright, here's the beans. Once you take them, you come down in about 30 minutes. And if you get nervous up there, hit one of the green panic squares to take you back here.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Check for proper behavior when beans are used too soon after previous
	 * use.
	 */
	@Test
	public void testTooFastUse() {
		long now = System.currentTimeMillis();
		player.setQuest(questSlot,"bought;" + now + ";taken;" + now);
		PlayerTestHelper.equipWithItem(player, "rainbow beans");
		Item beans = player.getFirstEquipped("rainbow beans");
		if (beans instanceof RainbowBeansScroll) {
			((RainbowBeansScroll) beans).onUsed(player);
		} else {
			fail();
		}
		assertEquals("You were just sick from overuse of the rainbow beans. Classy!", PlayerTestHelper.getPrivateReply(player));
		assertFalse("Beans should be used up", player.isEquipped("rainbow beans"));
	}
}
