package games.stendhal.server.entity.item;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;

import java.util.Map;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;


/**
 * a box which can be unwrapped.
 *
 * @author hendrik
 */
public class Box extends Item implements UseListener {
	private Logger logger = Logger.getLogger(Box.class);
	// for christmas presents
	private static final String[] ITEMS = {"greater_potion", "pie", "sandwich", "carrot", "cherry", "elf_cloak_+2", "summon_scroll"};
	// for easter presents
	private static final String[] ITEMS_2 = {"greater_potion", "pie", "sandwich", "dice", "cherry", "elf_cloak_+2", "home_scroll"};
	/**
	 * Creates a new box
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Box(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public void onUsed(RPEntity user) {
		// TODO: clean up duplicated code with other Item subclasses.
		if (this.isContained()) {
			// We modify the base container if the object change.
			RPObject base = this.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			if (!user.nextTo((Entity) base, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		} else {
			if (!user.nextTo(this, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		}
		
		Player player = (Player) user;
		String name = getName();
		if (name.equals("present")||name.equals("basket")) {
			usePresent(player);
		} else {
			player.sendPrivateText("What a strange box! You don't know how to open it.");
		}
	}

	private void usePresent(Player player) {
		this.removeOne();
		// change line below if you want ITEMS from christmas
		String itemName = ITEMS_2[Rand.rand(ITEMS_2.length)];
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemName);
		player.sendPrivateText("Congratulations, you've got " + Grammar.a_noun(itemName));
		player.equip(item, true);
		player.notifyWorldAboutChanges();
	}
}
