package games.stendhal.server.entity.item;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;

import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;

/**
 * a box which can be unwrapped.
 *
 * @author hendrik
 */
public class Box extends Item implements UseListener {

	private Logger logger = Log4J.getLogger(Box.class);

	// for christmas presents
	private static final String[] ITEMS = { "greater_potion", "pie", "sandwich", "carrot", "cherry", "blue_elf_cloak",
	        "summon_scroll" };

	// for easter presents
	private static final String[] ITEMS_2 = { "greater_potion", "pie", "sandwich", "cherry", "blue_elf_cloak",
	        "home_scroll" };

	/**
	 * Creates a new box
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Box(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 *
	 * @param item item to copy
	 */
	public Box(Box item) {
		super(item);
	}

	public boolean onUsed(RPEntity user) {
		// TODO: clean up duplicated code with other Item subclasses.
		if (this.isContained()) {
			// We modify the base container if the object change.
			RPObject base = this.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			if (!user.nextTo((Entity) base)) {
				logger.debug("Consumable item is too far");
				return false;
			}
		} else {
			if (!user.nextTo(this)) {
				logger.debug("Consumable item is too far");
				return false;
			}
		}

		Player player = (Player) user;
		String name = getName();
		if (name.equals("present")) {
			usePresent(player);
			return true;
		} else if (name.equals("basket")) {
			useBasket(player);
			return true;
		} else {
			player.sendPrivateText("What a strange box! You don't know how to open it.");
			return false;
		}
	}

	private void usePresent(Player player) {
		this.removeOne();
		String itemName = ITEMS[Rand.rand(ITEMS.length)];
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemName);
		player.sendPrivateText("Congratulations, you've got " + Grammar.a_noun(itemName));
		player.equip(item, true);
		player.notifyWorldAboutChanges();
	}

	private void useBasket(Player player) {
		this.removeOne();
		String itemName;
		if (Rand.roll1D20() == 1) {
			itemName = "easter_egg";
		} else {
			itemName = ITEMS_2[Rand.rand(ITEMS_2.length)];
		}
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemName);
		if (itemName == "easter_egg") {
			item.setBoundTo(player.getName());
			// item.setInfoString(Bunny);
			// it'd be nice to store the fact that these came from Bunny?
		}
		player.sendPrivateText("Congratulations, you've got " + Grammar.a_noun(itemName));
		player.equip(item, true);
		player.notifyWorldAboutChanges();
	}
}
