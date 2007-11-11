package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Was this quest started?
 */
public class QuestStartedCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	/**
	 * Creates a new QuestStartedCondition
	 *
	 * @param questname name of quest slot
	 */
	public QuestStartedCondition(String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (player.hasQuest(questname));
	}

	@Override
	public String toString() {
		return "QuestStarted <" + questname + ">";
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
		final QuestStartedCondition other = (QuestStartedCondition) obj;
		if (questname == null) {
			if (other.questname != null) return false;
		} else if (!questname.equals(other.questname)) return false;
		return true;
	}
}