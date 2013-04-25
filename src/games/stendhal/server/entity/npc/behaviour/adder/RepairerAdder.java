package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ComplainAboutSentenceErrorAction;
import games.stendhal.server.entity.npc.action.RepairingBehaviourAction;
import games.stendhal.server.entity.npc.behaviour.impl.RepairerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.prices.RepairingPriceCalculationStrategy;
import games.stendhal.server.entity.npc.behaviour.journal.ServicersRegister;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.SentenceHasErrorCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.HashSet;
/**
 * Adds the needed state model parts for an NPC that offers repairing of items.
 * 
 * @author madmetzger
 */
public class RepairerAdder {
	
    private final ServicersRegister servicersRegister = SingletonRepository.getServicersRegister();
    
	/**
	 * Add the model fragments to the given NPC
	 * 
	 * @param npc the NPC to enrich
	 */
	public void addRepairer(final SpeakerNPC npc) {
		final RepairerBehaviour repairerBehaviour = new RepairerBehaviour(
						new RepairingPriceCalculationStrategy(new HashSet<String>()),
						new HashSet<String>(Arrays.asList("buckler")));
		
		servicersRegister.add(npc.getName(), repairerBehaviour);
		
		final Engine engine = npc.getEngine();
		
		//sentence was not recognized			
		ChatCondition errorCondition = new SentenceHasErrorCondition();
		engine.add(ConversationStates.ATTENDING, "repair", errorCondition,
				false, ConversationStates.ATTENDING,
				null, new ComplainAboutSentenceErrorAction());
		
		//sentence recognized but not able to repair
		ChatCondition cannotRepair = new AndCondition(
				new NotCondition(new SentenceHasErrorCondition()),
				new NotCondition(repairerBehaviour.getTransactionCondition()));
		
		engine.add(ConversationStates.ATTENDING, "repair", cannotRepair,
				false, ConversationStates.ATTENDING,
				null, repairerBehaviour.getRejectedTransactionAction());
		
		//recognized and able to repair
		ChatCondition recognizedCondition = new AndCondition(
				new NotCondition(new SentenceHasErrorCondition()),
				repairerBehaviour.getTransactionCondition());
		
		engine.add(ConversationStates.ATTENDING, "repair",
				recognizedCondition, false, ConversationStates.REPAIR_OFFERED,
				null, new RepairingBehaviourAction(repairerBehaviour));

		engine.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES, null,
				false, ConversationStates.ATTENDING, "I can #repair items for you.", null);

		ChatAction behaviourAcceptedAction =new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				repairerBehaviour.transactAgreedDeal(repairerBehaviour.parse(sentence), raiser, player);
			}
		};
		
		engine.add(ConversationStates.REPAIR_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				null, behaviourAcceptedAction);

		engine.add(ConversationStates.REPAIR_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				"OK, how else may I help you?", null);
	}

}
