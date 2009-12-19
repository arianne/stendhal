package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowItemListEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates a portable NPC who gives ALL players powerful items, increases their
 * level and makes them admins. This is used on test-systems only. Therefore it
 * is disabled in default install and you have to use this parameter:
 * -Dstendhal.testserver=junk as a vm argument.
 * 
 * As admin uses /script AdminMaker.class to summon her right next to him/her.
 * Please unload it with /script -unload AdminMaker.class
 */

public class TestShowItemList extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		List<Item> itemList = new LinkedList<Item>();

		if (args.isEmpty()) {
			itemList.add(prepareItem("club", 100));
			itemList.add(prepareItem("leather armor", -100));
			itemList.add(prepareItem("ice sword", -10000));
		} else {
			ShopList shops = SingletonRepository.getShopList();
			Map<String, Integer> items = shops.get(args.get(0));
			for (Map.Entry<String, Integer> entry : items.entrySet()) {
				itemList.add(prepareItem(entry.getKey(), Integer.valueOf(entry.getValue())));
			}
		}

		ShowItemListEvent event = new ShowItemListEvent("Aramyk Shop", itemList);
		admin.addEvent(event);
	}

	/**
	 * prepares an item for displaying
	 *
	 * @param name   name of item
	 * @param price  price of item (negative is for cases in which the player has to pay money)
	 * @return Item
	 */
	private Item prepareItem(String name, int price) {
		Item item = SingletonRepository.getEntityManager().getItem(name);
		item.put("price", -price);
		item.put("description", item.describe());
		return item;
	}
}
