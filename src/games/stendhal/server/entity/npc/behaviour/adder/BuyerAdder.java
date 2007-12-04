package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationParser;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

public class BuyerAdder {
	private static Logger logger = Logger.getLogger(BuyerAdder.class);

	public void add(SpeakerNPC npc, final BuyerBehaviour behaviour, boolean offer) {
		Engine engine = npc.getEngine();

		if (offer) {
			engine.add(ConversationStates.ATTENDING, ConversationPhrases.OFFER_MESSAGES, null, ConversationStates.ATTENDING, "I buy "
			        + Grammar.enumerateCollection(behaviour.dealtItems()) + ".", null);
		}

		engine.add(ConversationStates.ATTENDING, "sell", null, ConversationStates.SELL_PRICE_OFFERED, null,
		        new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {

			        	Sentence sentence = ConversationParser.parse(text);

				        int amount = sentence.getAmount();
				        String item = sentence.getItemName();

				        if (sentence.getError()) {
					        engine.say("Sorry, I did not understand you.");
					        engine.setCurrentState(ConversationStates.ATTENDING);
					        return;
				        }

				        if (behaviour.hasItem(item)) {
					        behaviour.chosenItem = item;
							if (amount > 1000) {
								logger.warn("Decreasing very large amount of " + amount + " to 1 for player " + player.getName() + " talking to " + engine.getName() + " saying " + text);
								amount = 1;
							}
					        behaviour.setAmount(amount);
					        int price = behaviour.getCharge(player);

					        engine.say(Grammar.quantityplnoun(amount, item) + " " + Grammar.isare(amount) + " worth "
					                + price + ". Do you want to sell " + Grammar.itthem(amount) + "?");
				        } else {
					        if (item == null) {
						        engine.say("Please tell me what you want to sell.");
					        } else {
						        engine.say("Sorry, I don't buy any " + Grammar.plural(item));
					        }
					        engine.setCurrentState(ConversationStates.ATTENDING);
				        }
			        }
		        });

		engine.add(ConversationStates.SELL_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, "Thanks.",
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        logger.debug("Buying something from player " + player.getName());

				        behaviour.transactAgreedDeal(engine, player);
			        }
		        });

		engine.add(ConversationStates.SELL_PRICE_OFFERED, ConversationPhrases.NO_MESSAGES, null,
		        ConversationStates.ATTENDING, "Ok, then how else may I help you?", null);
	}

}
