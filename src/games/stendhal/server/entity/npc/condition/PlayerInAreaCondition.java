package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

/**
 * Is the player in the specified area?
 */
public class PlayerInAreaCondition extends SpeakerNPC.ChatCondition {

	private Area area;

	public PlayerInAreaCondition(Area area) {
		this.area = area;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return area.contains(player);
	}

}