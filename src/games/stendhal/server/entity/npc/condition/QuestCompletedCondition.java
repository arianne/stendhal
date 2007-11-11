package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Was this quest completed?
 */
public class QuestCompletedCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	/**
	 * Creates a new QuestCompletedCondition
	 *
	 * @param questname name of quest-slot
	 */
	public QuestCompletedCondition(String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (player.isQuestCompleted(questname));
	}

	@Override
	public String toString() {
		return "QuestCompleted <" + questname + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((questname == null) ? 0 : questname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final QuestCompletedCondition other = (QuestCompletedCondition) obj;
		if (questname == null) {
			if (other.questname != null) return false;
		} else if (!questname.equals(other.questname)) return false;
		return true;
	}
}