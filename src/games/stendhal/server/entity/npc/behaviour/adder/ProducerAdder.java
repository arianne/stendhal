package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

public class ProducerAdder {
	private static Logger logger = Logger.getLogger(ProducerAdder.class);
	
    private final ProducerRegister producerRegister = SingletonRepository.getProducerRegister();
    
    /** Adds all the dialogue associated with a Producing NPC */
	public void addProducer(final SpeakerNPC npc, final ProducerBehaviour behaviour,
			final String welcomeMessage) {

        /** Which NPC is this? */
		final Engine engine = npc.getEngine();

        /** What quest slot is the production stored in? */
        final String QUEST_SLOT = behaviour.getQuestSlot();

        /** How should we greet the player? */
		final String thisWelcomeMessage = welcomeMessage;
		
		/** What is the NPC name? */
		final String npcName = npc.getName();
		
		/* add to producer register */
		producerRegister.add(npcName, behaviour);		

        /* If the NPC is attending another player, say who they are attending */
		npc.addWaitMessage(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
				engine.say("Please wait! I am attending "
						+ engine.getAttending().getName() + ".");
			}
		});

        /* The Player greets the NPC.
        * The NPC is not currently producing for player (not started, is rejected, or is complete) */
		engine.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestNotActiveCondition(QUEST_SLOT), 
                ConversationStates.ATTENDING, thisWelcomeMessage, null);

        /* In the behaviour a production activity is defined, e.g. 'cast' or 'mill' 
        * and this is used as the trigger to start the production,
        * provided that the NPC is not currently producing for player (not started, is rejected, or is complete) */		
        engine.add(
				ConversationStates.ATTENDING,
				behaviour.getProductionActivity(),
				new QuestNotActiveCondition(QUEST_SLOT), 
                ConversationStates.ATTENDING, 
                null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
                        // TODO: this can be a part of a separate condition-action block
						if (sentence.hasError()) {
							npc.say("Sorry, I did not understand you. "
									+ sentence.getErrorString());
						} else {
							boolean found = behaviour.parseRequest(sentence);

    						// Find out how much items we shall produce.
    						if (!found && (behaviour.getChosenItemName() == null)) {
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
    						} else {
    							if (behaviour.getItemNames().size() == 1) { 
    								npc.say("Sorry, I can only produce " + behaviour.getItemNames().iterator().next() + ".");
    							} else {
    								npc.say("Sorry, I don't understand you.");
    							}
    						}
						}
					}
				});

        /* Player agrees to the proposed production deal */
		engine.add(ConversationStates.PRODUCTION_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC npc) {
						behaviour.transactAgreedDeal(npc, player);
					}
				});

        /* Player does not agree to the proposed production deal */
		engine.add(ConversationStates.PRODUCTION_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "OK, no problem.", null);

        /* Player says the production trigger word but the NPC is already producing items for that player */
		engine.add(
				ConversationStates.ATTENDING,
				behaviour.getProductionActivity(),
				new QuestActiveCondition(QUEST_SLOT), 
                ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC npc) {
                        // TODO: check - can the StateRemainingTimeAction be used here? 
						npc.say("I still haven't finished your last order. Come back in "
								+ behaviour.getApproximateRemainingTime(player)
								+ "!");
					}
				});

        /* Player greets NPC and the NPC is already producing items for that player
         * There are two options: the NPC is still busy or he is finished
         * The method giveProduct(npc, player) used here takes care of both. */
		engine.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT), 
                ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC npc) {
						behaviour.giveProduct(npc, player);
					}
				});
	}

}
