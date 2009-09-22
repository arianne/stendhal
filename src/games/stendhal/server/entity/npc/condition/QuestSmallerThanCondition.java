package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is this quest state smaller than the value in this condition?
 */
public class QuestSmallerThanCondition implements ChatCondition {

	private final String questname;
	private final int index;
	private final int state;

	/**
	 * Creates a new QuestSmallerThanCondition.
	 * 
	 * @param questname name of quest-slot
	 * @param state state
	 */
	public QuestSmallerThanCondition(final String questname, final int state) {
		this.questname = questname;
		this.index = -1;
		this.state = state;
	}


	/**
	 * Creates a new QuestSmallerThanCondition.
	 * 
	 * @param questname name of quest-slot
	 * @param index index of sub state
	 * @param state state
	 */
	public QuestSmallerThanCondition(final String questname, final int index, final int state) {
		this.questname = questname;
		this.index = index;
		this.state = state;
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		if (!player.hasQuest(questname)) {
			return false;
		}
		String questState;
		if (index > -1) {
			questState = player.getQuest(questname, index);
		} else {
			questState = player.getQuest(questname);
		}

		int questStateInt;
		try {
			questStateInt = Integer.parseInt(questState);
		} catch (NumberFormatException e) {
			return false;
		}

		return questStateInt < state;
	}

	@Override
	public String toString() {
		return "QuestSmallerThan <" + questname + "[" + index + "] = " + state + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestStartedCondition.class);
	}
}
