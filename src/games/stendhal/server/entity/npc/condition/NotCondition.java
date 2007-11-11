package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * An inverse condition
 */
public class NotCondition extends SpeakerNPC.ChatCondition {

	private SpeakerNPC.ChatCondition condition;

	/**
	 * Creates a new "not"-condition
	 *
	 * @param condition condition which result is to be inversed
	 */
	public NotCondition(SpeakerNPC.ChatCondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return !condition.fire(player, text, engine);
	}

	@Override
	public String toString() {
		return "not <" + condition + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((condition == null) ? 0 : condition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final NotCondition other = (NotCondition) obj;
		if (condition == null) {
			if (other.condition != null) return false;
		} else if (!condition.equals(other.condition)) return false;
		return true;
	}

}