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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OutfitCompatibleWithClothesCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Pizza Delivery
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Leander (the baker in Semos)
 * <li> NPC's all over the world (as customers)
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Leander gives you a pizza and tells you who ordered it, and how much
 * time you have to deliver.
 * <li> As a gimmick, you get a pizza delivery uniform.
 * <li> You walk to the customer and say "pizza".
 * <li> The customer takes the pizza. If you were fast enough, you get a tip.
 * <li> You put on your original clothes automatically.
 * </ul>
 * REWARD:
 * <ul>
 * <li> XP (Amount varies depending on customer. You only get half of the XP if
 * the pizza has become cold.)
 * <li> some karma if delivered on time (5)
 * <li> gold coins (As a tip, if you were fast enough; amount varies depending
 * on customer.)
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> As many as wanted, but you can't get a new task while you still have the
 * chance to do the current delivery on time.
 * </ul>
 */
public class PizzaDelivery extends AbstractQuest {
	private static final Logger logger = Logger.getLogger(PizzaDelivery.class);
	private final static Outfit UNIFORM = new Outfit(null, Integer.valueOf(990), null, null, null, null, null, null, null);

	/**
	 * A customer data object.
	 */
	static class CustomerData {
		/** A hint where to find the customer. */
		private final String npcDescription;

		/** The pizza style the customer likes. */
		private final String flavor;

		/** The time until the pizza should be delivered. */
		private final int expectedMinutes;

		/** The money the player should get on fast delivery. */
		private final int tip;

		/**
		 * The experience the player should gain for delivery. When the pizza
		 * has already become cold, the player will gain half of this amount.
		 */
		private final int xp;

		/**
		 * The text that the customer should say upon quick delivery. It should
		 * contain %d as a placeholder for the tip, and can optionally contain
		 * %s as a placeholder for the pizza flavor.
		 */
		private final String messageOnHotPizza;

		/**
		 * The text that the customer should say upon quick delivery. It can
		 * optionally contain %s as a placeholder for the pizza flavor.
		 */
		private final String messageOnColdPizza;

		/**
		 * The min level player who can get to this NPC
		 */
		private final int level ;

		/**
		 * Creates a CustomerData object.
		 *
		 * @param npcDescription
		 * @param flavor
		 * @param expectedTime
		 * @param tip
		 * @param xp
		 * @param messageHot
		 * @param messageCold
		 * @param level
		 */
		CustomerData(final String npcDescription, final String flavor,
				final int expectedTime, final int tip, final int xp, final String messageHot,
				final String messageCold, final int level) {
			this.npcDescription = npcDescription;
			this.flavor = flavor;
			this.expectedMinutes = expectedTime;
			this.tip = tip;
			this.xp = xp;
			this.messageOnHotPizza = messageHot;
			this.messageOnColdPizza = messageCold;
			this.level = level;
		}

		/**
		 * Get the minimum level needed for the NPC
		 *
		 * @return minimum level
		 */
		public int getLevel() {
			return level;
		}
	}

	private static final String QUEST_SLOT = "pizza_delivery";

