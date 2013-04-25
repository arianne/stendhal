package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Condition to check if a player's mana is greater than a number
 *
 * @author madmetzger
 */
@Dev(category=Category.STATS, label="Mana?")
public class PlayerManaGreaterThanCondition implements ChatCondition {

	private final int mana;

	/**
	 * ManaGreaterThanCondition
	 *
	 * @param mana amount of mana required
	 */
	public PlayerManaGreaterThanCondition(int mana) {
		this.mana = mana;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return player.getMana() > this.mana;
	}


	@Override
	public String toString() {
		return "mana > " + this.mana;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerManaGreaterThanCondition.class);
	}
}
