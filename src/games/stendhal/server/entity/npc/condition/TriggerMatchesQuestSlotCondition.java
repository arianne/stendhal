package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * was the content of a quest slot said?
 */
@Dev(category=Category.CHAT, label="\"questtext\"?")
public class TriggerMatchesQuestSlotCondition implements ChatCondition {
	private String questname;
	private int index;

	/**
	 * creates a TriggerMatchesQuestSlotCondition
	 *
	 * @param questname name of quest
	 * @param index index of questslot
	 */
	public TriggerMatchesQuestSlotCondition(String questname, int index) {
		this.questname = questname;
		this.index = index;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return sentence.matchesNormalized(player.getQuest(questname, index));
	}

	@Override
	public String toString() {
		return "questtext? <" + questname + "[" + index + "]>";
	}

	@Override
	public int hashCode() {
		return 5021 * questname.hashCode() + index;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TriggerMatchesQuestSlotCondition)) {
			return false;
		}
		TriggerMatchesQuestSlotCondition other = (TriggerMatchesQuestSlotCondition) obj;
		if (this.index != other.index) {
			return false;
		}
		return questname.equals(other.questname);
	}
}
