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
public class Stocking extends Box {

	// TODO: Make these configurable
	// for christmas presents
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
	public Stocking(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Stocking(Stocking item) {
		super(item);
	}

	@Override
	protected boolean useMe(Player player) {
		this.removeOne();
		String itemName = ITEMS[Rand.rand(ITEMS.length)];
		Item item = SingletonRepository.getEntityManager().getItem(
				itemName);
		player.sendPrivateText("Congratulations, you've got "
				+ Grammar.a_noun(itemName));
		player.equip(item, true);
		player.notifyWorldAboutChanges();
		return true;
	}

}
