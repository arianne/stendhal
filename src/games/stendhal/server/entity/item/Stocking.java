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
public class Stocking extends Box {

	private static final String[] ITEMS = { "mega potion", "fish pie",
			"lucky charm", "diamond", "gold bar", "empty scroll" };

	/**
	 * Creates a new Stocking.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Stocking(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Stocking(final Stocking item) {
		super(item);
	}

	@Override
	protected boolean useMe(final Player player) {
		this.removeOne();
		final String itemName = ITEMS[Rand.rand(ITEMS.length)];
		final Item item = SingletonRepository.getEntityManager().getItem(
				itemName);
		player.sendPrivateText("Congratulations, you've got "
				+ Grammar.a_noun(itemName));
		player.equip(item, true);
		player.notifyWorldAboutChanges();
		return true;
	}

}
