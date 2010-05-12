package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Does the player have a pet or sheep?
 */
public class PlayerHasPetOrSheepCondition implements ChatCondition {

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
	    return (player.hasPet() || player.hasSheep());
	}

	@Override
	public String toString() {
		return "player has pet or sheep";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerHasPetOrSheepCondition.class);
	}
}
