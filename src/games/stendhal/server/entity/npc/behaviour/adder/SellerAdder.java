package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ComplainAboutSentenceErrorAction;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.SentenceHasErrorCondition;
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

		engine.add(ConversationStates.ATTENDING, "buy", new SentenceHasErrorCondition(),
				ConversationStates.ATTENDING, null,
				new ComplainAboutSentenceErrorAction());

		ChatCondition condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			new NotCondition(behaviour.getTransactionCondition()));
		engine.add(ConversationStates.ATTENDING, "buy", condition,
			ConversationStates.ATTENDING, null,
			behaviour.getRejectedTransactionAction());

		condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			behaviour.getTransactionCondition());
		engine.add(ConversationStates.ATTENDING, "buy", condition,
				ConversationStates.BUY_PRICE_OFFERED, null,
				new ChatAction() {

					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC engine) {
						// find out what the player wants to buy, and how much of it
						if (behaviour.parseRequest(sentence)) {
							// find out if the NPC sells this item, and if so,
							// how much it costs.
							if (behaviour.getAmount() > 1000) {
								logger.warn("Refusing to sell very large amount of "
										+ behaviour.getAmount()
										+ " " + behaviour.getChosenItemName()
										+ " to player "
										+ player.getName() + " talking to "
										+ engine.getName() + " saying "
										+ sentence);
								engine.say("Sorry, the maximum number of " 
										+ behaviour.getChosenItemName() 
										+ " which I can sell at once is 1000.");
								engine.setCurrentState(ConversationStates.ATTENDING);
							} else if (behaviour.getAmount() > 0) {
    							int price = behaviour.getUnitPrice(behaviour.getChosenItemName())
    									* behaviour.getAmount();
    							String message = "";
    							if (player.isBadBoy()) {
    								price = (int) (SellerBehaviour.BAD_BOY_BUYING_PENALTY * price);
    								
    								message = "To friends I charge less, but you seem like you have played unfairly here. So,  ";
    							}
    							engine.say(message + Grammar.quantityplnoun(behaviour.getAmount(), behaviour.getChosenItemName())
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
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						final String itemName = behaviour.getChosenItemName();
						logger.debug("Selling a " + itemName + " to player " + player.getName());
						behaviour.transactAgreedDeal(engine, player);
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Ok, how else may I help you?",
				null);
	}

}
