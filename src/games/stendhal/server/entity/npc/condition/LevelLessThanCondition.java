package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player's level smaller than the specified one?
 */
public class LevelLessThanCondition extends SpeakerNPC.ChatCondition {

	private int level;

	/**
	 * Creates a new LevelGreaterThanCondition.
	 * 
	 * @param level
	 *            level '
	 */
	public LevelLessThanCondition(int level) {
		this.level = level;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
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
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				LevelLessThanCondition.class);
	}

}
