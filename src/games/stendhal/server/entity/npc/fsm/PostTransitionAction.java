package games.stendhal.server.entity.npc.fsm;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * This action is executed after a successful transition of the state machine.
 * 
 * @author hendrik
 */
public interface PostTransitionAction {

	/**
	 * does some action after a transition.
	 * 
	 * @param player
	 *            player who caused the transition
	 * @param sentence
	 *            text he/she said
	 * @param engine
	 *            the NPC doing the transition
	 */
	void fire(Player player, Sentence sentence, SpeakerNPC engine);
}
