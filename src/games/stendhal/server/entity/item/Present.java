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
	public Present(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		setContent(ITEMS[Rand.rand(ITEMS.length)]);
	}

	/**
	 * Sets content.
	 * @param type of item to be produced.
	 */
	public void setContent(final String type) {
		setInfoString(type);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Present(final Present item) {
		super(item);
	}

	@Override
	protected boolean useMe(final Player player) {
		this.removeOne();

		final String itemName = getInfoString();
		final Item item = SingletonRepository.getEntityManager().getItem(itemName);
		player.sendPrivateText("Congratulations, you've got " 
				+ Grammar.a_noun(ItemTools.itemNameToDisplayName(itemName)) + "!");

		player.equipOrPutOnGround(item);
		player.notifyWorldAboutChanges();

		return true;
	}

}
