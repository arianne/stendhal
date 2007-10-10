/*
 * $Id$
 */
package games.stendhal.server.entity.item.scroll;

import java.util.Map;

/**
 * Represents the rainbow beans that takes the player to the dream world zone, after which
 * it will teleport player to a random location in 0_semos_plains_s.
 * 
 * infostring attribute in items.xml: 1_dreamscape 77 35 5400 0_semos_plains_s -1 -1
 * where 1_dreamscape is the target zone name;
 * 	77 and 35 are the target x and y position;
 * 	5400 is the number of turns before return;
 * 	0_semos_plains_s is the return zone;
 * 	-1 and -1 are the return x and y positions (negative value means a random position)
 */
public class RainbowBeansScroll extends TimedTeleportScroll {

	/**
	 * Creates a new timed marked teleport scroll
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public RainbowBeansScroll(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 *
	 * @param item item to copy
	 */
	public RainbowBeansScroll(RainbowBeansScroll item) {
		super(item);
	}


	@Override
	protected String getBeforeReturnMessage() {
		return "Your head begins to feel clearer...";
	}

	@Override
	protected String getAfterReturnMessage() {
		return "You find yourself in the forest with a bad headache. That was a strange experience.";
	}
}
