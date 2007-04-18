/*
 * @(#) src/games/stendhal/server/entity/area/LifeDrainAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A base factory for <code>LifeDrainArea</code> objects.
 */
public class LifeDrainAreaFactory extends OccupantAreaFactory {

	//
	// LifeDrainAreaFactory
	//

	/**
	 * Extract the maximum damage amount from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The maximum damage amount.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected int getDamage(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("maximum-damage")) == null) {
return 0;
// For now, unused.. require later
//			throw new IllegalArgumentException("Required attribute 'maximum-damage' missing");
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'damage' attribute: " + s);
		}
	}


	//
	// OccupantAreaFactory
	//

	protected OccupantArea createArea(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new LifeDrainArea(getName(ctx), getWidth(ctx), getHeight(ctx), getDamage(ctx), getInterval(ctx));
	}
}
