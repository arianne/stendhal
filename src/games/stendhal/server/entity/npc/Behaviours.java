package games.stendhal.server.entity.npc;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import games.stendhal.server.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.rule.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import org.apache.log4j.Logger;

public class Behaviours {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Behaviours.class);

	// private static RPServerManager rpman;

	// private static StendhalRPRuleProcessor rules;

	private static StendhalRPWorld world;
	
	public static final String[] GREETING_MESSAGES = {"hi", "hello", "greetings", "hola"};
	
	public static final String[] JOB_MESSAGES = {"job", "work"};

	public static final String[] HELP_MESSAGES = {"help", "ayuda"};

	public static final String[] QUEST_MESSAGES = {"task", "quest"};

	public static final String[] GOODBYE_MESSAGES = {"bye", "farewell", "cya", "adios"};
	
	/**
	 * Helper function to nicely formulate an enumeration of a collection.
	 * For example, for a collection containing the 3 elements x, y, z,
	 * returns the string "x, y, and z".
	 * @param collection The collection whose elements should be enumerated 
	 * @return A nice String representation of the collection
	 */
	public static String enumerateCollection(Collection<String> collection) {
		String[] elements = collection.toArray(new String[collection.size()]);
		if (elements.length == 0) {
			return "";
		} else if (elements.length == 1) {
			return elements[0];
		} else if (elements.length == 2) {
			return elements[0] + " and " + elements[1];
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < elements.length - 1; i++) {
				sb.append(elements[i] + ", ");
			}
			sb.append("and " + elements[elements.length - 1]);
			return sb.toString();
		}
	}
	
	public static void initialize(RPServerManager rpman, StendhalRPRuleProcessor rules, RPWorld world) {
		// Behaviours.rpman = rpman;
		// Behaviours.rules = rules;
		Behaviours.world = (StendhalRPWorld) world;
	}

	public static void addGreeting(SpeakerNPC npc) {
		addGreeting(npc, "Greetings! How may I help you?", null);
	}

	public static void addGreeting(SpeakerNPC npc, String text) {
		addGreeting(npc, text, null);
	}

	public static void addGreeting(SpeakerNPC npc, String text,
			SpeakerNPC.ChatAction action) {
		npc.add(ConversationStates.IDLE,
				GREETING_MESSAGES,
				ConversationStates.ATTENDING,
				text,
				action);

		npc.addWaitMessage(null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("Please wait! I am attending "
								+ engine.getAttending().getName() + ".");
					}
				});
	}

	/**
	 * Makes the given NPC say a text when it hears a certain trigger during
	 * a conversation.
	 * @param npc The NPC that should reply
	 * @param trigger The text that causes the NPC to answer
	 * @param text The answer
	 */
	public static void addReply(SpeakerNPC npc, String trigger, String text) {
		npc.add(ConversationStates.ATTENDING,
				trigger,
				null,
				ConversationStates.ATTENDING,
				text,
				null);
	}

	/**
	 * Makes the given NPC say a text when it hears one of the given triggers
	 * during a conversation.
	 * @param npc The NPC that should reply
	 * @param triggers The texts that cause the NPC to answer
	 * @param text The answer
	 */
	public static void addReply(SpeakerNPC npc, String[] triggers, String text) {
		npc.add(ConversationStates.ATTENDING,
				triggers,
				ConversationStates.ATTENDING,
				text,
				null);
	}

	/**
	 * @param npc
	 * @param triggers
	 * @param text
	 */
	public static void addReply(SpeakerNPC npc, List<String> triggers,
			String text) {
		addReply(npc,
				triggers.toArray(new String[2]),
				text);
	}

	public static void addQuest(SpeakerNPC npc, String text) {
		npc.add(ConversationStates.ATTENDING,
				new String[] { "quest", "task" },
				ConversationStates.ATTENDING,
				text,
				null);
	}

	public static void addQuest(SpeakerNPC npc, String[] texts) {
		npc.add(ConversationStates.ATTENDING,
				new String[] { "quest", "task" },
				ConversationStates.ATTENDING,
				texts,
				null);
	}

	public static void addJob(SpeakerNPC npc, String jobDescription) {
		addReply(npc,
				JOB_MESSAGES,
				jobDescription);
	}

	public static void addHelp(SpeakerNPC npc, String helpDescription) {
		addReply(npc,
				HELP_MESSAGES,
				helpDescription);
	}

	public static void addGoodbye(SpeakerNPC npc) {
		addGoodbye(npc, "Bye.");
	}

	public static void addGoodbye(SpeakerNPC npc, String text) {
		npc.addByeMessage(text, null);
		npc.add(ConversationStates.ANY,
				GOODBYE_MESSAGES,
				ConversationStates.IDLE,
				text,
				null);
	}

	public static class SellerBehaviour {
		protected Map<String, Integer> items;

		protected String chosenItem;

		protected int amount;

		public SellerBehaviour() {
			this.items = new HashMap<String, Integer>();
		}

		public SellerBehaviour(Map<String, Integer> items) {
			this.items = items;
		}

		public Set<String> getItems() {
			return items.keySet();
		}

		public boolean hasItem(String item) {
			return items.containsKey(item);
		}

		public int getUnitPrice(String item) {
			return items.get(item);
		}

		public void setChosenItem(String item) {
			chosenItem = item;
		}

		public String getChosenItem() {
			return chosenItem;
		}

		public void setAmount(String text) {
			try {
				amount = Integer.parseInt(text);
			} catch (Exception e) {
				amount = 1;
			}
		}

		public int getAmount() {
			return amount;
		}

		/**
		 * Returns the price of the desired amount of the chosen item.
		 * @param player The player who considers buying
		 * @return The price; 0 if no item was chosen or if the amount is 0.
		 */
		public int getCharge(Player player) {
			if (chosenItem == null) {
				return 0;
			} else {
				return amount * getUnitPrice(chosenItem);
			}
		}

		/**
		 * Transacts the sale that has been agreed on earlier via
		 * setChosenItem() and setAmount().
		 * @param seller The NPC who sells
		 * @param player The player who buys
		 * @return true iff the transaction was successful, that is when the
		 *              player was able to equip the item(s).
		 */
		public boolean transactAgreedSale(SpeakerNPC seller, Player player) {
			EntityManager manager = world.getRuleManager().getEntityManager();

			Item item = manager.getItem(getChosenItem());
			if (item == null) {
				logger.error("Trying to sell an unexisting item: " + getChosenItem());
				return false;
			}

			// TODO: When the user tries to buy several of a non-stackable
			// item, he is forced to buy only one.
			if (item instanceof StackableItem) {
				((StackableItem) item).setQuantity(amount);
			} else {
				amount = 1;
			}

			item.put("zoneid", player.get("zoneid"));
			IRPZone zone = world.getRPZone(player.getID());
			zone.assignRPObjectID(item);

			if (player.isEquipped("money", getCharge(player))) {
				if (player.equip(item)) {
					player.drop("money", getCharge(player));
					seller.say("Congratulations! Here is your " + getChosenItem() + "!");
					return true;
				} else {
					seller.say("Sorry, but you cannot equip the " + getChosenItem() + ".");
					return false;
				}
			} else {
				seller.say("A real pity! You don't have enough money!");
				return false;
			}
		}
	}

	public static void addSeller(SpeakerNPC npc, SellerBehaviour items) {
		addSeller(npc, items, true);
	}

	public static void addSeller(SpeakerNPC npc, SellerBehaviour items,
			boolean offer) {
		npc.setBehaviourData("seller", items);

		if (offer) {
			npc.add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"I sell " + enumerateCollection(items.getItems()) + ".",
					null);
		}

		npc.add(ConversationStates.ATTENDING,
				"buy",
				null,
				ConversationStates.BUY_PRICE_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						SellerBehaviour sellerBehaviour = (SellerBehaviour) engine
								.getBehaviourData("seller");
		
						// find out what the player wants to buy, and how
						// much of it
						String[] words = text.split(" ");
		
						String amount = "1";
						String item = null;
						if (words.length > 2) {
							amount = words[1].trim();
							item = words[2].trim();
						} else if (words.length > 1) {
							item = words[1].trim();
						}
		
						// find out if the NPC sells this item, and if so,
						// how much it costs.
						if (sellerBehaviour.hasItem(item)) {
							sellerBehaviour.setChosenItem(item);
							sellerBehaviour.setAmount(amount);
		
							int price = sellerBehaviour.getUnitPrice(item)
									* sellerBehaviour.getAmount();
		
							engine.say(amount + " " + item + " costs " + price
									+ ". Do you want to buy?");
						} else {
							engine.say("Sorry, I don't sell " + item);
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.BUY_PRICE_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thanks.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						SellerBehaviour sellerBehaviour = (SellerBehaviour) engine
								.getBehaviourData("seller");
		
						String itemName = sellerBehaviour.getChosenItem();
						logger.debug("Selling a " + itemName + " to player "
								+ player.getName());
		
						sellerBehaviour.transactAgreedSale(engine, player);
					}
				});

		npc.add(ConversationStates.BUY_PRICE_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING, "Ok, how may I help you?",
				null);
	}

	public static class BuyerBehaviour {
		private Map<String, Integer> items;

		private String chosenItem;
		
		private int amount = 0;

		public BuyerBehaviour(Map<String, Integer> items) {
			this.items = items;
		}

		public Set<String> getItems() {
			return items.keySet();
		}

		public boolean hasItem(String item) {
			return items.containsKey(item);
		}

		public int getUnitPrice(String item) {
			return items.get(item);
		}

		public void setChosenItem(String item) {
			chosenItem = item;
		}

		public String getChosenItem() {
			return chosenItem;
		}

		public void setAmount(String text) {
			try {
				amount = Integer.parseInt(text);
			} catch (Exception e) {
				amount = 1;
			}
		}

		public int getAmount() {
			return amount;
		}
		
		public int getCharge(Player player) {
			if (chosenItem == null) {
				return 0;
			} else {
				return amount * getUnitPrice(chosenItem);
			}
		}
		
		// TODO: create RPEntity.equip() with amount parameter.
		public void payPlayer(Player player) {
			boolean found = false;
			Iterator<RPSlot> it = player.slotsIterator();
			// First try to stack the money on existing money
			while (it.hasNext() && !found) {
				RPSlot slot = it.next();
				for (RPObject object: slot) {
					if (object instanceof Money) {
						((Money) object).add(getCharge(player));
						found = true;
						break;
					}
				}
			}
			if (!found) {
				// The player has no money. Put the money into an empty slot.  
				RPSlot slot = player.getSlot("bag");
				Money money = new Money(getCharge(player));
				slot.assignValidID(money);
				slot.add(money);
			}
			world.modify(player);
		}

		public boolean onBuy(SpeakerNPC seller, Player player) {
			if (player.drop(chosenItem, amount)) {
				payPlayer(player);
				seller.say("Thanks! Here is your money.");
				return true;
			} else {
				seller.say("Sorry! You don't have enough " + chosenItem + ".");
				return false;
			}
		}
	}

	public static void addBuyer(SpeakerNPC npc, BuyerBehaviour items) {
		addBuyer(npc, items, true);
	}

	public static void addBuyer(SpeakerNPC npc, BuyerBehaviour items,
			boolean offer) {
		npc.setBehaviourData("buyer", items);

		if (offer) {
			npc.add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"I buy " + enumerateCollection(items.getItems()) + ".",
					null);
		}

		npc.add(ConversationStates.ATTENDING,
				"sell",
				null,
				ConversationStates.SELL_PRICE_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						BuyerBehaviour buyerBehaviour = (BuyerBehaviour) engine
								.getBehaviourData("buyer");
		
						String[] words = text.split(" ");
		
						String amount = "1";
						String item = null;
						if (words.length > 2) {
							amount = words[1].trim();
							item = words[2].trim();
						} else if (words.length > 1) {
							item = words[1].trim();
						}
		
						if (buyerBehaviour.hasItem(item)) {
							buyerBehaviour.setChosenItem(item);
							buyerBehaviour.setAmount(amount);
							int price = buyerBehaviour.getCharge(player);
		
							engine.say(amount + " " + item + " is worth " + price
									+ ". Do you want to sell?");
						} else {
							engine.say("Sorry, I don't buy " + item);
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.SELL_PRICE_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thanks.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						BuyerBehaviour buyerBehaviour = (BuyerBehaviour) engine
								.getBehaviourData("buyer");
		
						logger.debug("Buying something from player "
								+ player.getName());
		
						buyerBehaviour.onBuy(engine, player);
					}
				});

		npc.add(ConversationStates.SELL_PRICE_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Ok, how may I help you?",
				null);
	}

	public static class HealerBehaviour extends SellerBehaviour {
		public HealerBehaviour(int cost) {
			super();
			items.put("heal", cost);
		}

		public void heal(Player player, SpeakerNPC engine) {
			player.setHP(player.getBaseHP());
			player.healPoison();
			world.modify(player);
		}
	}

	public static void addHealer(SpeakerNPC npc, int cost) {
		npc.setBehaviourData("healer", new HealerBehaviour(cost));

		npc.add(ConversationStates.ATTENDING,
				"offer",
				null,
				ConversationStates.ATTENDING,
				"I can #heal you.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				"heal",
				null,
				ConversationStates.HEAL_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						HealerBehaviour healer = (HealerBehaviour) engine
								.getBehaviourData("healer");
						healer.setChosenItem("heal");
						healer.setAmount("1");
						int cost = healer.getCharge(player);
		
						if (cost > 0) {
							engine.say("Healing costs " + cost
									+ ". Do you want to pay?");
						} else {
							engine.say("You are healed. How may I help you?");
							healer.heal(player, engine);
		
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.HEAL_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						HealerBehaviour healerBehaviour = (HealerBehaviour) engine
								.getBehaviourData("healer");
						
						if (player.drop("money", healerBehaviour.getCharge(player))) {
							healerBehaviour.heal(player, engine);
							engine.say("You are healed. How may I help you?");
						} else {
							engine.say("A real pity! You don't have enough money!");
						}
					}
				});

		npc.add(ConversationStates.HEAL_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"OK, how may I help you?",
				null);
		}
}