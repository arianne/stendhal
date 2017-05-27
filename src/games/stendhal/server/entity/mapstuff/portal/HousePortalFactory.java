/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>HousePortal</code> objects.
 */
public class HousePortalFactory extends AccessCheckingPortalFactory {

	//
	// StoredKeyedPortalFactory
	//

	/**
	 * Extract the portal key's doorId from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The key doorId.
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected String getKey(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("doorId");
	}

	//
	// AccessCheckingPortalFactory
	//

	/**
	 * Create a stored keyed portal.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The portal.
	 *
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	@Override
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
		return new HousePortal(getKey(ctx));
	}
}
