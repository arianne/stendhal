package games.stendhal.server.entity.npc.condition;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks if a player has looted a minimum number of item(s).
 *
 * @author madmetzger
 */
@Dev(category=Category.ITEMS_LOOTED, label="Item?")
public class PlayerLootedNumberOfItemsCondition implements ChatCondition {

	private final List<String> items;

	private final int number;

	/**
	 * Create a new PlayerLootedNumberOfItemsCondition
	 *
	 * @param number required number of each item
	 * @param item list of required items
	 */
	public PlayerLootedNumberOfItemsCondition(int number, String... item) {
		this.number = number;
		items = new LinkedList<String>();
		if (item != null) {
			for (String string : item) {
				items.add(string);
			}
		}
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for (String item : items) {
			if (player.getNumberOfLootsForItem(item) < number) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return 43991 * items.hashCode() + number;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerLootedNumberOfItemsCondition)) {
			return false;
		}
		PlayerLootedNumberOfItemsCondition other = (PlayerLootedNumberOfItemsCondition) obj;
		return (number == other.number)
			&& items.equals(other.items);
	}

	@Override
	public String toString() {
		return "player has looted <"+number+" of "+items.toString()+">";
	}

}
