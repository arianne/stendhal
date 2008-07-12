package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

public class SellerAdder {
	private static Logger logger = Logger.getLogger(SellerAdder.class);

	public void addSeller(final SpeakerNPC npc, final SellerBehaviour behaviour) {
		addSeller(npc, behaviour, true);
	}

	public void addSeller(final SpeakerNPC npc, final SellerBehaviour behaviour,
			final boolean offer) {
		final Engine engine = npc.getEngine();

		if (offer) {
			engine.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I sell "
							+ Grammar.enumerateCollection(behaviour.dealtItems())
							+ ".", null);
		}

		engine.add(ConversationStates.ATTENDING, "buy", null,
				ConversationStates.BUY_PRICE_OFFERED, null,
				new SpeakerNPC.ChatAction() {

					@Override
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC engine) {
						if (sentence.hasError()) {
							engine.say("Sorry, I did not understand you. "
									+ sentence.getErrorString());
							engine.setCurrentState(ConversationStates.ATTENDING);
						} else {
							// find out what the player wants to buy, and how much of it
							if (behaviour.parseRequest(sentence)) {
    							// find out if the NPC sells this item, and if so,
    							// how much it costs.
    							if (behaviour.getAmount() > 1000) {
    								logger.warn("Decreasing very large amount of "
    										+ behaviour.getAmount()
    										+ " " + behaviour.getChosenItemName()
    										+ " to 1 for player "
    										+ player.getName() + " talking to "
    										+ engine.getName() + " saying "
    										+ sentence);
    								behaviour.setAmount(1);
    							}

    							if (behaviour.getAmount() > 0) {
	    							final int price = behaviour.getUnitPrice(behaviour.getChosenItemName())
	    									* behaviour.getAmount();

	    							engine.say(Grammar.quantityplnoun(behaviour.getAmount(), behaviour.getChosenItemName())
	    									+ " will cost " + price
	    									+ ". Do you want to buy "
	    									+ Grammar.itthem(behaviour.getAmount()) + "?");
    							} else {
    								engine.say("Sorry, how many " + Grammar.plural(behaviour.getChosenItemName()) + " do you want to buy?!");

        							engine.setCurrentState(ConversationStates.ATTENDING);
    							}
    						} else {
    							if (behaviour.getChosenItemName() == null) {
    								engine.say("Please tell me what you want to buy.");
    							} else {
    								engine.say("Sorry, I don't sell "
    										+ Grammar.plural(behaviour.getChosenItemName()) + ".");
    							}

    							engine.setCurrentState(ConversationStates.ATTENDING);
    						}
						}
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, "Thanks.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC engine) {
						final String itemName = behaviour.getChosenItemName();
						logger.debug("Selling a " + itemName + " to player "
								+ player.getName());

						behaviour.transactAgreedDeal(engine, player);
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Ok, how else may I help you?",
				null);
	}

}
