package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is constructed from a group of conditions. <p>
 * It evaluates to <code>true</code>, iff each condition evaluates to true. 
 */
public class AndCondition extends SpeakerNPC.ChatCondition {

	private List<SpeakerNPC.ChatCondition> conditions;

	/**
	 * Creates a new "and"-condition.
	 * 
	 * @param condition
	 *            condition which should be and-ed.
	 */
	public AndCondition(SpeakerNPC.ChatCondition... condition) {
		this.conditions = Arrays.asList(condition);
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		for (SpeakerNPC.ChatCondition condition : conditions) {
			boolean res = condition.fire(player, sentence, engine);
			if (!res) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return conditions.toString();
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