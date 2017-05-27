package games.stendhal.server.entity.npc.condition;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks if a player has harvested a minimum number of an item
 *
 * @author madmetzger
 */
@Dev(category=Category.ITEMS_LOOTED, label="Item?")
public class PlayerHasHarvestedNumberOfItemsCondition implements ChatCondition {

	private final List<String> itemMinedList;

	private final int quantity;

	/**
	 * Checks if a player has harvested a minimum number of an item
	 *
	 * @param number required number of each item
	 * @param items list of items required
	 */
	public PlayerHasHarvestedNumberOfItemsCondition(int number, String... items) {
		itemMinedList = new ArrayList<String>();
		if (items != null) {
			for (String item : items) {
				itemMinedList.add(item);
			}
		}
		quantity = number;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for(String item : itemMinedList) {
			if(quantity > player.getQuantityOfHarvestedItems(item)) {
				return false;
			}
		}
		return true;
	}


	@Override
	public int hashCode() {
		return 43853 * itemMinedList.hashCode() + quantity;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasHarvestedNumberOfItemsCondition)) {
			return false;
		}
		PlayerHasHarvestedNumberOfItemsCondition other = (PlayerHasHarvestedNumberOfItemsCondition) obj;
		return (quantity == other.quantity)
			&& itemMinedList.equals(other.itemMinedList);
	}

	@Override
	public String toString() {
		return "player has harvested <"+quantity+" of "+itemMinedList+">";
	}

}
