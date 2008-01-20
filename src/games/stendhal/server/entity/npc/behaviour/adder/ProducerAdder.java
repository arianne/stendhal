package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

public class ProducerAdder {
	private static Logger logger = Logger.getLogger(ProducerAdder.class);

	public void addProducer(SpeakerNPC npc, final ProducerBehaviour behaviour,
			String welcomeMessage) {
		Engine engine = npc.getEngine();

		final String thisWelcomeMessage = welcomeMessage;
		npc.addWaitMessage(null, new SpeakerNPC.ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
				engine.say("Please wait! I am attending "
						+ engine.getAttending().getName() + ".");
			}
		});

		engine.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						return !player.hasQuest(behaviour.getQuestSlot())
								|| player.isQuestCompleted(behaviour.getQuestSlot());
					}
				}, ConversationStates.ATTENDING, thisWelcomeMessage, null);

		engine.add(
				ConversationStates.ATTENDING,
				behaviour.getProductionActivity(),
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						return !player.hasQuest(behaviour.getQuestSlot())
								|| player.isQuestCompleted(behaviour.getQuestSlot());
					}
				}, ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						if (sentence.hasError()) {
							npc.say("Sorry, I did not understand you. "
									+ sentence.getErrorString());
						} else {
							boolean found = behaviour.parseRequest(sentence);

    						// Find out how much items we shall produce.
    						if (!found && behaviour.getChosenItemName() == null) {
    							behaviour.setChosenItemName(behaviour.getProductName());
    							found = true;
    						}

    						if (found) {
    							if (behaviour.getAmount() > 1000) {
    								logger.warn("Decreasing very large amount of "
    										+ behaviour.getAmount()
    										+ " " + behaviour.getChosenItemName()
    										+ " to 1 for player "
    										+ player.getName() + " talking to "
    										+ npc.getName() + " saying " + sentence);
    								behaviour.setAmount(1);
    							}

    							if (behaviour.askForResources(npc, player, behaviour.getAmount())) {
    								npc.setCurrentState(ConversationStates.PRODUCTION_OFFERED);
    							}
    						}
						}
					}
				});

		engine.add(ConversationStates.PRODUCTION_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						behaviour.transactAgreedDeal(npc, player);
					}
				});

		engine.add(ConversationStates.PRODUCTION_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "OK, no problem.", null);

		engine.add(
				ConversationStates.ATTENDING,
				behaviour.getProductionActivity(),
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						return player.hasQuest(behaviour.getQuestSlot())
								&& !player.isQuestCompleted(behaviour.getQuestSlot());
					}
				}, ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						npc.say("I still haven't finished your last order. Come back in "
								+ behaviour.getApproximateRemainingTime(player)
								+ "!");
					}
				});

		engine.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						return player.hasQuest(behaviour.getQuestSlot())
								&& !player.isQuestCompleted(behaviour.getQuestSlot());
					}
				}, ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						behaviour.giveProduct(npc, player);
					}
				});
	}

}
