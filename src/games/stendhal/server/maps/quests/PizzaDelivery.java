package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Pizza Delivery
 * 
 * PARTICIPANTS:
 * - Leander (the baker in Semos)
 * - NPC's all over the world (as customers)
 *
 * STEPS:
 * - Leander gives you a pizza and tells you who ordered it, and how
 *   much time you have to deliver.
 * - As a gimmick, you get a pizza delivery uniform. 
 * - You walk to the customer and say "pizza".
 * - The customer takes the pizza. If you were fast enough, you get a
 *   tip.
 * - You put on your original clothes automatically. 
 *
 * REWARD:
 * - XP (Amount varies depending on customer. You only get half of the XP
 *   if the pizza has become cold.) 
 * - gold coins (As a tip, if you were fast enough; amount varies depending on
 *   customer.)
 *
 * REPETITIONS:
 * - As many as wanted, but you can't get a new task while you still have the
 *   chance to do the current delivery on time.
 */
public class PizzaDelivery extends AbstractQuest {
	
	private static class CustomerData {
		/** A hint where to find the customer */
		private String npcDescription;
		/** The pizza style the customer likes */
		private String flavor;
		/** The time until the pizza should be delivered */
		private int expectedMinutes;
		/** The money the player should get on fast delivery */
		private int tip;
		/**
		 * The experience the player should gain for delivery.
		 * When the pizza has already become cold, the player will
		 * gain half of this amount.
		 */
		private int xp;
		
		private CustomerData(String npcDescription, String flavor, int expectedTime, int tip, int xp) {
			this.npcDescription = npcDescription;
			this.flavor = flavor;
			this.expectedMinutes = expectedTime;
			this.tip = tip;
			this.xp = xp;
		}
	}
	private static final String QUEST_SLOT = "pizza_delivery";
	
	private static Map<String, CustomerData> customerDB;
	
	/**
	 * Responsible for putting on the pizza delivery uniform, and for taking it off
	 * again after delivery.
	 */
	private OutfitChangerBehaviour outfitChangerBehaviour; 

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
	
	// TODO: balance time, tips, xp
	// You can't enter NPC's with spaces in their names, see TODOs below.
	private void buildCustomerDatabase() {
		customerDB = new HashMap<String, CustomerData>();
		
		customerDB.put("Balduin", new CustomerData(
				"Balduin is a hermit who's living on a mountain between Semos and Ados.",
				"Pizza Prosciutto",
				15,   // minutes to deliver
				200,   // tip when delivered on time
				30)); // experience gain for delivery
		
		customerDB.put("Eliza", new CustomerData(
				"Eliza works for the Athor Island ferry service. You'll find her at the docks south of the Ados swamps.",
				"Pizza del Mare",
				15,   // minutes to deliver
				200,  // tip when delivered on time
				30)); // experience gain for delivery
		
		customerDB.put("Fidorea", new CustomerData(
				"Fidorea lives in Ados city. She is a makeup artist.",
				"Pizza Margherita",
				10,   // minutes to deliver
				150,  // tip when delivered on time
				20)); // experience gain for delivery
		
		customerDB.put("Jenny", new CustomerData(
				"Jenny owns a mill near Semos.",
				"Pizza Bolognese",
				2,    // minutes to deliver
				20,   // tip when delivered on time
				10)); // experience gain for delivery
		
		customerDB.put("Jynath", new CustomerData(
				"Jynath is a witch who lives in a small house south of Or'ril castle.",
				"Pizza Funghi",
				7,   // minutes to deliver. You can run through in ca. 5 min,
					 // but you might have to kill some creatures that get in
					 // your way.
				120,  // tip when delivered on time
				20)); // experience gain for delivery
		
		customerDB.put("Marcus", new CustomerData(
				"Marcus is a guard in the Semos jail.",
				"Pizza Tonno",
				5,    // minutes to deliver
				20,   // tip when delivered on time
				10)); // experience gain for delivery
		
		customerDB.put("Ouchit", new CustomerData(
				"Ouchit is a weapons trader. He has currently rented a room in the tavern, just around the corner.",
				"Pizza Quattro Stagioni",
				1,    // minutes to deliver
				10,   // tip when delivered on time
				5));  // experience gain for delivery
		
		customerDB.put("Tor'Koom", new CustomerData(
				"Tor'Koom is an orc who lives in the dungeon below this town. Sheep are his favourite food.",
				"Pizza Pecora", // "Pizza sheep" in Italian ;)
				15,   // minutes to deliver
				100,  // tip when delivered on time
				30)); // experience gain for delivery 
	}
	
