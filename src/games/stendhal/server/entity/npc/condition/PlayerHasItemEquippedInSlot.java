package games.stendhal.server.entity.npc.condition;

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Check if the Player has equipped an item in a specified slot
 *
 * @author madmetzger
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerHasItemEquippedInSlot implements ChatCondition {

	private final String item;

	private final String slot;

	/**
	 * Check if the Player has equipped an item in a specified slot
	 *
	 * @param item name of item
	 * @param slot name of slot
	 */
	public PlayerHasItemEquippedInSlot(final String item, final String slot) {
		this.item = checkNotNull(item);
		this.slot = checkNotNull(slot);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		return player.isEquippedItemInSlot(this.slot, this.item);
	}

	@Override
	public int hashCode() {
		return 43889 * item.hashCode() + slot.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasItemEquippedInSlot)) {
			return false;
		}
		PlayerHasItemEquippedInSlot other = (PlayerHasItemEquippedInSlot) obj;
		return item.equals(other.item)
			&& slot.equals(other.slot);
	}

}
