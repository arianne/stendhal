package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * An inverse condition
 */
public class AndCondition extends SpeakerNPC.ChatCondition {

	private List<SpeakerNPC.ChatCondition> conditions;

	/**
	 * Creates a new "and"-condition
	 *
	 * @param condition condition which should be and-ed.
	 */
	public AndCondition(SpeakerNPC.ChatCondition... condition) {
		this.conditions = Arrays.asList(condition);
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		for (SpeakerNPC.ChatCondition condition : conditions) {
			boolean res = condition.fire(player, text, engine);
			if (!res) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return conditions.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((conditions == null) ? 0 : conditions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final AndCondition other = (AndCondition) obj;
		if (conditions == null) {
			if (other.conditions != null) return false;
		} else if (!conditions.equals(other.conditions)) return false;
		return true;
	}

}