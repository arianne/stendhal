package games.stendhal.server.entity.item;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * a basket which can be unwrapped.
 * 
 * @author kymara
 */
public class Basket extends Box {


	private static final String[] ITEMS = { "mega potion", "fairy cake",
			"easter egg", "egg", "spotted egg", "mythical egg"};

	/**
	 * Creates a new Basket.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Basket(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Basket(final Basket item) {
		super(item);
	}

	@Override
	protected boolean useMe(final Player player) {
		this.removeOne();
		String itemName;
		itemName = ITEMS[Rand.rand(ITEMS.length)];
		final Item item = SingletonRepository.getEntityManager().getItem(
				itemName);
		if ("easter egg".equals(itemName)) {
			item.setBoundTo(player.getName());
		}
		player.sendPrivateText("Congratulations, you've got "
				+ Grammar.a_noun(itemName));
		player.equipOrPutOnGround(item);
		player.notifyWorldAboutChanges();
		return true;
	}

}
