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

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>ShopSign</code> objects.
 */
public class PopupImageFactory implements ConfigurableFactory {

	/**
	 * Extract the image name from a context.
	 * 
	 * @param ctx
	 *            The configuration context. Must provide 'image'.
	 * 
	 * @return The shop name.
	 */
	protected String getImage(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("image");
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
	 * Gets the alt text for the image
	 * 
	 * @param ctx
	 *            The configuration context. May provide 'alt'.
	 * 
	 * @return The sign title.
	 */
	private String getAlt(ConfigurableFactoryContext ctx) {
		return ctx.getString("alt", "");
	}


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
	public Object create(final ConfigurableFactoryContext ctx) {
		return new PopupImage(getImage(ctx), getTitle(ctx), getAlt(ctx));
	}

}
