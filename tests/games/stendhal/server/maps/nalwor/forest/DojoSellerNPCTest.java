/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.forest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.item.BreakableWeapon;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.shop.ItemShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.maps.nalwor.forest.AssassinRepairerAdder.AssassinRepairer;
import marauroa.common.game.RPEvent;
import utilities.ZonePlayerAndNPCTestImpl;


public class DojoSellerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static String npcName = "Akutagawa";
	private static String shopName = "dojo";
	private static String zoneName = "dojo_zone";


	@Override
	@Before
	public void setUp() throws Exception {
		// initialize zone, player, & NPC
		setupZone(zoneName, new DojoSellerNPC());
		setNpcNames(npcName);
		setZoneForPlayer(zoneName);
		super.setUp();

		assertThat(zone, notNullValue());
		assertThat(player, notNullValue());
		assertThat(player.getZone(), is(zone));
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		assertThat(npc, notNullValue());
		assertThat(npc.getZone(), is(zone));
		player.setPosition(npc.getX(), npc.getY() + 1);
		assertThat(player.nextTo(npc), is(true));
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		assertThat(zone.getNPCList().size(), is(0));
		assertThat(zone.getPlayers().size(), is(0));
		assertThat(SingletonRepository.getRuleProcessor().getOnlinePlayers().getAllPlayers(),
				not(contains(player)));
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		world.removeZone(zone);
		assertThat(world.getZone(zoneName), nullValue());
	}

	private ItemShopInventory getInventory() {
		return SingletonRepository.getShopsList().get(shopName, ShopType.ITEM_SELL);
	}

	private ShopSign getSign() {
		List<?> signs = zone.getEntitiesOfClass(ShopSign.class);
		assertThat(signs, not(empty()));
		return (ShopSign) signs.get(0);
	}

	private void equipTrainingSwords(int count) {
		Item base = SingletonRepository.getEntityManager().getItem("training sword");
		assertThat(base, instanceOf(BreakableWeapon.class));
		BreakableWeapon proto = (BreakableWeapon) base;
		for (int idx = 0; idx < count; idx++) {
			player.equipToInventoryOnly(new BreakableWeapon(proto));
		}
	}

	private List<BreakableWeapon> getEquippedTrainingSwords() {
		List<BreakableWeapon> swords = new LinkedList<>();
		for (Item sword: player.getAllEquipped("training sword")) {
			swords.add((BreakableWeapon) sword);
		}
		return swords;
	}

	@Test
	public void testGeneralDialogue() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		Engine en = npc.getEngine();

		en.step(player, "hi");
		assertThat(getReply(npc),
				is("If you're looking for training equipment, you have come to the right place."));
		en.step(player, "offer");
		assertThat(getReply(npc), is("See my blackboard for what I sell. I can also #repair any used"
				+ " #'training swords' that you have."));
		en.step(player, "job");
		assertThat(getReply(npc), is("I run the assassins' dojo shop where we sell equipment and do"
				+ " #repairs on #'training swords'."));
		en.step(player, "quest");
		assertThat(getReply(npc),
				is("I don't have any task for you to do. I only #fix and sell equipment."));
		en.step(player, "help");
		assertThat(getReply(npc),
				is("If you want to train in the dojo, I recommend that you buy a #'training sword'."));
		en.step(player, "training sword");
		assertThat(getReply(npc), is("My training swords are light and easy to swing. And just because"
				+ " they are made out of wood, doesn't mean that it won't hurt if you get whacked with"
				+ " one."));
		en.step(player, "bye");
		assertThat(getReply(npc), is("Bye."));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}

	@Test
	public void testInventory() {
		ItemShopInventory inventory = getInventory();
		assertThat(inventory, notNullValue());
		// shop sells 3 items
		assertThat(inventory.size(), is(3));
		String itemName = "training sword";
		int itemPrice = 2100;
		assertThat(inventory.containsKey(itemName), is(true));
		assertThat(inventory.getPrice(itemName), is(itemPrice));
		itemName = "shuriken";
		itemPrice = 80;
		assertThat(inventory.containsKey(itemName), is(true));
		assertThat(inventory.getPrice(itemName), is(itemPrice));
		itemName = "fire shuriken";
		itemPrice = 105;
		assertThat(inventory.containsKey(itemName), is(true));
		assertThat(inventory.getPrice(itemName), is(itemPrice));
	}

	@Test
	public void testCannotReadSign() {
		ShopSign sign = getSign();
		assertThat(sign, notNullValue());
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);

		assertThat(player.getNumberOfEquipped("assassins id"), is(0));
		sign.onUsed(player);
		assertThat(getReply(npc), is("Only members of the assassin guild can trade here."));
		assertThat(npc.getEngine().getCurrentState(), is(ConversationStates.IDLE));

		assertThat(player.eventsIterator().hasNext(), is(false));
	}

	@Test
	public void testCanReadSign() {
		ShopSign sign = getSign();
		assertThat(sign, notNullValue());
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);

		equipWithItem(player, "assassins id");
		assertThat(player.getNumberOfEquipped("assassins id"), is(1));
		sign.onUsed(player);
		assertThat(getReply(npc), nullValue());
		assertThat(npc.getEngine().getCurrentState(), is(ConversationStates.IDLE));

		Iterator<RPEvent> iter = player.eventsIterator();
		assertThat(iter.hasNext(), is(true));
		RPEvent event = iter.next();
		assertThat(event, notNullValue());
		assertThat(event.getName(), is("show_item_list"));
		player.clearEvents();
		assertThat(player.eventsIterator().hasNext(), is(false));
	}

	@Test
	public void testCannotTrade() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		Engine en = npc.getEngine();

		assertThat(player.getNumberOfEquipped("assassins id"), is(0));
		en.step(player, "hi");
		assertThat(getReply(npc),
				is("If you're looking for training equipment, you have come to the right place."));
		en.step(player, "buy training sword");
		assertThat(getReply(npc), is("Only members of the assassin guild can trade here."));
		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}

	@Test
	public void testCanTrade() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		Engine en = npc.getEngine();

		equipWithItem(player, "assassins id");
		assertThat(player.getNumberOfEquipped("assassins id"), is(1));
		en.step(player, "hi");
		assertThat(getReply(npc),
				is("If you're looking for training equipment, you have come to the right place."));

		// `ItemParser.getErrormessage`
		// item wasn't specified
		en.step(player, "buy");
		assertThat(getReply(npc), is("Please tell me what you want to buy."));
		// doesn't offer item
		en.step(player, "buy marbles");
		// `ItemParser.getErrormessage`
		assertThat(getReply(npc), is("Sorry, I don't sell marbles."));

		String itemName = "training sword";
		int price = 2100;
		assertThat(player.getNumberOfEquipped(itemName), is(0));
		assertThat(player.getNumberOfEquipped("money"), is(0));

		en.step(player, "buy " + itemName);
		assertThat(getReply(npc), is("A training sword will cost " + price
				+ ". Do you want to buy it?"));
		en.step(player, "no");
		assertThat(getReply(npc), is("Ok, how else may I help you?"));
		en.step(player, "buy 2 " + itemName);
		assertThat(getReply(npc), is("You can only buy one training sword at a time. A training sword"
				+ " will cost " + price + ". Do you want to buy it?"));
		en.step(player, "no");
		assertThat(getReply(npc), is("Ok, how else may I help you?"));
		en.step(player, "buy " + itemName);
		en.step(player, "yes");
		assertThat(getReply(npc), is("Sorry, you don't have enough money!"));
		equipWithMoney(player, price);
		assertThat(player.getNumberOfEquipped("money"), is(price));
		en.step(player, "buy " + itemName);
		en.step(player, "yes");
		assertThat(getReply(npc), is("Congratulations! Here is your training sword!"));
		assertThat(player.getNumberOfEquipped(itemName), is(1));

		itemName = "shuriken";
		price = 80 * 5;
		assertThat(player.getNumberOfEquipped(itemName), is(0));
		assertThat(player.getNumberOfEquipped("money"), is(0));

		en.step(player, "buy 5 " + itemName);
		assertThat(getReply(npc), is("5 shurikens will cost " + price + ". Do you want to buy them?"));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Sorry, you don't have enough money!"));
		equipWithMoney(player, price);
		assertThat(player.getNumberOfEquipped("money"), is(price));
		en.step(player, "buy 5 " + itemName);
		en.step(player, "yes");
		assertThat(getReply(npc), is("Congratulations! Here are your shurikens!"));
		assertThat(player.getNumberOfEquipped(itemName), is(5));
		assertThat(player.getNumberOfEquipped("money"), is(0));

		itemName = "fire shuriken";
		price = 105 * 10;
		assertThat(player.getNumberOfEquipped(itemName), is(0));

		en.step(player, "buy 10 " + itemName);
		assertThat(getReply(npc), is("10 fire shurikens will cost " + price
				+ ". Do you want to buy them?"));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Sorry, you don't have enough money!"));
		equipWithMoney(player, price);
		assertThat(player.getNumberOfEquipped("money"), is(price));
		en.step(player, "buy 10 " + itemName);
		en.step(player, "yes");
		assertThat(getReply(npc), is("Congratulations! Here are your fire shurikens!"));
		assertThat(player.getNumberOfEquipped(itemName), is(10));
		assertThat(player.getNumberOfEquipped("money"), is(0));

		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}

	@Test
	public void testRepairList() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		assertThat(npc, instanceOf(AssassinRepairer.class));
		AssassinRepairer repairer = (AssassinRepairer) npc;

		assertThat(repairer.getNumberOfRepairables(), is(1));
		assertThat(repairer.getFirstRepairable(), is("training sword"));
	}

	@Test
	public void testCannotRepair() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		Engine en = npc.getEngine();

		assertThat(player.getNumberOfEquipped("assassins id"), is(0));

		en.step(player, "hi");
		assertThat(getReply(npc),
				is("If you're looking for training equipment, you have come to the right place."));
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("Only members of the assassins guild can have items repaired."));
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));
		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}

	@Test
	public void testCanRepair() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		Engine en = npc.getEngine();

		equipWithItem(player, "assassins id");
		assertThat(player.getNumberOfEquipped("assassins id"), is(1));

		en.step(player, "hi");
		assertThat(getReply(npc),
				is("If you're looking for training equipment, you have come to the right place."));
		// item wasn't specified
		/* this check isn't necessary because Akutagawa repairs only one item type
		en.step(player, "repair");
		assertThat(getReply(npc), is("Please tell me what you would like repaired."));
		*/
		// does not repair item
		en.step(player, "repair black pearl");
		assertThat(getReply(npc), is("I do not repair black pearls."));
		// not carrying item
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("You don't have any #'training swords' that need repaired."));

		equipTrainingSwords(3);
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		List<BreakableWeapon> swords = getEquippedTrainingSwords();
		assertThat(swords.get(0).getUses(), is(0));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		// carrying item but doesn't need repaired
		en.step(player, "fix training sword");
		assertThat(getReply(npc), is("You don't have any #'training swords' that need repaired."));

		swords.get(0).deteriorate();
		assertThat(player.getNumberOfEquipped("money"), is(0));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		assertThat(swords.get(0).getUses(), is(1));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		final int unitPrice = 2100 / 2;
		int price = unitPrice;

		// 1 item needs repaired but not carrying money
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("You have 1 used training sword. I can repair it for " + price
				+ " money. Would you like me to do so?"));
		assertThat(en.getCurrentState(), is(ConversationStates.QUESTION_2));
		en.step(player, "yes");
		assertThat(getReply(npc), is("You don't have enough money."));

		equipWithMoney(player, price);
		assertThat(player.getNumberOfEquipped("money"), is(price));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		assertThat(swords.get(0).getUses(), is(1));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		// 1 item needs repaired but doesn't want to pay
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("You have 1 used training sword. I can repair it for " + price
				+ " money. Would you like me to do so?"));
		en.step(player, "no");
		assertThat(getReply(npc),
				is("Good luck then. Remember, once they break, they can't be repaired."));

		assertThat(player.getNumberOfEquipped("money"), is(price));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		assertThat(swords.get(0).getUses(), is(1));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		// 1 item needs repaired but dropped
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("You have 1 used training sword. I can repair it for " + price
				+ " money. Would you like me to do so?"));
		player.drop(swords.get(0));
		assertThat(player.getNumberOfEquipped("training sword"), is(2));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Did you drop the item?"));
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));

		player.equipToInventoryOnly(swords.get(0));
		assertThat(player.getNumberOfEquipped("money"), is(price));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		assertThat(swords.get(0).getUses(), is(1));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		// 1 item needs repaired and has money
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("You have 1 used training sword. I can repair it for " + price
				+ " money. Would you like me to do so?"));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Done! Your training sword is as good as new."));

		assertThat(player.getNumberOfEquipped("money"), is(0));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		assertThat(swords.get(0).getUses(), is(0));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		swords.get(0).deteriorate();
		swords.get(1).deteriorate();
		assertThat(swords.get(0).getUses(), is(1));
		assertThat(swords.get(1).getUses(), is(1));
		assertThat(swords.get(2).getUses(), is(0));

		price = unitPrice * 2;
		equipWithMoney(player, price);
		assertThat(player.getNumberOfEquipped("money"), is(price));

		// 2 items need repaired and has money
		en.step(player, "fix training sword");
		assertThat(getReply(npc), is("You have 2 used training swords. I can repair them all for "
				+ price + " money. Would you like me to do so?"));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Done! Your training swords are as good as new."));

		assertThat(player.getNumberOfEquipped("money"), is(0));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		assertThat(swords.get(0).getUses(), is(0));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		swords.get(0).deteriorate();
		swords.get(1).deteriorate();
		swords.get(2).deteriorate();
		assertThat(swords.get(0).getUses(), is(1));
		assertThat(swords.get(1).getUses(), is(1));
		assertThat(swords.get(2).getUses(), is(1));

		price = unitPrice * 3;
		equipWithMoney(player, price);
		assertThat(player.getNumberOfEquipped("money"), is(price));

		// 3 items need repaired and has money
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("You have 3 used training swords. I can repair them all for "
				+ price + " money. Would you like me to do so?"));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Done! Your training swords are as good as new."));

		assertThat(player.getNumberOfEquipped("money"), is(0));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		assertThat(swords.get(0).getUses(), is(0));
		assertThat(swords.get(1).getUses(), is(0));
		assertThat(swords.get(2).getUses(), is(0));

		swords.get(0).deteriorate();
		swords.get(1).deteriorate();

		// player tries to get discount by dropping worn item before requesting price
		player.drop(swords.get(0));
		assertThat(player.getNumberOfEquipped("training sword"), is(2));
		en.step(player, "repair training sword");
		assertThat(getReply(npc), is("You have 1 used training sword. I can repair it for " + unitPrice
				+ " money. Would you like me to do so?"));
		player.equipToInventoryOnly(swords.get(0));
		assertThat(player.getNumberOfEquipped("training sword"), is(3));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Did you drop the item?"));
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));

		// check plural request
		en.step(player, "repair training swords");
		assertThat(getReply(npc), is("You have 2 used training swords. I can repair them all for "
				+ (unitPrice * 2) + " money. Would you like me to do so?"));
		en.step(player, "no");

		// check case-insensitive request
		en.step(player, "RepAiR tRaIniNG SwOrD");
		assertThat(getReply(npc), is("You have 2 used training swords. I can repair them all for "
				+ (unitPrice * 2) + " money. Would you like me to do so?"));
		en.step(player, "no");

		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}
}
