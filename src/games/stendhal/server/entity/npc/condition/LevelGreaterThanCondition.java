package games.stendhal.server.entity.npc.condition;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Is the player's level less than the specified one?
 */
public class LevelGreaterThanCondition extends SpeakerNPC.ChatCondition {

	private int level;


	/**
	 * Creates a new LevelLessThanCondition
	 *
	 * @param level level
'	 */
	public LevelGreaterThanCondition(int level) {
		this.level = level;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
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
		return EqualsBuilder.reflectionEquals(this, obj, false, LevelGreaterThanCondition.class);
	}

}
