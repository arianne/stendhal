package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * An inverse condition
 */
public class Not extends SpeakerNPC.ChatCondition {

	private SpeakerNPC.ChatCondition condition;

	/**
	 * Creates a new "not"-condition
	 *
	 * @param condition condition which result is to be inversed
	 */
	public Not(SpeakerNPC.ChatCondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return !condition.fire(player, text, engine);
	}

}