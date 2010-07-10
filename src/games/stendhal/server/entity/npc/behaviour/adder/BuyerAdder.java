package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ComplainAboutSentenceErrorAction;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.SentenceHasErrorCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;

import org.apache.log4j.Logger;

public class BuyerAdder {
	private static Logger logger = Logger.getLogger(BuyerAdder.class);

	public void add(final SpeakerNPC npc, final BuyerBehaviour behaviour,
			final boolean offer) {
		final Engine engine = npc.getEngine();

		if (offer) {
			engine.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null, 
					new ChatAction() {
						public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
							StringBuilder text = new StringBuilder("i buy ");
							if(behaviour.dealtItems().size()==1) {
								text.append("only this kind of items: ");
							} else {
								text.append("items of these kinds: ");
							}
							text.append(Grammar.enumerateCollection(behaviour.dealtItems()));
							text.append(".");
							npc.say(text.toString());
						}						
					});
		}
		engine.add(ConversationStates.ATTENDING, "sell", new SentenceHasErrorCondition(),
				ConversationStates.ATTENDING, null,
				new ComplainAboutSentenceErrorAction());

		ChatCondition condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			new NotCondition(behaviour.getTransactionCondition()));
		engine.add(ConversationStates.ATTENDING, "sell", condition,
			ConversationStates.ATTENDING, null,
			behaviour.getRejectedTransactionAction());

		condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			behaviour.getTransactionCondition());
		engine.add(ConversationStates.ATTENDING, "sell", condition,
				ConversationStates.SELL_PRICE_OFFERED, null,
				new ChatAction() {

					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (player.isBadBoy()) {
							// don't buy from player killers at all
							raiser.say("Sorry, but I just can't trust you. You look too dangerous to deal with. Please go away.");
							raiser.setCurrentState(ConversationStates.IDLE);
							return;
						}
						if (behaviour.parseRequest(sentence)) {
							if (behaviour.getAmount() > 1000) {
								logger.warn("Refusing to buy very large amount of "
										+ behaviour.getAmount()
										+ " " + behaviour.getChosenItemName()
										+ " from player "
										+ player.getName() + " talking to "
										+ raiser.getName() + " saying "
										+ sentence);
								raiser.say("Sorry, the maximum number of " 
										+ behaviour.getChosenItemName() 
										+ " which I can buy at once is 1000.");
								raiser.setCurrentState(ConversationStates.ATTENDING);
							} else if (behaviour.getAmount() > 0) {
								final String itemName=behaviour.getChosenItemName();
								// will check if player have claimed amount of items
								if(itemName.equals("sheep")) {
									// player have no sheep...
									if (!player.hasSheep()) {
										raiser.say("You don't have any sheep, " + player.getTitle() + "! What are you trying to pull?");
										return;
									}
								} else {
									
								}
								final int price = behaviour.getCharge(player);

								if (price != 0) {
	    							raiser.say(Grammar.makeUpperCaseWord(Grammar.quantityplnoun(behaviour.getAmount(), behaviour.getChosenItemName()))
	    									+ " " + Grammar.isare(behaviour.getAmount()) + " worth "
	    									+ price + ". Do you want to sell "
	    									+ Grammar.itthem(behaviour.getAmount()) + "?");
								} else {
									raiser.say("Sorry, " 
											+ Grammar.thatthose(behaviour.getAmount()) + " " 
											+ Grammar.plnoun(behaviour.getAmount(), behaviour.getChosenItemName())
	    									+ " " + Grammar.isare(behaviour.getAmount()) + " worth nothing.");
									raiser.setCurrentState(ConversationStates.ATTENDING);
								}
							} else {
								raiser.say("Sorry, how many " + Grammar.plural(behaviour.getChosenItemName()) + " do you want to sell?!");

    							raiser.setCurrentState(ConversationStates.ATTENDING);
							}
						} else {
							if (behaviour.getChosenItemName() == null) {
								raiser.say("Please tell me what you want to sell.");
							} else {
								raiser.say("Sorry, I don't buy any "
										+ Grammar.plural(behaviour.getChosenItemName()) + ".");
							}

							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		engine.add(ConversationStates.SELL_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						logger.debug("Buying something from player " + player.getName());

						boolean success = behaviour.transactAgreedDeal(raiser, player);
						if (success) {
							raiser.addEvent(new SoundEvent("coins-1", SoundLayer.CREATURE_NOISE));
						}
					}
				});

		engine.add(ConversationStates.SELL_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Ok, then how else may I help you?", null);
	}

}
