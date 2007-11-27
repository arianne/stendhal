// $Id$
package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
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
