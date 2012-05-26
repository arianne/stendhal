package games.stendhal.server.entity.npc.condition;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

public class PlayerGotNumberOfItemsFromWellCondition implements ChatCondition {
	
	private final int number;
	
	public PlayerGotNumberOfItemsFromWellCondition(int quantity) {
		number = quantity;
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return number < player.getQuantityOfObtainedItems();
	}
	
	@Override
	public String toString() {
		return "player has obtained "+number+" items from the ";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerGotNumberOfItemsFromWellCondition.class);
	}

}
