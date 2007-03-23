/*
 * @(#) src/games/stendhal/server/entity/portal/DoorFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;
import games.stendhal.common.Direction;

/**
 * A base factory for <code>Door</code> objects.
 */
public abstract class DoorFactory implements ConfigurableFactory {

	//
	// DoorFactory
	//

	/**
	 * Extract the door class from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The class name.
	 *
	 * @throws	IllegalArgumentException
	 *				If the class attribute is missing.
	 */
	protected String getClass(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("class")) == null) {
			throw new IllegalArgumentException("Required attribute 'class' missing");
		}

		return s;
	}

	/**
	 * Extract the door direction from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The direction.
	 *
	 * @throws	IllegalArgumentException
	 *				If the direction attribute is missing
	 *				or invalid.
	 */
	protected Direction getDirection(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("direction")) == null) {
			throw new IllegalArgumentException("Required attribute 'direction' missing");
		}

		if (s.equals("up")) {
			return Direction.UP;
		} else if (s.equals("down")) {
			return Direction.DOWN;
		} else if (s.equals("left")) {
			return Direction.LEFT;
		} else if (s.equals("right")) {
			return Direction.RIGHT;
		}

		throw new IllegalArgumentException("Invalid direction attribute: " + s);
	}
}
