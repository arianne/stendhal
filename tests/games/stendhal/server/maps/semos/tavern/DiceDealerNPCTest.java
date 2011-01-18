/* $Id$ */
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

package games.stendhal.server.maps.semos.tavern;

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.actions.equip.DropAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.quests.DiceGambling;
import games.stendhal.server.util.Area;

import java.awt.Rectangle;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class DiceDealerNPCTest extends ZonePlayerAndNPCTestImpl {

	DiceDealerNPC dealer = new DiceDealerNPC();
	private Engine en = null;

	private final DiceGambling quest = new DiceGambling();
	private final String questSlot = quest.getSlotName();

	private static final String ZONE_NAME = "int_semos_tavern_0";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME, false);
	}

	public DiceDealerNPCTest() {
		super(ZONE_NAME, "Ricardo");
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		dealer.configureZone(zone, null);

		quest.addToWorld();

		Area table = dealer.getPlayingArea();
		Rectangle tableBounds = table.getShape().getBounds();
		player.setPosition(tableBounds.x+1, tableBounds.y-1);
	}

	@Test
	public void testQuest() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get("Ricardo");
		en = npc.getEngine();

		// [16:55] You read:
		// "PRIZES:
		// 18: magic chain helmet
		// 17: red cloak
		// 16: longbow
		// 15: greater potion
		// 14: home scroll
		// 13: cheeseydog"
		// 12: sandwich
		// 11: antidote
		// 10: chain legs
		// 9: studded shield
		// 8: wine
		// 7: beer"

		en.step(player, "hi");
		assertEquals("Welcome to the #gambling table, where dreams can come true.", getReply(npc));
		en.step(player, "gambling");
		assertEquals("The rules are simple: just tell me if you want to #play, pay the stake, and throw the dice on the table. The higher the sum of the upper faces is, the nicer will be your prize. Take a look at the blackboards on the wall!", getReply(npc));
		PlayerTestHelper.equipWithMoney(player, 1000);
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));

		roleDice();

/*TODO
		assertEquals("Congratulations, player, you have 11 points. This antidote will serve you well when you fight against poisonous creatures.", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));
		assertEquals("Congratulations, player, you have 7 points. That's enough for a consolation prize, a bottle of beer.", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));
		assertEquals("Congratulations, player, you have 7 points. That's enough for a consolation prize, a bottle of beer.", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));
		assertEquals("Congratulations, player, you have 15 points. You have won a greater potion, but with your luck you'll probably never have to use it!", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));
		assertEquals("Congratulations, player, you have 9 points. Take this simple shield as a reward.", getReply(npc));
		assertEquals("Congratulations, player, you have 10 points. I hope you have a use for these chain legs.", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));
		assertEquals("Congratulations, player, you have 12 points. You have won a tasty sandwich!", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", getReply(npc));
		assertEquals("Congratulations, player, you have 12 points. You have won a tasty sandwich!", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Hey! You don't have enough money!", getReply(npc));
		// [16:58] There is no easy path to that place.
		// [16:58] There is no easy path to that place.
		assertEquals("Bye.", getReply(npc));
		en.step(player, "bye");
		en.step(player, "hi");
		assertEquals("Welcome to the gambling table, where dreams can come true.", getReply(npc));
		en.step(player, "offer");
		en.step(player, "job");
		assertEquals("I'm the only person in Semos who is licensed to offer gambling activities.", getReply(npc));
		en.step(player, "help");
		assertEquals("If you are looking for Ouchit: he's upstairs.", getReply(npc));
		en.step(player, "done");
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Hey! You don't have enough money!", getReply(npc));
*/
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

	}

	private void roleDice() {
		Area table = dealer.getPlayingArea();
		Rectangle tableBounds = table.getShape().getBounds();

		Item dice = player.getFirstEquipped("dice");
		RPObject parent = dice.getContainer();

		final RPAction action = new RPAction();
		action.put("type", "drop");
		action.put("baseitem", dice.getID().getObjectID());
		action.put(EquipActionConsts.BASE_OBJECT, parent.getID().getObjectID());
		action.put(EquipActionConsts.BASE_SLOT, dice.getContainerSlot().getName());
		action.put("x", (int)tableBounds.getCenterX());
		action.put("y", (int)tableBounds.getCenterY());

		new DropAction().onAction(player, action);
	}
}
