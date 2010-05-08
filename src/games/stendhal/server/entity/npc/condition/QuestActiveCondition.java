package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Was this quest started but not completed?
 */
public class QuestActiveCondition implements ChatCondition {

	private final String questname;

	/**
	 * Creates a new QuestActiveCondition.
	 * 
	 * @param questname
	 *            name of quest slot
	 */
	public QuestActiveCondition(final String questname) {
		this.questname = questname;
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		return (player.hasQuest(questname) && !player.isQuestInState(questname, "rejected") && !player.isQuestCompleted(questname));
	}

	@Override
	public String toString() {
		return "QuestActive <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestActiveCondition.class);
	}
}
