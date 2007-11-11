package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

/**
 * Is the player in the specified area?
 */
public class PlayerInAreaCondition extends SpeakerNPC.ChatCondition {

	private Area area;

	/**
	 * Creates a new PlayerInAreaCondition
	 *
	 * @param area Area
	 */
	public PlayerInAreaCondition(Area area) {
		this.area = area;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return area.contains(player);
	}

	@Override
	public String toString() {
		return "player in <" + area + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((area == null) ? 0 : area.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final PlayerInAreaCondition other = (PlayerInAreaCondition) obj;
		if (area == null) {
			if (other.area != null) return false;
		} else if (!area.equals(other.area)) return false;
		return true;
	}

}