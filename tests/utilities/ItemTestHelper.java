package utilities;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

public class ItemTestHelper {

	public static Item createItem() {
		PlayerHelper.generateItemRPClasses();
		return new Item("item", "itemclass", "subclass", null);

	}

	public static Item createItem(String name) {
		PlayerHelper.generateItemRPClasses();
		return new Item(name, "itemclass", "subclass", null);

	}
	
	public static Item createItem(String name, int quantity) {
		PlayerHelper.generateItemRPClasses();
		StackableItem item = new StackableItem(name, "itemclass", "subclass", null);
		item.setQuantity(quantity);
		return item;
	}

}
