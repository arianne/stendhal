/***************************************************************************
 *                    Copyright © 2007-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.chest;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

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
	 * @param ctx
	 *            The configuration context. 'slot' must be defined in ctx
	 * @return The slot name.
	 *
	 */
	protected String getSlot(final ConfigurableFactoryContext ctx) {
		return ctx.getString("slot", PersonalChest.DEFAULT_BANK);
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a personal chest.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return A PersonalChest.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 *
	 * @see PersonalChest
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		return new PersonalChest(getSlot(ctx));
	}
}
