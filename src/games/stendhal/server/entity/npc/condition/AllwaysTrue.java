package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * This condition returns always true. Use it in a quest file to override
 * behaviour defined in the map file
 */
public class AllwaysTrue extends SpeakerNPC.ChatCondition {

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return true;
	}

}