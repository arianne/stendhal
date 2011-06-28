package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.RepairingBehaviourAction;
import games.stendhal.server.entity.npc.behaviour.impl.RepairerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.prices.RepairingPriceCalculationStrategy;
import games.stendhal.server.entity.npc.fsm.Engine;

import java.util.HashSet;

public class RepairerAdder {
	
	public void addRepairer(final SpeakerNPC npc, final int cost) {
		final RepairerBehaviour repairerBehaviour = new RepairerBehaviour(new RepairingPriceCalculationStrategy(new HashSet<String>()));
		final Engine engine = npc.getEngine();

		engine.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES, null,
				false, ConversationStates.ATTENDING, "I can #repair items for you.", null);

		engine.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				null, new RepairingBehaviourAction(repairerBehaviour));

		engine.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				"OK, how else may I help you?", null);
	}

}
