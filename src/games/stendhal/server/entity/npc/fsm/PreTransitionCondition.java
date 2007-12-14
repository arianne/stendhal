package games.stendhal.server.entity.npc.fsm;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * a condition to check before an transition is executed
 * 
 * @author hendrik
 */
public interface PreTransitionCondition {

	/**
	 * can the transition be done?
	 * 
	 * @param player
	 *            player who caused the transition
	 * @param text
	 *            text he/she said
	 * @param engine
	 *            the NPC doing the transition
	 * @return true, if the transition is possible, false otherwise
	 */
	boolean fire(Player player, Sentence sentence, SpeakerNPC engine);
}
