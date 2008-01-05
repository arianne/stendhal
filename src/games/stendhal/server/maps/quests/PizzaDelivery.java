package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QUEST: Pizza Delivery
 * 
 * PARTICIPANTS: - Leander (the baker in Semos) - NPC's all over the world (as
 * customers)
 * 
 * STEPS: - Leander gives you a pizza and tells you who ordered it, and how much
 * time you have to deliver. - As a gimmick, you get a pizza delivery uniform. -
 * You walk to the customer and say "pizza". - The customer takes the pizza. If
 * you were fast enough, you get a tip. - You put on your original clothes
 * automatically.
 * 
 * REWARD: - XP (Amount varies depending on customer. You only get half of the
 * XP if the pizza has become cold.) - gold coins (As a tip, if you were fast
 * enough; amount varies depending on customer.)
 * 
 * REPETITIONS: - As many as wanted, but you can't get a new task while you
 * still have the chance to do the current delivery on time.
 */
public class PizzaDelivery extends AbstractQuest {

	private static final Outfit UNIFORM = new Outfit(null, null, Integer.valueOf(90), null);

	/**
	 * A customer data object.
	 */
	static class CustomerData {
		/** A hint where to find the customer. */
		private String npcDescription;

		/** The pizza style the customer likes. */
		private String flavor;

		/** The time until the pizza should be delivered. */
		private int expectedMinutes;

		/** The money the player should get on fast delivery. */
		private int tip;

		/**
		 * The experience the player should gain for delivery. When the pizza
		 * has already become cold, the player will gain half of this amount.
		 */
		private int xp;

		/**
		 * The text that the customer should say upon quick delivery. It should
		 * contain %d as a placeholder for the tip, and can optionally contain
		 * %s as a placeholder for the pizza flavor.
		 */
		private String messageOnHotPizza;

		/**
		 * The text that the customer should say upon quick delivery. It can
		 * optionally contain %s as a placeholder for the pizza flavor.
		 */
		private String messageOnColdPizza;

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
		 */
		CustomerData(String npcDescription, String flavor,
				int expectedTime, int tip, int xp, String messageHot,
				String messageCold) {
			this.npcDescription = npcDescription;
			this.flavor = flavor;
			this.expectedMinutes = expectedTime;
			this.tip = tip;
			this.xp = xp;
			this.messageOnHotPizza = messageHot;
			this.messageOnColdPizza = messageCold;
		}
	}

	private static final String QUEST_SLOT = "pizza_delivery";

