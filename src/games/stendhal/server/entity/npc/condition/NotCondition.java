package games.stendhal.server.entity.npc.condition;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * An inverse condition
 */
public class NotCondition extends SpeakerNPC.ChatCondition {

	private SpeakerNPC.ChatCondition condition;

	/**
	 * Creates a new "not"-condition
	 *
	 * @param condition condition which result is to be inversed
	 */
	public NotCondition(SpeakerNPC.ChatCondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		return !condition.fire(player, sentence, engine);
	}

	@Override
	public String toString() {
		return "not <" + condition + ">";
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