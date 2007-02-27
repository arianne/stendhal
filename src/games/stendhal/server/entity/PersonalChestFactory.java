/*
 * @(#) src/games/stendhal/server/entity/PersonalChestFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A factory for <code>PersonalChest</code> objects.
 */
public class PersonalChestFactory implements ConfigurableFactory {
	//
	// PersonalChestFactory
	//

	/**
	 * Extract the slot name from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The slot name.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is invalid.
	 */
	protected String getSlot(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("slot")) == null) {
			return PersonalChest.DEFAULT_BANK;
		}

		return s;
	}


	//
	// ConfigurableFactory
	//

	/**
	 * Create a personal chest.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A PersonalChest.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		PersonalChest
	 */
	public Object create(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		return new PersonalChest(getSlot(ctx));
	}
}