	private static Map<String, CustomerData> customerDB;



	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("I met Leander and agreed to help with pizza delivery.");
		if (!"done".equals(questState)) {
			final String[] questData = questState.split(";");
			final String customerName = questData[0];
			final CustomerData customerData = customerDB.get(customerName);
			res.add("Leander gave me a " + customerData.flavor + " for " + customerName + ".");
			res.add("Leander told me: \"" + customerData.npcDescription + "\"");
			if (!isDeliveryTooLate(player)) {
				res.add("If I hurry I might still get there with the pizza hot.");
			} else {
				res.add("The pizza has already gone cold.");
			}
		} else {
			res.add("I delivered the last pizza Leander gave me.");
		}
		return res;
	}

	// Don't add Sally here, as it would conflict with Leander telling
	// about his daughter.
	private static void buildCustomerDatabase() {
		customerDB = new HashMap<String, CustomerData>();

		customerDB.put("Balduin",
			new CustomerData(
				"Balduin is a hermit who's living on a mountain between Semos and Ados. It's called Ados Rock. Walk east from here.",
				"Pizza Prosciutto",
				// minutes to deliver. Tested by mort: 6:30
				// min, with killing some orcs.
				7,
				// tip when delivered on time. Quite
				// high because you can't do much
				// senseful on top of the hill and must
				// walk down again.
				200,
				// experience gain for delivery
				300,
				"Thanks! I wonder how you managed to bring it up here so fast. Take these %d pieces of gold as a tip, I can't spend it up here anyway!",
				"Brrr. This %s is no longer hot. Well, thanks for the effort anyway.",
				10));

		customerDB.put("Cyk",
			new CustomerData(
				"Cyk is currently on holiday on Athor Island. You'll easily recognize him by his blue hair. Go South East to find Athor ferry.",
				"Pizza Hawaii",
				// minutes to deliver. You need about 6 min
				// to Eliza, up to 12 min to wait for the
				// ferry, 5 min for the crossing, and 0.5
				// min from the docks to the beach, so you
				// need a bit of luck for this one.
				20,
				// tip when delivered on time
				300,
				// experience gain for delivery
				500,
				"Wow, I never believed you would really deliver this half over the world! Here, take these %s bucks!",
				"It has become cold, but what do I expect when I order a pizza from a bakery so far away... So thanks anyway.",
				20));

		customerDB.put("Eliza",
			new CustomerData(
				"Eliza works for the Athor Island ferry service. You'll find her at the docks south of the Ados swamps.",
				"Pizza del Mare",
				// minutes to deliver. Tested by mort: 6
				// min, ignoring slow animals and not
				// walking through the swamps.
				7,
				// tip when delivered on time.
				170,
				// experience gain for delivery
				300,
				"Incredible! It's still hot! Here, buy something nice from these %d pieces of gold!",
				"What a pity. It has become cold. Nevertheless, thank you!",
				20));

		customerDB.put("Fidorea",
			new CustomerData(
				"Fidorea lives in Ados city. She is a makeup artist. You'll need to walk east from here.",
				"Pizza Napoli",
				// minutes to deliver. Tested by mort: about
				// 6 min, outrunning all enemies.
				7,
				// tip when delivered on time
				150,
				// experience gain for delivery
				200,
				"Thanks a lot! You're a born pizza deliverer. You can have these %d pieces of gold as a tip!",
				"Bummer. Cold pizza.",
				15));

		customerDB.put("Haizen",
			new CustomerData(
				"Haizen is a magician who lives in a hut near the road to Ados. You'll need to walk east and north from here.",
				"Pizza Diavolo",
				// minutes to deliver. Tested by kymara:
				// exactly 3 min.
				4,
				// tip when delivered on time
				80,
				// experience gain for delivery
				150,
				"Ah, my %s! And it's fresh out of the oven! Take these %d coins as a tip!",
				"I hope next time I order a pizza it's still hot.",
				10));

		customerDB.put(
			"Jenny",
			new CustomerData(
				"Jenny owns a mill in the plains north and a little east of Semos.",
				"Pizza Margherita",
				// minutes to deliver. Tested by mort: can
				// be done in 1:15 min, with no real danger.
				2,
				// tip when delivered on time
				20,
				// experience gain for delivery
				50,
				"Ah, you brought my %s! Very nice of you! Here, take %d coins as a tip!",
				"It's a shame. Your pizza service can't deliver a hot pizza although the bakery is just around the corner.",
				2));

		customerDB.put("Jynath",
			new CustomerData(
				"Jynath is a witch who lives in a small house south of Or'ril castle. You'll need to walk south west from Semos, all the way through the forest, then follow the path west till you see her hut.",
				"Pizza Funghi",
				// minutes to deliver. Tested by mort: 5:30
				// min, leaving the slow monsters on the way
				// behind.
				6,
				// tip when delivered on time
				140,
				// experience gain for delivery
				200,
				"Oh, I didn't expect you so early. Great! Usually I don't give tips, but for you I'll make an exception. Here are %d pieces of gold.",
				"Too bad... I will have to use an extra strong spell to get this pizza hot again.",
				5));

		customerDB.put("Katinka",
			new CustomerData(
				"Katinka takes care of the animals at the Ados Wildlife Refuge. That's north east of here, on the way to Ados city.",
				"Pizza Vegetale",
				// minutes to deliver. Tested by kymara in
				// 3:25 min, leaving behind the orcs.
				4,
				// tip when delivered on time
				100,
				// experience gain for delivery
				200,
				"Yay! My %s! Here, you can have %d pieces of gold as a tip!",
				"Eek. I hate cold pizza. I think I'll feed it to the animals.",
				10));

		customerDB.put("Marcus",
			new CustomerData(
				"Marcus is a guard in the Semos jail. That is due west from here, beyond Semos village.", "Pizza Tonno",
				// minutes to deliver. Tested by kymara: takes longer than before due to fence in village
				3,
				// tip when delivered on time. A bit higher than Jenny
				// because you can't do anything else in the jail and need
				// to walk out again.
				25,
				// experience gain for delivery
				100,
				"Ah, my %s! Here's your tip: %d pieces of gold.",
				"Finally! Why did that take so long?",
				2));

		customerDB.put("Nishiya",
			new CustomerData(
				"Nishiya sells sheep. You'll find him west of here, in Semos village.",
				"Pizza Pasta",
				// minutes to deliver. Tested by mort: easy
				// to do in less than 1 min.
				1,
				// tip when delivered on time
				10,
				// experience gain for delivery
				25,
				"Thank you! That was fast. Here, take %d pieces of gold as a tip!",
				"Too bad. It has become cold. Thank you anyway.",
				0));

		customerDB.put("Ouchit",
			new CustomerData(
				"Ouchit is a weapons trader. He has currently rented a room upstairs in Semos tavern, just around the corner.",
				"Pizza Quattro Stagioni",
				// minutes to deliver. Tested by mort: can
				// be done in 45 sec with no danger.
				1,
				// tip when delivered on time
				10,
				// experience gain for delivery
				25,
				"Thank you! It's nice to have a pizza service right around the corner. Here, you can have %d coins!",
				"I should have rather picked it up myself at the bakery, that would have been faster.",
				0));

		customerDB.put("Ramon",
			new CustomerData(
				"Ramon works as a blackjack dealer on the ferry to Athor Island. The ferry terminal is south east from here - it's a long way!",
				"Pizza Bolognese",
				// minutes to deliver. You need about 6 mins
				// to Eliza, and once you board the ferry,
				// about 15 sec to deliver. If you have bad
				// luck, you need to wait up to 12 mins for
				// the ferry to arrive at the mainland, so
				// you need a bit of luck for this one.
				14,
				// tip when delivered on time
				250,
				// experience gain for delivery
				400,
				"Thank you so much! Finally I get something better than the terrible food that Laura cooks. Take these %d pieces of gold as a tip!",
				"Too bad. It is cold. And I had hoped to get something better than that galley food.",
				20));

		customerDB.put("Tor'Koom",
			new CustomerData(
				"Tor'Koom is an orc who lives in the dungeon below this town, Semos. Sheep are his favourite food. He lives at the 4th level below the ground. Be careful!",
				// "Pizza sheep" in Italian ;)
				"Pizza Pecora",
				// minutes to deliver. Tested by kymara:
				// done in about 8 min, with lots of monsters getting in your way.
				9,
				// tip when delivered on time
				170,
				// experience gain for delivery
				300,
				"Yummy %s! Here, take %d moneys!",
				"Grrr. Pizza cold. You walking slow like sheep.",
				15));

		customerDB.put("Martin Farmer",
				new CustomerData(
					"Martin Farmer is holidaying in Ados City. You'll need to walk east from here.",
					"Pizza Fiorentina",
					// minutes to deliver. Time for Fidorea was 7, so 8 should be ok for martin
					8,
					// tip when delivered on time
					160,
					// experience gain for delivery
					220,
					"Ooooh, I loove fresh hot pizza, thanks. take this %d money...!",
					"Hmpf.. a cold pizza.. ok.. I will take it. But hurry up next time.",
					10));
	}

	private void startDelivery(final Player player, final EventRaiser npc) {

		final String name = Rand.rand(getAllowedCustomers(player));
		final CustomerData data = customerDB.get(name);

		final Item pizza = SingletonRepository.getEntityManager().getItem("pizza");
		pizza.setInfoString(data.flavor);
		pizza.setDescription("You see a " + data.flavor + ".");
		pizza.setBoundTo(name);

		if (player.equipToInventoryOnly(pizza)) {
    		npc.say("You must bring this "
    			+ data.flavor
    			+ " to "
    			+ Grammar.quoteHash("#" + name)
    			+ " within "
    			+ Grammar.quantityplnoun(data.expectedMinutes, "minute", "one")
    			+ ". Say \"pizza\" so that "
    			+ name
    			+ " knows that I sent you. Oh, and please wear this uniform on your way and don't drop this " + data.flavor + " on the ground! Our customers want it fresh.");
    		player.setOutfit(UNIFORM, true);
    		player.setQuest(QUEST_SLOT, name + ";" + System.currentTimeMillis());
		} else {
			npc.say("Come back when you have space to carry the pizza!");
		}
	}

	/**
	 * Get a list of customers appropriate for a player
	 *
	 * @param player the player doing the quest
	 * @return list of customer data
	 */
	private List<String> getAllowedCustomers(Player player) {
		List<String> allowed = new LinkedList<String>();
		int level = player.getLevel();
		for (Map.Entry<String, CustomerData> entry : customerDB.entrySet()) {
			if (level >= entry.getValue().getLevel()) {
				allowed.add(entry.getKey());
			}
		}
		return allowed;
	}

	/**
	 * Checks whether the player has failed to fulfil his current delivery job
	 * in time.
	 *
	 * @param player
	 *            The player.
	 * @return true if the player is too late. false if the player still has
	 *         time, or if he doesn't have a delivery to do currently.
	 */
	private boolean isDeliveryTooLate(final Player player) {
		if (player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)) {
			final String[] questData = player.getQuest(QUEST_SLOT).split(";");
			final String customerName = questData[0];
			final CustomerData customerData = customerDB.get(customerName);
			final long bakeTime = Long.parseLong(questData[1]);
			final long expectedTimeOfDelivery = bakeTime
				+ (long) 60 * 1000 * customerData.expectedMinutes;
			if (System.currentTimeMillis() > expectedTimeOfDelivery) {
				return true;
			}
		}
		return false;

	}

	private void handOverPizza(final Player player, final EventRaiser npc) {
		if (player.isEquipped("pizza")) {
			final CustomerData data = customerDB.get(npc.getName());
			for (final Item pizza : player.getAllEquipped("pizza")) {
				final String flavor = pizza.getInfoString();
				if (data.flavor.equals(flavor)) {
					player.drop(pizza);
					// Check whether the player was supposed to deliver the
					// pizza.
					if (player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)) {
						if (isDeliveryTooLate(player)) {
							if (data.messageOnColdPizza.contains("%s")) {
								npc.say(String.format(data.messageOnColdPizza, data.flavor));
							} else {
								npc.say(data.messageOnColdPizza);
							}
							player.addXP(data.xp / 2);
						} else {
							if (data.messageOnHotPizza.contains("%s")) {
								npc.say(String.format(data.messageOnHotPizza,
										data.flavor, data.tip));
							} else {
								npc.say(String.format(data.messageOnHotPizza,
										data.tip));
							}
							final StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
							money.setQuantity(data.tip);
							player.equipOrPutOnGround(money);
							player.addXP(data.xp);
							player.addKarma(5);
						}
						new InflictStatusOnNPCAction("pizza").fire(player, null, npc);
						player.setQuest(QUEST_SLOT, "done");
						putOffUniform(player);
					} else {
						// This should not happen: a player cannot pick up a pizza from the ground
						// that did have a flavor, those are bound. If a pizza has flavor the player
						// should only have got it from the quest.
						npc.say("Eek! This pizza is all dirty! Did you find it on the ground?");
					}
					return;
				}
			}
			// The player has brought the pizza to the wrong NPC, or it's a plain pizza.
			npc.say("No, thanks. I like " + data.flavor + " better.");
		} else {
			npc.say("A pizza? Where?");
		}
	}

	/** Takes away the player's uniform, if the he is wearing it.
	 * @param player to remove uniform from*/
	private void putOffUniform(final Player player) {
		if (UNIFORM.isPartOf(player.getOutfit())) {
			player.returnToOriginalOutfit();
		}
	}

	private void prepareBaker() {
		final SpeakerNPC leander = npcs.get("Leander");

		// haven't done the pizza quest before or already delivered the last one, ok to wear pizza outfit
		leander.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new OutfitCompatibleWithClothesCondition(), new QuestNotActiveCondition(QUEST_SLOT)),
				ConversationStates.QUEST_OFFERED,
				"I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?",
				null);

		// haven't done the pizza quest before or already delivered the last one, outfit would be incompatible with pizza outfit
		leander.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new OutfitCompatibleWithClothesCondition()), new QuestNotActiveCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"Sorry, you can't wear our pizza delivery uniform looking like that. If you get changed, you can ask about the #task again.",
				null);

		// pizza quest is active: check if the delivery is too late already or not
		leander.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						final String[] questData = player.getQuest(QUEST_SLOT)
								.split(";");
						final String customerName = questData[0];
						if (isDeliveryTooLate(player)) {
							// If the player still carries any pizza due for an NPC,
							// take it away because the baker is angry,
							// and because the player probably won't
							// deliver it anymore anyway.
							for (final Item pizza : player.getAllEquipped("pizza")) {
								if (pizza.getInfoString()!=null) {
									player.drop(pizza);
								}
							}
							npc.say("I see you failed to deliver the pizza to "
								+ customerName
								+ " in time. Are you sure you will be more reliable this time?");
						} else {
							npc.say("You still have to deliver a pizza to "
									+ customerName + ", and hurry!");
							npc.setCurrentState(ConversationStates.ATTENDING);
						}
				}
			});

		leander.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					startDelivery(player, npc);
				}
			});

		leander.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Too bad. I hope my daughter #Sally will soon come back from her camp to help me with the deliveries.",
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					putOffUniform(player);
				}
			});

		for (final String name : customerDB.keySet()) {
			final CustomerData data = customerDB.get(name);
			leander.addReply(name, data.npcDescription);
		}
	}

	private void prepareCustomers() {
		for (final String name : customerDB.keySet()) {
			final SpeakerNPC npc = npcs.get(name);
			if (npc == null) {
				logger.error("NPC " + name + " is used in the Pizza Delivery quest but does not exist in game.", new Throwable());
				continue;
			}

			npc.add(ConversationStates.ATTENDING, "pizza", null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						handOverPizza(player, npc);
					}
				});
		}
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Pizza Delivery",
				"Leander's pizza business is doing so well that he now recruits delivery boys and girls.",
				false);
		buildCustomerDatabase();
		prepareBaker();
		prepareCustomers();
	}

	@Override
	public String getName() {
		return "PizzaDelivery";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Leander";
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return true;
	}
}
