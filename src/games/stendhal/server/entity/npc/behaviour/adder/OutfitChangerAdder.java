package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

public class OutfitChangerAdder {
	static Logger logger = Logger.getLogger(OutfitChangerAdder.class);

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 *
	 * @param npc SpeakerNPC
	 * @param behaviour The behaviour (which includes a pricelist).
	 * @param command The action needed to get the outfit, e.g. "buy", "lend".
	 */
	public void addOutfitChanger(SpeakerNPC npc, OutfitChangerBehaviour behaviour, String command) {
		addOutfitChanger(npc, behaviour, command, true, true);
	}

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 *
	 * @param npc SpeakerNPC
	 * @param behaviour The behaviour (which includes a pricelist).
	 * @param command The action needed to get the outfit, e.g. "buy", "lend".
	 * @param offer Defines if the NPC should react to the word "offer".
	 * @param canReturn If true, a player can say "return" to get his original
	 *                  outfit back.
	 */
	public void addOutfitChanger(final SpeakerNPC npc, final OutfitChangerBehaviour behaviour, final String command, boolean offer, final boolean canReturn) {

		Engine engine = npc.getEngine();
		if (offer) {
			engine.add(ConversationStates.ATTENDING, ConversationPhrases.OFFER_MESSAGES, null, ConversationStates.ATTENDING, "You can #" + command
			        + " " + Grammar.enumerateCollection(behaviour.dealtItems()) + ".", null);
		}

		engine.add(ConversationStates.ATTENDING, command, null, ConversationStates.BUY_PRICE_OFFERED, null,
		        new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
				         // find out what the player wants to wear
			        	String item = sentence.getItemName();
			        	/* We ignore any amounts and skip the numeric expressions.
			        	sentence.getAmount(); */

				        if (sentence.hasError()) {
				        	engine.say("Sorry, I did not understand you. " + sentence.getError());
				        } else if (item==null && behaviour.dealtItems().size()==1) {
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
			        public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
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
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
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

}
