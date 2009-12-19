package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowItemListEvent;

import java.util.LinkedList;
import java.util.List;

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
		itemList.add(SingletonRepository.getEntityManager().getItem("club"));
		itemList.add(SingletonRepository.getEntityManager().getItem("leather armor"));
		ShowItemListEvent event = new ShowItemListEvent("Test-Title", itemList);
		admin.addEvent(event);
	}

}
