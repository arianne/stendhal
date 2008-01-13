package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is this quest not in this state?
 */
public class QuestNotInStateCondition extends SpeakerNPC.ChatCondition {

	private String questname;
	private String state;

	/**
	 * Creates a new QuestNotInStateCondition.
	 * 
	 * @param questname
	 *            name of quest-slot
	 * @param state
	 *            state
	 */
	public QuestNotInStateCondition(String questname, String state) {
		this.questname = questname;
		this.state = state;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		return (!player.hasQuest(questname) || !player.getQuest(questname).equals(
				state));
	}

	@Override
	public String toString() {
		return "QuestNotInState <" + questname + "," + state + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestStartedCondition.class);
	}
}