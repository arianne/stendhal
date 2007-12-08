package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

public class ProducerAdder {
	static Logger logger = Logger.getLogger(ProducerAdder.class);

	public void addProducer(SpeakerNPC npc, final ProducerBehaviour behaviour, String welcomeMessage) {
		Engine engine = npc.getEngine();

		final String thisWelcomeMessage = welcomeMessage;
		npc.addWaitMessage(null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
					engine.say("Please wait! I am attending " + engine.getAttending().getName() + ".");
				}
			}
		);

		engine.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
					return !player.hasQuest(behaviour.getQuestSlot()) || player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, thisWelcomeMessage, null);

		engine.add(ConversationStates.ATTENDING, behaviour.getProductionActivity(),
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
					return !player.hasQuest(behaviour.getQuestSlot()) || player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
			        int amount = sentence.getAmount();
			        String item = sentence.getItemName();

			        if (sentence.hasError()) {
			        	npc.say("Sorry, I did not understand you. " + sentence.getError());
			        } else {
			        	if (amount > 1000) {
    						logger.warn("Decreasing very large amount of " + amount + (item!=null? item+" ": "") + " to 1 for player " + player.getName() + " talking to " + npc.getName() + " saying " + sentence);
    						amount = 1;
    					}
    					if (behaviour.askForResources(npc, player, amount)) {
    						npc.setCurrentState(ConversationStates.PRODUCTION_OFFERED);
    					}
			        }
				}
			}
		);

		engine.add(ConversationStates.PRODUCTION_OFFERED, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
				        behaviour.transactAgreedDeal(npc, player);
			        }
		        }
			);

		engine.add(ConversationStates.PRODUCTION_OFFERED, ConversationPhrases.NO_MESSAGES, null,
		        ConversationStates.ATTENDING, "OK, no problem.", null);

		engine.add(ConversationStates.ATTENDING, behaviour.getProductionActivity(),
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
					return player.hasQuest(behaviour.getQuestSlot()) && !player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					npc.say("I still haven't finished your last order. Come back in "
					        + behaviour.getApproximateRemainingTime(player) + "!");
				}
			}
		);

		engine.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
					return player.hasQuest(behaviour.getQuestSlot()) && !player.isQuestCompleted(behaviour.getQuestSlot());
				}
			},
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					behaviour.giveProduct(npc, player);
				}
			}
		);
	}

}
