package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		return (player.hasQuest(questname));
	}

	@Override
	public String toString() {
		return "QuestStarted <" + questname + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false, QuestStartedCondition.class);
	}
}