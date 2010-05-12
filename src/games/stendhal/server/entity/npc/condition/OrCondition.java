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
 * An or condition.
 */
public class OrCondition implements ChatCondition {

	private final List<ChatCondition> conditions;

	/**
	 * Creates a new "or"-condition.
	 * 
	 * @param condition
	 *            condition which should be or-ed.
	 */
	public OrCondition(final ChatCondition... condition) {
		this.conditions = Arrays.asList(condition);
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		for (final ChatCondition condition : conditions) {
			final boolean res = condition.fire(player, sentence, entity);
			if (res) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "or <" + conditions.toString() + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				OrCondition.class);
	}
}
