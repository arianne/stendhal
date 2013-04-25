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
 * Did the player get at least this number of items from the wishing well?
 *
 * @author madmetzger
 */
@Dev(category=Category.ITEMS_LOOTED, label="Item?")
public class PlayerGotNumberOfItemsFromWellCondition implements ChatCondition {

	private final int number;

	/**
	 * PlayerGotNumberOfItemsFromWellCondition
	 *
	 * @param quantity required number of items
	 */
	public PlayerGotNumberOfItemsFromWellCondition(int quantity) {
		number = quantity;
	}

	@Override
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
