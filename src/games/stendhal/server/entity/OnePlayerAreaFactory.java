/*
 * @(#) src/games/stendhal/server/entity/OnePlayerAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>OnePlayerArea</code> objects.
 */
public class OnePlayerAreaFactory implements ConfigurableFactory {

	/**
	 * Extract the height from context
	 *
	 * @param	ctx		The configuration context.
	 * @return	The height, 1 if unspecified.
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getHeight(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return ctx.getInt("height", 1);
	}
	
	/**
	 * Extract the width from context
	 *
	 * @param	ctx		The configuration context.
	 * @return	The width, 1 if unspecified.
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getWidth(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
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
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value sutable for meaningful user
	 *             interpretation.
	 * 
	 * @see OnePlayerArea
	 */
	public Object create(ConfigurableFactoryContext ctx)
			throws IllegalArgumentException {
		return new OnePlayerArea(getWidth(ctx), getHeight(ctx));
	}
}
