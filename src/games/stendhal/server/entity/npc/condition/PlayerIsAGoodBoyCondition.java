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
 * Check if a player is a good boy, e. g. has not recently killed another player.
 *
 * @author madmetzger
 */
@Dev(category=Category.OTHER, label="GoodBoy?")
public class PlayerIsAGoodBoyCondition implements ChatCondition {

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return !player.isBadBoy();
	}

	@Override
	public String toString() {
		return "goodboy";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerIsAGoodBoyCondition.class);
	}

}
