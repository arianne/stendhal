package games.stendhal.server.entity.npc.fsm;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * This action is executed after a successful transition of the state maschine.
 *
 * @author hendrik
 */
public interface PostTransitionAction {

	/**
	 * does some action after a transition
	 *
	 * @param player player who caused the transition
	 * @param text   text he/she said
	 * @param engine the NPC doing the transition
	 */
	public void fire(Player player, String text, SpeakerNPC engine);
}