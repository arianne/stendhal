package games.stendhal.server.maps.quests.logic;

import java.util.LinkedList;
import java.util.List;

public class ItemCollector {

	private final List<ItemCollectorData> requiredItems = new LinkedList<>();

	public ItemCollectorSetters require() {
		ItemCollectorData itemData = new ItemCollectorData();
		requiredItems.add(itemData);
		return itemData;
	}

	public List<ItemCollectorData> requiredItems() {
		return requiredItems;
	}
}
