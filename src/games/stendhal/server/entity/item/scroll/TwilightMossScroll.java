package games.stendhal.server.entity.item.scroll;

import java.util.Map;

/**
 * Represents the balloon that takes the player to twilight zone,
 * after which it will teleport player to a random location in ida's sewing room.
 */
public class TwilightMossScroll extends TimedTeleportScroll {

	/**
	 * Creates a new timed marked TwilightMossScroll scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public TwilightMossScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public TwilightMossScroll(final TwilightMossScroll item) {
		super(item);
	}

	@Override
	protected String getBeforeReturnMessage() {
		return "The twilight is dwindling ...";
	}

	@Override
	protected String getAfterReturnMessage() {
		return "You wake up back in Ida's familiar sewing room.";
	}
}
