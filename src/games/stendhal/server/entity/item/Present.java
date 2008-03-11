package games.stendhal.server.entity.item;

import games.stendhal.common.Grammar;
import games.stendhal.common.ItemTools;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * A present which can be unwrapped.
 * 
 * @author kymara
 */
public class Present extends Box {

	private static final String[] ITEMS = { "greater potion", "pie",
			"sandwich", "carrot", "cherry", "blue elf cloak", "summon scroll" };

	/**
	 * Creates a new present.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Present(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		setContent(ITEMS[Rand.rand(ITEMS.length)]);
	}

	/**
	 * Sets content.
	 */
	public void setContent(String type) {
		setInfoString(type);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Present(Present item) {
		super(item);
	}

	@Override
	protected boolean useMe(Player player) {
		this.removeOne();

		String itemName = getInfoString();
		Item item = SingletonRepository.getEntityManager().getItem(itemName);
		player.sendPrivateText("Congratulations, you've got " 
				+ Grammar.a_noun(ItemTools.itemNameToDisplayName(itemName)));

		player.equip(item, true);
		player.notifyWorldAboutChanges();

		return true;
	}

}