	private void startDelivery(Player player, SpeakerNPC npc) {
		String name = Rand.rand(customerDB.keySet());
		CustomerData data = customerDB.get(name);
		
		Item pizza = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("pizza");
		pizza.put("infostring", data.flavor);
		pizza.setDescription("You see a " + data.flavor + ".");
		player.equip(pizza, true);

		// TODO: If there's a space in the NPC name, colorization won't work.
		// TODO: correct singular/plural for minutes
		npc.say("You must bring this " + data.flavor + " to #" + name + " within " + data.expectedMinutes + " minutes. Say \"pizza\" so that " + name + " knows that I sent you. Oh, and please wear this uniform on your way.");
		outfitChangerBehaviour.putOnOutfit(player, "pizza_delivery_uniform");
		player.setQuest(QUEST_SLOT, name + ";" + System.currentTimeMillis());
	}
	
	/**
	 * Checks whether the player has failed to fulfil his current delivery
	 * job in time. 
	 * @param player The player.
	 * @return true if the player is too late. false if the player still has
	 * 		   time, or if he doesn't have a delivery to do currently.
	 */
	private boolean isDeliveryTooLate(Player player) {
		if (player.hasQuest(QUEST_SLOT)) {
			String[] questData = player.getQuest(QUEST_SLOT).split(";");
			String customerName = questData[0];
			CustomerData customerData = customerDB.get(customerName);
			long bakeTime = Long.parseLong(questData[1]);
			long expectedTimeOfDelivery = bakeTime + 60 * 1000 * customerData.expectedMinutes;
			if (System.currentTimeMillis() > expectedTimeOfDelivery) {
				return true;
			}
		}
		return false;

		
	}
	
	private void handOverPizza(Player player, SpeakerNPC npc) {
		if (player.isEquipped("pizza")) {
			CustomerData data = customerDB.get(npc.getName());
			for (Item pizza: player.getAllEquipped("pizza")) {
				String flavor = pizza.get("infostring");
				if (data.flavor.equals(flavor)) {
					player.drop(pizza);
					if (isDeliveryTooLate(player)) {
						npc.say("Too bad. It has become cold. Thank you anyway.");
						player.addXP(data.xp / 2);
					} else {
						int tip = data.tip;
						npc.say("Thank you! That was fast. Here, take " + tip + " pieces of gold as a tip!");
						StackableItem money = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("money");
						money.setQuantity(tip);
						player.equip(money, true);
						player.addXP(data.xp);
					}
					player.removeQuest(QUEST_SLOT);
					if (outfitChangerBehaviour.wearsOutfitFromHere(player)) {
						outfitChangerBehaviour.returnToOriginalOutfit(player);
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
	
	private void prepareBaker() {
		
		SpeakerNPC leander = npcs.get("Leander");

		leander.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (player.hasQuest(QUEST_SLOT)) {
							String[] questData = player.getQuest(QUEST_SLOT).split(";");
							String customerName = questData[0];
							if (isDeliveryTooLate(player)) {
								// If the player still carries the pizza,
								// take it away because the baker is angry,
								// and because the player probably won't
								// deliver it anymore anyway.
								player.dropAll("pizza");
								npc.say("I see you failed to deliver the pizza to " + customerName +" in time. Are you sure you will be more reliable this time?");
							} else {
								npc.say("You still have to deliver a pizza " + customerName +", and hurry!");
								npc.setCurrentState(ConversationStates.ATTENDING);
							}
						} else {
							npc.say("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?");
						}
					}
				});

		leander.add(ConversationStates.QUEST_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						startDelivery(player, npc);
					}
				});

		leander.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Too bad. I hope my daughter #Sally will soon come back from her camp to help me with the deliveries.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						// Take away the uniform, if the player is wearing one.
						if (outfitChangerBehaviour.wearsOutfitFromHere(player)) {
							outfitChangerBehaviour.returnToOriginalOutfit(player);
						}
					}
				});

		for (String name: customerDB.keySet()) {
			CustomerData data = customerDB.get(name);
			// TODO: If there's a space in the NPC name, this won't work. 
			leander.addReply(name, data.npcDescription);
		}
		
		Map<String, Integer> priceList = new HashMap<String, Integer>();
		priceList.put("pizza_delivery_uniform", 10);
		outfitChangerBehaviour = new OutfitChangerBehaviour(
				priceList);
		// You can get the pizza delivery uniform for 10 gold by saying
		// "lend pizza_delivery_uniform", but Leander doesn't advertise this.
		// The usual way to get the pizzaboy suit is to do a
		// delivery.
		leander.addOutfitChanger(outfitChangerBehaviour, "lend", false, true);
	}

	private void prepareCustomers() {
		
		for (String name: customerDB.keySet()) {
			SpeakerNPC npc = npcs.get(name);

			npc.add(ConversationStates.ATTENDING,
					"pizza",
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, String text, SpeakerNPC engine) {
							return player.hasQuest(QUEST_SLOT);
						}
					},
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text, SpeakerNPC npc) {
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
