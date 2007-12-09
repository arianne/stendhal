package utilities;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

public class ItemTestHelper {

	public static Item createItem() {
		PlayerTestHelper.generateItemRPClasses();
		return new Item("item", "itemclass", "subclass", null);

	}

	public static Item createItem(String name) {
		PlayerTestHelper.generateItemRPClasses();
		return new Item(name, "itemclass", "subclass", null);

	}
	
	public static Item createItem(String name, int quantity) {
		PlayerTestHelper.generateItemRPClasses();
		StackableItem item = new StackableItem(name, "itemclass", "subclass", null);
		item.setQuantity(quantity);
		return item;
	}

}
