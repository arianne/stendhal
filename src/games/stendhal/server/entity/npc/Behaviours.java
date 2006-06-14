/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.entity.npc;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;

import java.util.Collection;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.server.game.RPServerManager;
import marauroa.server.game.RPWorld;

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

	public static void addSeller(SpeakerNPC npc, SellerBehaviour behaviour) {
		addSeller(npc, behaviour, true);
	}

	public static void addSeller(SpeakerNPC npc, SellerBehaviour behaviour,
			boolean offer) {
		npc.setBehaviourData("seller", behaviour);

		if (offer) {
			npc.add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"I sell " + enumerateCollection(behaviour.offeredItems()) + ".",
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
							sellerBehaviour.chosenItem = item;
							sellerBehaviour.setAmount(amount);
		
							int price = sellerBehaviour.getUnitPrice(item)
									* sellerBehaviour.amount;
		
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
		
						String itemName = sellerBehaviour.chosenItem;
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

	public static void addBuyer(SpeakerNPC npc, BuyerBehaviour behaviour) {
		addBuyer(npc, behaviour, true);
	}

	public static void addBuyer(SpeakerNPC npc, BuyerBehaviour behaviour,
			boolean offer) {
		npc.setBehaviourData("buyer", behaviour);

		if (offer) {
			npc.add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"I buy " + enumerateCollection(behaviour.acceptedItems()) + ".",
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
							buyerBehaviour.chosenItem = item;
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

	public static void addHealer(SpeakerNPC npc, int cost) {
		npc.setBehaviourData("healer", new HealerBehaviour(world, cost));

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
						healer.chosenItem = "heal";
						healer.amount = 1;
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