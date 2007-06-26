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
}
