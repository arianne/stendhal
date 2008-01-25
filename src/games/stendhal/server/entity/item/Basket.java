package games.stendhal.server.entity.item;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * a basket which can be unwrapped.
 * 
 * @author kymara
 */
public class Basket extends Box {

	// TODO: Make these configurable
	// for easter presents
	private static final String[] ITEMS = { "greater potion", "pie",
			"sandwich", "cherry", "blue elf cloak", "home scroll" };

	/**
	 * Creates a new Basket.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Basket(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Basket(Basket item) {
		super(item);
	}

	@Override
	protected boolean useMe(Player player) {
		this.removeOne();
		String itemName;
		if (Rand.roll1D20() == 1) {
			itemName = "easter egg";
		} else {
			itemName = ITEMS[Rand.rand(ITEMS.length)];
		}
		Item item = SingletonRepository.getEntityManager().getItem(
				itemName);
		if (itemName.equals("easter egg")) {
			item.setBoundTo(player.getName());
		}
		player.sendPrivateText("Congratulations, you've got "
				+ Grammar.a_noun(itemName));
		player.equip(item, true);
		player.notifyWorldAboutChanges();
		return true;
	}

}
