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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.awt.Rectangle;
import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.actions.equip.DropAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Money;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.quests.DiceGambling;
import games.stendhal.server.util.Area;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Tests for the gambling in Semos tavern.
 * @author martin
 */
public class DiceDealerNPCTest extends ZonePlayerAndNPCTestImpl {

	DiceDealerNPC dealer = new DiceDealerNPC();
	private Engine en = null;

	private static final String ZONE_NAME = "int_semos_tavern_0";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME, false);
	}

	public DiceDealerNPCTest() {
		super(ZONE_NAME, "Ricardo");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		dealer.configureZone(zone, null);

		quest = new DiceGambling();
		quest.addToWorld();

		Area table = dealer.getPlayingArea();
		Rectangle tableBounds = table.getShape().getBounds();
		player.setPosition(tableBounds.x+1, tableBounds.y-1);
	}

	@Test
	public void testQuest() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get("Ricardo");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Welcome to the #gambling table, where dreams can come true.", getReply(npc));
		en.step(player, "gambling");
		assertEquals("The rules are simple: just tell me if you want to #play, pay the stake, and throw the dice on the table. The higher the sum of the upper faces is, the nicer will be your prize. Take a look at the blackboards on the wall!", getReply(npc));
		en.step(player, "play");
		assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Hey! You don't have enough money!", getReply(npc));

		equipWithMoney(player, 5000);	// Enough money for 50 rounds of gambling

		int won = 0;
		int lost = 0;

		for(int i=0; i<1000; ++i) {
			en.step(player, "play");
			assertEquals("In order to play, you have to stake 100 gold. Do you want to pay?", getReply(npc));

			en.step(player, "yes");
			String reply = getReply(npc);
			if (reply.equals("Hey! You don't have enough money!")) {
				break;
			}
			assertEquals("OK, here are the dice. Just throw them when you're ready. Good luck!", reply);

			assertTrue("lost="+lost + " won="+won, rollDice());

			reply = getReply(npc);
			System.out.println(reply);
			if (reply.matches("Sorry, player, you only have [0-9] points. You haven't won anything. Better luck next time!")) {
				++lost;
			} else {
				assertTrue("Unexpected reply: "+reply, reply.matches("Congratulations, player, you have [0-9]+ points\\. .*"));
				++won;

				// immediately drop the win (and anything other than money also), so we have enough space for the dices again
				for(;;) {
					Iterator<RPObject> it = player.getSlot("bag").iterator();
					boolean found = false;

					while(it.hasNext()) {
						RPObject obj = it.next();

						if (!(obj instanceof Money)) {
							player.drop((Item)obj);
							found = true;
							break;
						}
					}

					if (!found) {
						break;
					}
				}
			}
		}

		assertEquals("number of wins and losses should be 5000/100", 50, lost+won);
		assertFalse(player.isEquipped("money", 1)); // We should have wasted all our money.

		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	private boolean rollDice() {
		Area table = dealer.getPlayingArea();
		Rectangle tableBounds = table.getShape().getBounds();

		Item dice = player.getFirstEquipped("dice");
		if (dice == null) {
			return false;
		}

		RPObject parent = dice.getContainer();

		final RPAction action = new RPAction();
		action.put("type", "drop");
		action.put("baseitem", dice.getID().getObjectID());
		action.put(EquipActionConsts.BASE_OBJECT, parent.getID().getObjectID());
		action.put(EquipActionConsts.BASE_SLOT, dice.getContainerSlot().getName());
		action.put("x", (int)tableBounds.getCenterX());
		action.put("y", (int)tableBounds.getCenterY());

		new DropAction().onAction(player, action);

		return true;
	}
}
