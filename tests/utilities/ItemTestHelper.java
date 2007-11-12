package utilities;

import games.stendhal.server.entity.item.Item;

public class ItemTestHelper  {

	public static Item createItem() {
		PlayerHelper.generateItemRPClasses();
		return new Item("item","itemclass","subclass",null);

	}
	public static Item createItem(String name) {
		PlayerHelper.generateItemRPClasses();
		return new Item(name,"itemclass","subclass",null);

	}



}
