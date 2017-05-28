/* $Id$ */
/***************************************************************************
 *                    (C) Copyright 2003-2010 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.sign;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>ShopSign</code> objects.
 */
public class ShopSignFactory implements ConfigurableFactory {
	//
	// ShopSignFactory
	//

	/**
	 * Extract the shop name from a context.
	 *
	 * @param ctx
	 *            The configuration context. Must provide 'shop'.
	 *
	 * @return The shop name.
	 */
	protected String getShop(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("shop");
	}

	/**
	 * Extract the sign title from a context.
	 *
	 * @param ctx
	 *            The configuration context. Must provide 'title'.
	 *
	 * @return The sign title.
	 */
	protected String getTitle(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("title");
	}

	/**
	 * Extract the selling/buying-type from a context.
	 *
	 * @param ctx
	 *            The configuration context. Must provide 'seller'.
	 *
	 * @return The sign title.
	 */
	private boolean getSeller(ConfigurableFactoryContext ctx) {
		// TODO: make this a required field
		return ctx.getBoolean("seller", true);
	}

	/**
	 * Extract the caption from a context.
	 *
	 * @param ctx
	 *            The configuration context. May provide 'caption'.
	 *
	 * @return The sign title.
	 */
	private String getCaption(ConfigurableFactoryContext ctx) {
		return ctx.getString("caption", null);
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a shop sign.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return A ShopSign.
	 *
	 * @see ShopSign
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		return new ShopSign(getShop(ctx), getTitle(ctx), getCaption(ctx), getSeller(ctx));
	}
}
