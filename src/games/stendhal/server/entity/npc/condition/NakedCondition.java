package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player naked? (e. g. not wearing anything on his/her body)
 */
public class NakedCondition implements ChatCondition {

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final Outfit outfit = player.getOutfit();
		return outfit.isNaked();
	}

	@Override
	public String toString() {
		return "naked?";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				NakedCondition.class);
	}
}
