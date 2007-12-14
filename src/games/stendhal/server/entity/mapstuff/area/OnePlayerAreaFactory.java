/*
 * @(#) src/games/stendhal/server/entity/OnePlayerAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>OnePlayerArea</code> objects.
 */
public class OnePlayerAreaFactory implements ConfigurableFactory {

	/**
	 * Extract the height from context
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The height, 1 if unspecified.
	 */
	protected int getHeight(ConfigurableFactoryContext ctx) {
		return ctx.getInt("height", 1);
	}

	/**
	 * Extract the width from context
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The width, 1 if unspecified.
	 */
	protected int getWidth(ConfigurableFactoryContext ctx) {
		return ctx.getInt("width", 1);
	}

	/**
	 * Create a damaging area.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return A OnePlayerArea.
	 * 
	 * @see OnePlayerArea
	 */
	public Object create(ConfigurableFactoryContext ctx) {
		return new OnePlayerArea(getWidth(ctx), getHeight(ctx));
	}
}
