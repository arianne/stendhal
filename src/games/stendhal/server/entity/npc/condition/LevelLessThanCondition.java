package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player's level smaller than the specified one?
 */
public class LevelLessThanCondition implements ChatCondition {

	private final int level;

	/**
	 * Creates a new LevelGreaterThanCondition.
	 * 
	 * @param level
	 *            level '
	 */
	public LevelLessThanCondition(final int level) {
		this.level = level;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.getLevel() < level);
	}

	@Override
	public String toString() {
		return "level < " + level + " ";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				LevelLessThanCondition.class);
	}

}
