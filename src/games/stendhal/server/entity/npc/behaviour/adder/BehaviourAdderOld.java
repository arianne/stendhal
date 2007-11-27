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



}