	private static Map<String, CustomerData> customerDB;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	// This could maybe be more precise.
	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("QUEST_ACCEPTED");
		return res;
	}

	// You can't enter NPC's with spaces in their names, see TODOs below.
	// Don't add Sally here, as it would conflict with Leander telling
	// about his daughter.
	// Don't add NPC's that are only reachable for high-level players.
	private static void buildCustomerDatabase() {
		customerDB = new HashMap<String, CustomerData>();

		customerDB.put("Balduin",
			new CustomerData(
				"Balduin is a hermit who's living on a mountain between Semos and Ados.",
				"Pizza Prosciutto",
				7, // minutes to deliver. Tested by mort: 6:30
					// min, with killing some orcs.
				200, // tip when delivered on time. Quite
						// high because you can't do much
						// senseful on top of the hill and must
						// walk down again.
				30, // experience gain for delivery
				"Thanks! I wonder how you managed to bring it up here so fast. Take these %d pieces of gold as a tip, I can't spend it up here anyway!",
				"Brrr. This %s is no longer hot. Well, thanks for the effort anyway."));

		customerDB.put("Cyk",
			new CustomerData(
				"Cyk is currently on holiday on Athor Island. You'll easily recognize him by his blue hair.",
				"Pizza Hawaii",
				20, // minutes to deliver. You need about 6 min
					// to Eliza, up to 12 min to wait for the
					// ferry, 5 min for the crossing, and 0.5
					// min from the docks to the beach, so you
					// need a bit of luck for this one.
				300, // tip when delivered on time
				50, // experience gain for delivery
				"Wow, I never believed you would really deliver this half over the world! Here, take these %s bucks!",
				"It has become cold, but what do I expect when I order a pizza from a bakery so far away... So thanks anyway."));

		customerDB.put("Eliza",
			new CustomerData(
				"Eliza works for the Athor Island ferry service. You'll find her at the docks south of the Ados swamps.",
				"Pizza del Mare",
				7, // minutes to deliver. Tested by mort: 6
					// min, ignoring slow animals and not
					// walking through the swamps.
				170, // tip when delivered on time.
				30, // experience gain for delivery
				"Incredible! It's still hot! Here, buy something nice from these %d pieces of gold!",
				"What a pity. It has become cold. Nevertheless, thank you!"));

		customerDB.put("Fidorea",
			new CustomerData(
				"Fidorea lives in Ados city. She is a makeup artist.",
				"Pizza Napoli",
				7, // minutes to deliver. Tested by mort: about
					// 6 min, outrunning all enemies.
				150, // tip when delivered on time
				20, // experience gain for delivery
				"Thanks a lot! You're a born pizza deliverer. You can have these %d pieces of gold as a tip!",
				"Bummer. Cold pizza."));

		customerDB.put("Haizen",
			new CustomerData(
				"Haizen is a magician who lives in a hut near the road to Ados.",
				"Pizza Diavolo",
				4, // minutes to deliver. Tested by kymara:
					// exactly 3 min.
				80, // tip when delivered on time
				15, // experience gain for delivery
				"Ah, my %s! And it's fresh out of the oven! Take these %d coins as a tip!",
				"I hope next time I order a pizza it's still hot."));

		customerDB.put(
			"Jenny",
			new CustomerData(
				"Jenny owns a mill near Semos.",
				"Pizza Margherita",
				2, // minutes to deliver. Tested by mort: can
					// be done in 1:15 min, with no real danger.
				20, // tip when delivered on time
				10, // experience gain for delivery
				"Ah, you brought my %s! Very nice of you! Here, take %d coins as a tip!",
				"It's a shame. Your pizza service can't deliver a hot pizza although the bakery is just around the corner."));

		customerDB.put("Jynath",
			new CustomerData(
				"Jynath is a witch who lives in a small house south of Or'ril castle.",
				"Pizza Funghi",
				6, // minutes to deliver. Tested by mort: 5:30
					// min, leaving the slow monsters on the way
					// behind.
				140, // tip when delivered on time
				20, // experience gain for delivery
				"Oh, I didn't expect you so early. Great! Usually I don't give tips, but for you I'll make an exception. Here are %d pieces of gold.",
				"Too bad... I will have to use an extra strong spell to get this pizza hot again."));

		customerDB.put("Katinka",
			new CustomerData(
				"Katinka takes care of the animals at the Ados Wildlife Refuge.",
				"Pizza Vegetale",
				4, // minutes to deliver. Tested by kymara in
					// 3:25 min, leaving behind the orcs.
				100, // tip when delivered on time
				20, // experience gain for delivery
				"Yay! My %s! Here, you can have %d pieces of gold as a tip!",
				"Eek. I hate cold pizza. I think I'll feed it to the animals."));

		customerDB.put("Marcus", 
			new CustomerData(
				"Marcus is a guard in the Semos jail.", "Pizza Tonno",
				2, // minutes to deliver. Tested by mort: can be done in 90 sec
					// with no danger.
				25, // tip when delivered on time. A bit higher than Jenny
					// because you can't do anything else in the jail and need
					// to walk out again.
				10, // experience gain for delivery
				"Ah, my %s! Here's your tip: %d pieces of gold.",
				"Finally! Why did that take so long?"));

		customerDB.put("Nishiya",
			new CustomerData(
				"Nishiya sells sheep. You'll find him west of here.",
				"Pizza Pasta",
				1, // minutes to deliver. Tested by mort: easy
					// to do in less than 1 min.
				10, // tip when delivered on time
				5, // experience gain for delivery
				"Thank you! That was fast. Here, take %d pieces of gold as a tip!",
				"Too bad. It has become cold. Thank you anyway."));

		customerDB.put("Ouchit",
			new CustomerData(
				"Ouchit is a weapons trader. He has currently rented a room in the tavern, just around the corner.",
				"Pizza Quattro Stagioni",
				1, // minutes to deliver. Tested by mort: can
					// be done in 45 sec with no danger.
				10, // tip when delivered on time
				5, // experience gain for delivery
				"Thank you! It's nice to have a pizza service right around the corner. Here, you can have %d coins!",
				"I should have rather picked it up myself at the bakery, that would have been faster."));

		customerDB.put("Ramon",
			new CustomerData(
				"Ramon works as a blackjack dealer on the ferry to Athor Island.",
				"Pizza Bolognese",
				14, // minutes to deliver. You need about 6 mins
					// to Eliza, and once you board the ferry,
					// about 15 sec to deliver. If you have bad
					// luck, you need to wait up to 12 mins for
					// the ferry to arrive at the mainland, so
					// you need a bit of luck for this one.
				250, // tip when delivered on time
				40, // experience gain for delivery
				"Thank you so much! Finally I get something better than the terrible food that Laura cooks. Take these %d pieces of gold as a tip!",
				"Too bad. It is cold. And I had hoped to get something better than that galley food."));

		customerDB.put("Tor'Koom",
			new CustomerData(
				"Tor'Koom is an orc who lives in the dungeon below this town. Sheep are his favourite food.",
				"Pizza Pecora", // "Pizza sheep" in Italian ;)
				9, // minutes to deliver. Tested by kymara:
					// done in about 8 min, with lots of
					// monsters getting in your way.
				170, // tip when delivered on time
				30, // experience gain for delivery
				"Yummy %s! Here, take %d moneys!",
				"Grrr. Pizza cold. You walking slow like sheep."));
	}

	private void startDelivery(Player player, SpeakerNPC npc) {
		String name = Rand.rand(customerDB.keySet());
		CustomerData data = customerDB.get(name);

		Item pizza = StendhalRPWorld.get().getRuleManager().getEntityManager()
				.getItem("pizza");
		pizza.setInfoString(data.flavor);
		pizza.setDescription("You see a " + data.flavor + ".");
		player.equip(pizza, true);

		// TODO: If there's a space in the NPC name, colorization won't work.
		npc.say("You must bring this "
			+ data.flavor
			+ " to #"
			+ name
			+ " within "
			+ Grammar.quantityplnoun(data.expectedMinutes, "minute")
			+ ". Say \"pizza\" so that "
			+ name
			+ " knows that I sent you. Oh, and please wear this uniform on your way.");
		player.setOutfit(UNIFORM, true);
		player.setQuest(QUEST_SLOT, name + ";" + System.currentTimeMillis());
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
	private boolean isDeliveryTooLate(Player player) {
		if (player.hasQuest(QUEST_SLOT)) {
			String[] questData = player.getQuest(QUEST_SLOT).split(";");
			String customerName = questData[0];
			CustomerData customerData = customerDB.get(customerName);
			long bakeTime = Long.parseLong(questData[1]);
			long expectedTimeOfDelivery = bakeTime 
				+ (long) 60 * 1000 * customerData.expectedMinutes;
			if (System.currentTimeMillis() > expectedTimeOfDelivery) {
				return true;
			}
		}
		return false;

	}

	private void handOverPizza(Player player, SpeakerNPC npc) {
		if (player.isEquipped("pizza")) {
			CustomerData data = customerDB.get(npc.getName());
			for (Item pizza : player.getAllEquipped("pizza")) {
				String flavor = pizza.getInfoString();
				if (data.flavor.equals(flavor)) {
					player.drop(pizza);
					// Check whether the player was supposed to deliver the
					// pizza, or if he just picked up a pizza from the ground.
					// NOTE: This is no perfect protection (two players can
					// still swap their pizzas), but the abuse potential is
					// quite low. TODO: For full security, we'd have to rewrite
					// this so that the pizza flavor and baking time are stored
					// inside the pizza item's infostring, and the quest slot
					// containss the item ID of the pizza.
					if (player.hasQuest(QUEST_SLOT)) {
						if (isDeliveryTooLate(player)) {
							if (data.messageOnColdPizza.contains("%s")) {
								npc.say(String.format(data.messageOnColdPizza,
										data.flavor));
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
							StackableItem money = (StackableItem) StendhalRPWorld
									.get().getRuleManager().getEntityManager()
									.getItem("money");
							money.setQuantity(data.tip);
							player.equip(money, true);
							player.addXP(data.xp);
						}
						player.removeQuest(QUEST_SLOT);
						putOffUniform(player);
					} else {
						// The player delivered a pizza that another player
						// gave him, or that he found on the ground. We cannot
						// allow this because we have no chance to find out
						// if the pizza is still hot (we can't access the quest
						// slot).
						npc.say("Eek! This pizza is all dirty! Did you find it on the ground?");
					}
					return;
				}
			}
			// The player has brought the pizza to the wrong NPC.
			npc.say("No, thanks. I like " + data.flavor + " better.");
		} else {
			npc.say("A pizza? Where?");
		}
	}

	/** Takes away the player's uniform, if the he is wearing it. */
	private void putOffUniform(Player player) {
		if (UNIFORM.isPartOf(player.getOutfit())) {
			player.returnToOriginalOutfit();
		}
	}

	private void prepareBaker() {

		SpeakerNPC leander = npcs.get("Leander");

		leander.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					if (player.hasQuest(QUEST_SLOT)) {
						String[] questData = player.getQuest(QUEST_SLOT)
								.split(";");
						String customerName = questData[0];
						if (isDeliveryTooLate(player)) {
							// If the player still carries the pizza,
							// take it away because the baker is angry,
							// and because the player probably won't
							// deliver it anymore anyway.
							player.dropAll("pizza");
							npc.say("I see you failed to deliver the pizza to "
								+ customerName
								+ " in time. Are you sure you will be more reliable this time?");
						} else {
							npc.say("You still have to deliver a pizza "
									+ customerName + ", and hurry!");
							npc.setCurrentState(ConversationStates.ATTENDING);
						}
					} else {
						npc.say("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?");
					}
				}
			});

		leander.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					startDelivery(player, npc);
				}
			});

		leander.add(
			ConversationStates.QUEST_OFFERED,
			"no",
			null,
			ConversationStates.ATTENDING,
			"Too bad. I hope my daughter #Sally will soon come back from her camp to help me with the deliveries.",
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					putOffUniform(player);
				}
			});

		for (String name : customerDB.keySet()) {
			CustomerData data = customerDB.get(name);
			// TODO: If there's a space in the NPC name, this won't work.
			leander.addReply(name, data.npcDescription);
		}
	}

	private void prepareCustomers() {

		for (String name : customerDB.keySet()) {
			SpeakerNPC npc = npcs.get(name);

			npc.add(ConversationStates.ATTENDING, "pizza", null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						handOverPizza(player, npc);
					}
				});
		}
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		buildCustomerDatabase();
		prepareBaker();
		prepareCustomers();
	}
}
