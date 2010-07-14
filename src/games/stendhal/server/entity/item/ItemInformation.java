package games.stendhal.server.entity.item;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

public class ItemInformation extends Item {


	/**
	 * copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public ItemInformation(final Item item) {
		super(item);
		setRPClass("item_information");
	}


	public static void generateRPClass() {
		final RPClass entity = new RPClass("item_information");
		entity.isA("item");

		// Some things may have a textual description
		entity.addAttribute("description_info", Type.LONG_STRING);

		// used for show_item_list events used as shop signs.
		entity.addAttribute("price", Type.INT, Definition.VOLATILE);
	}
}
