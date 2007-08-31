/*
 * @(#) src/games/stendhal/server/entity/area/LifeDrainAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>LifeDrainArea</code> objects.
 */
public class LifeDrainAreaFactory extends OccupantAreaFactory {

	/**
	 * Extract the maximum damage amount from a context.
	 *
	 * @param	ctx		The configuration context.
	 * @return	The maximum damage amount.
	 * @throws	IllegalArgumentException If the attribute is missing.
	 */
	protected int getDamage(ConfigurableFactoryContext ctx) {
		return ctx.getInt("maximum-damage", 0);
	}

	@Override
	protected OccupantArea createArea(ConfigurableFactoryContext ctx) {
		return new LifeDrainArea(getWidth(ctx), getHeight(ctx), getDamage(ctx), getInterval(ctx));
	}
}
