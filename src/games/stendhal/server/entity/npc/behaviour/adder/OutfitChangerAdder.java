package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import org.apache.log4j.Logger;

public class OutfitChangerAdder {
	private static Logger logger = Logger.getLogger(OutfitChangerAdder.class);

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 * 
	 * @param npc
	 *            SpeakerNPC
	 * @param behaviour
	 *            The behaviour (which includes a pricelist).
	 * @param command
	 *            The action needed to get the outfit, e.g. "buy", "lend".
	 */
	public void addOutfitChanger(final SpeakerNPC npc,
			final OutfitChangerBehaviour behaviour, final String command) {
		addOutfitChanger(npc, behaviour, command, true, true);
	}

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 * 
	 * @param npc
	 *            SpeakerNPC
	 * @param behaviour
	 *            The behaviour (which includes a pricelist).
	 * @param command
	 *            The action needed to get the outfit, e.g. "buy", "lend".
	 * @param offer
	 *            Defines if the NPC should react to the word "offer".
	 * @param canReturn
	 *            If true, a player can say "return" to get his original outfit
	 *            back.
	 */
	public void addOutfitChanger(final SpeakerNPC npc,
			final OutfitChangerBehaviour behaviour, final String command,
			final boolean offer, final boolean canReturn) {

		final Engine engine = npc.getEngine();
		if (offer) {
			engine.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"You can #"
							+ command
							+ " "
							+ Grammar.enumerateCollection(behaviour.dealtItems())
							+ ".", null);
		}

		engine.add(ConversationStates.ATTENDING, command, null,
				ConversationStates.BUY_PRICE_OFFERED, null,
				new ChatAction() {

					public void fire(final Player player, final Sentence sentence,
							final EventRaiser raiser) {
						if (sentence.hasError()) {
							raiser.say("Sorry, I did not understand you. "
									+ sentence.getErrorString());
						}

						// find out what the player wants to wear
						boolean found = behaviour.parseRequest(sentence);

						// find out if the NPC sells this item, and if so,
						// how much it costs.
						if (!found && (behaviour.dealtItems().size() == 1)) {
                			// The NPC only offers one type of outfit, so
                			// it's clear what the player wants.
							behaviour.setChosenItemName(behaviour.dealtItems().iterator().next());
							found = true;
						}

						if (found) {
							// We ignore any amounts.
							behaviour.setAmount(1);

							final int price = behaviour.getUnitPrice(behaviour.getChosenItemName())
									* behaviour.getAmount();

							raiser.say("To " + command + " a "  + behaviour.getChosenItemName() + " will cost " + price
									+ ". Do you want to " + command + " it?");
						} else {
							if (behaviour.getChosenItemName() == null) {
								raiser.say("Please tell me what you want to "
										+ command + ".");
							} else {
								raiser.say("Sorry, I don't offer "
										+ Grammar.plural(behaviour.getChosenItemName()) + ".");
							}
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						final String itemName = behaviour.getChosenItemName();
						logger.debug("Selling a " + itemName + " to player "
								+ player.getName());

						if (behaviour.transactAgreedDeal(npc, player)) {
							if (canReturn) {
								npc.say("Thanks, and please don't forget to #return it when you don't need it anymore!");
								// -1 is also the public static final int NEVER_WEARS_OFF = -1; 
								// but it doesn't recognise it here ...
							} else if (behaviour.endurance != -1) {
								npc.say("Thanks! This will wear off in " +  TimeUtil.timeUntil((int) (behaviour.endurance * 0.3)) + ".");
							} else {
								npc.say("Thanks!");
							}
						}
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Ok, how else may I help you?",
				null);

		if (canReturn) {
			engine.add(ConversationStates.ATTENDING, "return", null,
					ConversationStates.ATTENDING, null,
					new ChatAction() {
						public void fire(final Player player, final Sentence sentence,
								final EventRaiser npc) {
							if (behaviour.returnToOriginalOutfit(player)) {
								npc.say("Thank you!");
							} else {
								npc.say("I can't remember that I gave you anything.");
							}
						}
					});
		}
	}

}
