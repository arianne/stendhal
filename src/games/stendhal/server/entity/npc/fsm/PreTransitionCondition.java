package games.stendhal.server.entity.npc.fsm;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * a condition to check before an transition is executed.
 * 
 * @author hendrik
 */
public interface PreTransitionCondition {

	/**
	 * can the transition be done?
	 * 
	 * @param player
	 *            player who caused the transition
	 * @param sentence
	 *            text he/she said
	 * @param entity
	 *            the NPC doing the transition
	 * @return true, if the transition is possible, false otherwise
	 */
	boolean fire(Player player, Sentence sentence, Entity entity);
}
