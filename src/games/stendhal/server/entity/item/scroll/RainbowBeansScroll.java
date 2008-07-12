/*
 * $Id$
 */
package games.stendhal.server.entity.item.scroll;

import java.util.Map;

/**
 * Represents the rainbow beans that takes the player to the dream world zone,
 * after which it will teleport player to a random location in 0_semos_plains_s.
 */
public class RainbowBeansScroll extends TimedTeleportScroll {

	/**
	 * Creates a new timed marked RainbowBeansScroll scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public RainbowBeansScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public RainbowBeansScroll(final RainbowBeansScroll item) {
		super(item);
	}

	@Override
	protected String getBeforeReturnMessage() {
		return "Your head begins to feel clearer...";
	}

	@Override
	protected String getAfterReturnMessage() {
		return "You find yourself in the forest with a bad headache."
				+ " That was a strange experience.";
	}
}
