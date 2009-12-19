package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowItemListEvent;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

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
	private static Logger logger = Logger.getLogger(TestShowItemList.class);

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		List<Item> itemList = new LinkedList<Item>();

		Item item = SingletonRepository.getEntityManager().getItem("club");
		item.put("price", 100);
		item.put("description", item.describe());
		itemList.add(item);

		item = SingletonRepository.getEntityManager().getItem("leather armor");
		item.put("price", -100);
		item.put("description", item.describe());
		itemList.add(item);

		item = SingletonRepository.getEntityManager().getItem("ice sword");
		item.put("price", -10000);
		item.put("description", item.describe());
		itemList.add(item);

		ShowItemListEvent event = new ShowItemListEvent("Aramyk Shop", itemList);
		logger.info(event);
		admin.addEvent(event);
	}
}
