/*
 * @(#) src/games/stendhal/server/entity/ShopSignFactory.java
 *
 * $Id$
 */

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
	public Object create(final ConfigurableFactoryContext ctx) {
		return new ShopSign(getShop(ctx), getTitle(ctx));
	}
}
