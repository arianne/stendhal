package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Was this quest started?
 */
public class QuestStartedCondition extends SpeakerNPC.ChatCondition {

	protected String questname;

	/**
	 * Creates a new QuestStartedCondition.
	 * 
	 * @param questname
	 *            name of quest slot
	 */
	public QuestStartedCondition(final String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		return (player.hasQuest(questname) && !"rejected".equals(player.getQuest(questname)));
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
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestStartedCondition.class);
	}
}