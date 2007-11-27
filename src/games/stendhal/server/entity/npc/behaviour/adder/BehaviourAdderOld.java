// $Id$
package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

/**
 * Internal helper class for SpeakerNPC
 */
// TODO: This is just a first step to split it out of SpeakerNPC. More refactoring is needed.
public class BehaviourAdderOld {

	static final Logger logger = Logger.getLogger(BehaviourAdderOld.class);

	private Engine engine;

	@Deprecated
	private SpeakerNPC speakerNPC;

	/**
	 * creates a BehaviourAdder
	 *
	 * @param speakerNPC the speakerNPC the behaviour should be added to.
	 * @param engine the FSM
	 */
	public BehaviourAdderOld(SpeakerNPC speakerNPC, Engine engine) {
		this.speakerNPC = speakerNPC;
		this.engine = engine;
	}

	public void addSeller(final SellerBehaviour behaviour, boolean offer) {

		if (offer) {
			engine.add(ConversationStates.ATTENDING, ConversationPhrases.OFFER_MESSAGES, null, ConversationStates.ATTENDING, "I sell "
			        + Grammar.enumerateCollection(behaviour.dealtItems()) + ".", null);
		}

		engine.add(ConversationStates.ATTENDING, "buy", null, ConversationStates.BUY_PRICE_OFFERED, null,
		        new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        // find out what the player wants to buy, and how
				        // much of it
				        String[] words = text.split(" +");

				        int amount = 1;
				        String item = null;
				        if (words.length > 2) {
					        try {
						        amount = Integer.parseInt(words[1]);
					        } catch (NumberFormatException e) {
						        engine.say("Sorry, I did not understand you.");
						        engine.setCurrentState(ConversationStates.ATTENDING);
						        return;
					        }
					        item = words[2].toLowerCase();
				        } else if (words.length > 1) {
					        item = words[1].toLowerCase();
				        }

				        // find out if the NPC sells this item, and if so,
				        // how much it costs.
				        if (behaviour.hasItem(item)) {
					        behaviour.chosenItem = item;
							if (amount > 1000) {
								logger.warn("Decreasing very large amount of " + amount + " to 1 for player " + player.getName() + " talking to " + engine.getName() + " saying " + text);
								amount = 1;
							}
					        behaviour.setAmount(amount);

					        int price = behaviour.getUnitPrice(item) * behaviour.getAmount();

					        engine.say(Grammar.quantityplnoun(amount, item) + " will cost " + price
					                + ". Do you want to buy " + Grammar.itthem(amount) + "?");
				        } else {
					        if (item == null) {
						        engine.say("Please tell me what you want to buy.");
					        } else {
						        engine.say("Sorry, I don't sell " + Grammar.plural(item));
					        }
					        engine.setCurrentState(ConversationStates.ATTENDING);
				        }
			        }
		        });

		engine.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, "Thanks.",
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        String itemName = behaviour.chosenItem;
				        logger.debug("Selling a " + itemName + " to player " + player.getName());

				        behaviour.transactAgreedDeal(engine, player);
			        }
		        });

		engine.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.NO_MESSAGES, null,
		        ConversationStates.ATTENDING, "Ok, how else may I help you?", null);
	}

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 * @param behaviour The behaviour (which includes a pricelist).
	 * @param command The action needed to get the outfit, e.g. "buy", "lend".
	 */
	public void addOutfitChanger(OutfitChangerBehaviour behaviour, String command) {
		addOutfitChanger(behaviour, command, true, true);
	}

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 * @param behaviour The behaviour (which includes a pricelist).
	 * @param command The action needed to get the outfit, e.g. "buy", "lend".
	 * @param offer Defines if the NPC should react to the word "offer".
	 * @param canReturn If true, a player can say "return" to get his original
	 *                  outfit back.
	 */
	public void addOutfitChanger(final OutfitChangerBehaviour behaviour, final String command, boolean offer,
	        final boolean canReturn) {

		if (offer) {
			engine.add(ConversationStates.ATTENDING, ConversationPhrases.OFFER_MESSAGES, null, ConversationStates.ATTENDING, "You can #" + command
			        + " " + Grammar.enumerateCollection(behaviour.dealtItems()) + ".", null);
		}

		engine.add(ConversationStates.ATTENDING, command, null, ConversationStates.BUY_PRICE_OFFERED, null,
		        new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        // find out what the player wants to buy, and how
				        // much of it
				        String[] words = text.split(" +");

				        String item = null;
				        // we ignore any amounts
				        if (words.length > 1) {
					        item = words[words.length - 1].toLowerCase();
				        } else if (behaviour.dealtItems().size() == 1) {
				        	// The NPC only offers one type of outfit, so
				        	// it's clear what the player wants.
				        	item = behaviour.dealtItems().iterator().next();
				        }

				        // find out if the NPC sells this item, and if so,
				        // how much it costs.
				        if (behaviour.hasItem(item)) {
					        behaviour.chosenItem = item;
					        behaviour.setAmount(1);

					        int price = behaviour.getUnitPrice(item) * behaviour.getAmount();

					        engine.say("A " + item + " will cost " + price + ". Do you want to " + command + " it?");
				        } else {
					        if (item == null) {
						        engine.say("Please tell me what you want to " + command + ".");
					        } else {
						        engine.say("Sorry, I don't sell " + Grammar.plural(item));
					        }
					        engine.setCurrentState(ConversationStates.ATTENDING);
				        }
			        }
		        });

		engine.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        String itemName = behaviour.chosenItem;
				        logger.debug("Selling a " + itemName + " to player " + player.getName());

				        if (behaviour.transactAgreedDeal(npc, player)) {
					        if (canReturn) {
						        npc.say("Thanks, and please don't forget to #return it when you don't need it anymore!");
					        } else {
						        npc.say("Thanks!");
					        }
				        }
			        }
		        }
			);

		engine.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.NO_MESSAGES, null,
		        ConversationStates.ATTENDING, "Ok, how else may I help you?", null);

		if (canReturn) {
			engine.add(ConversationStates.ATTENDING, "return", null, ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (behaviour.returnToOriginalOutfit(player)) {
							// TODO: it would be cool if you could get a refund
							// for returning the outfit, i. e. the money is
							// only paid as a deposit.
							npc.say("Thank you!");
						} else {
							npc.say("I can't remember that I gave you anything.");
						}
					}
				}
			);
		}
	}

	public void addProducer(final ProducerBehaviour behaviour, String welcomeMessage) {
		final String thisWelcomeMessage = welcomeMessage;

		speakerNPC.addWaitMessage(null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					engine.say("Please wait! I am attending " + engine.getAttending().getName() + ".");
				}
			}
		);

		engine.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !player.hasQuest(behaviour.getQuestSlot()) || player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, thisWelcomeMessage, null);

		engine.add(ConversationStates.ATTENDING, behaviour.getProductionActivity(),
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !player.hasQuest(behaviour.getQuestSlot()) || player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC npc) {

					String[] words = text.split(" +");
					int amount = 1;
					if (words.length > 1) {
						amount = Integer.parseInt(words[1]);
					}
					if (amount > 1000) {
						logger.warn("Decreasing very large amount of " + amount + " to 1 for player " + player.getName() + " talking to " + npc.getName() + " saying " + text);
						amount = 1;
					}
					if (behaviour.askForResources(npc, player, amount)) {
						npc.setCurrentState(ConversationStates.PRODUCTION_OFFERED);
					}
				}
			}
		);

		engine.add(ConversationStates.PRODUCTION_OFFERED, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        behaviour.transactAgreedDeal(npc, player);
			        }
		        }
			);

		engine.add(ConversationStates.PRODUCTION_OFFERED, ConversationPhrases.NO_MESSAGES, null,
		        ConversationStates.ATTENDING, "OK, no problem.", null);

		engine.add(ConversationStates.ATTENDING, behaviour.getProductionActivity(),
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return player.hasQuest(behaviour.getQuestSlot()) && !player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC npc) {
					npc.say("I still haven't finished your last order. Come back in "
					        + behaviour.getApproximateRemainingTime(player) + "!");
				}
			}
		);

		engine.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return player.hasQuest(behaviour.getQuestSlot()) && !player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC npc) {
					behaviour.giveProduct(npc, player);
				}
			}
		);
	}
}
