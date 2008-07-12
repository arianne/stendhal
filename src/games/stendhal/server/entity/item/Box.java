package games.stendhal.server.entity.item;

import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

import org.apache.log4j.Logger;
import marauroa.common.game.RPObject;

/**
 * a box which can be unwrapped.
 * 
 * @author hendrik
 */
public class Box extends Item implements UseListener {

	private final Logger logger = Logger.getLogger(Box.class);

	/**
	 * Creates a new box.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Box(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Box(final Box item) {
		super(item);
	}

	public boolean onUsed(final RPEntity user) {
		// TODO: clean up duplicated code with other Item subclasses.
		if (this.isContained()) {
			// We modify the base container if the object change.
			RPObject base = this.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			if (!user.nextTo((Entity) base)) {
				logger.debug("Consumable item is too far");
				user.sendPrivateText("The item is too far away.");
				return false;
			}
		} else {
			if (!user.nextTo(this)) {
				logger.debug("Consumable item is too far");
				user.sendPrivateText("The item is too far away.");
				return false;
			}
		}
		final Player player = (Player) user;
		return useMe(player);
	}

	// this would be overridden in the subclass
	protected boolean useMe(final Player player) {
		logger.warn("A box that didn't have a use method failed to open.");
		player.sendPrivateText("What a strange box! You don't know how to open it.");
		return false;
	}

}
