package utilities.RPClass;

import marauroa.common.game.RPClass;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

public class ItemTestHelper {

	public static Item createItem() {
		ItemTestHelper.generateRPClasses();
		return new Item("item", "itemclass", "subclass", null);
	}

	public static Item createItem(final String name) {
		ItemTestHelper.generateRPClasses();
		return new Item(name, "itemclass", "subclass", null);
	}
	
	public static Item createItem(final String name, final int quantity) {
		ItemTestHelper.generateRPClasses();
		final StackableItem item = new StackableItem(name, "itemclass", "subclass", null);
		item.setQuantity(quantity);
		return item;
	}

	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();

		if (!RPClass.hasRPClass("item")) {
			Item.generateRPClass();
		}
	}

}
