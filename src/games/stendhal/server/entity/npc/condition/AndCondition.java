package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is constructed from a group of conditions. <p>
 * It evaluates to <code>true</code>, iff each condition evaluates to true. 
 */
public class AndCondition implements ChatCondition {

	private final List<ChatCondition> conditions;

	/**
	 * Creates a new "and"-condition.
	 * 
	 * @param condition
	 *            condition which should be and-ed.
	 */
	public AndCondition(final ChatCondition... condition) {
		this.conditions = Arrays.asList(condition);
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		for (final ChatCondition condition : conditions) {
			final boolean res = condition.fire(player, sentence, entity);
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
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				AndCondition.class);
	}
}
