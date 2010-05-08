package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is this quest in this state?
 */
public class QuestInStateCondition implements ChatCondition {

	private final String questname;
	private final String state;
	private final int index;

	/**
	 * Creates a new QuestInStateCondition.
	 * 
	 * @param questname
	 *            name of quest-slot
	 * @param state
	 *            state
	 */
	public QuestInStateCondition(final String questname, final String state) {
		this.questname = questname;
		this.index = -1;
		this.state = state;
	}


	/**
	 * Creates a new QuestInStateCondition.
	 * 
	 * @param questname
	 *            name of quest-slot
	 * @param index
	 *            index of sub state
	 * @param state
	 *            state
	 */
	public QuestInStateCondition(final String questname, final int index, final String state) {
		this.questname = questname;
		this.index = index;
		this.state = state;
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		if (!player.hasQuest(questname)) {
			return false;
		}
		if (index > -1) {
			return player.getQuest(questname, index).equals(state);
		} else {
			return player.getQuest(questname).equals(state);
		}
	}

	@Override
	public String toString() {
		return "QuestInState <" + questname + "[" + index + "] = " + state + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestInStateCondition.class);
	}
}
