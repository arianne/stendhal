package games.stendhal.server.entity.item.scroll;

import java.util.Map;

/**
 * Represents the balloon that takes the player to 7 kikareukin clouds,
 * after which it will teleport player to a random location in 6 kikareuikin islands.
 */
public class BalloonScroll extends TimedTeleportScroll {

	/**
	 * Creates a new timed marked BalloonScroll scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public BalloonScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public BalloonScroll(final BalloonScroll item) {
		super(item);
	}

	@Override
	protected String getBeforeReturnMessage() {
		return "It feels like the clouds won't take your weight much longer ... ";
	}

	@Override
	protected String getAfterReturnMessage() {
		return "You fell through a hole in the clouds, back to solid ground.";
	}
}
