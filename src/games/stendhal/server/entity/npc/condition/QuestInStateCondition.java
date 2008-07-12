package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is this quest in this state?
 */
public class QuestInStateCondition extends SpeakerNPC.ChatCondition {

	private final String questname;
	private final String state;

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
		this.state = state;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		return (player.hasQuest(questname) && player.getQuest(questname).equals(
				state));
	}

	@Override
	public String toString() {
		return "QuestInState <" + questname + "," + state + ">";
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