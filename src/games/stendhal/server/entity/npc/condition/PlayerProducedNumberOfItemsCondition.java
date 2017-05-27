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
 * Checks if a player has produced a given number of items
 *
 * @author madmetzger
 */
@Dev(category=Category.ITEMS_PRODUCER, label="Item?")
public class PlayerProducedNumberOfItemsCondition implements ChatCondition {

	private final List<String> itemProducedList;

	private final int quantity;

	/**
	 * Checks if a player has produced a given number of items
	 *
	 * @param number required number of each item
	 * @param items list of required items
	 */
	public PlayerProducedNumberOfItemsCondition(int number, String... items) {
		itemProducedList = new ArrayList<String>();
		if (items != null) {
			for(String item : items) {
				itemProducedList.add(item);
			}
		}
		quantity = number;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for(String item : itemProducedList) {
			if(quantity > player.getQuantityOfProducedItems(item)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return 44027 * itemProducedList.hashCode() + quantity;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerProducedNumberOfItemsCondition)) {
			return false;
		}
		PlayerProducedNumberOfItemsCondition other = (PlayerProducedNumberOfItemsCondition) obj;
		return (quantity == other.quantity)
			&& itemProducedList.equals(other.itemProducedList);
	}

	@Override
	public String toString() {
		return "player has produced <"+quantity+" of "+itemProducedList+">";
	}

}
