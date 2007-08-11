/*
 * @(#) src/games/stendhal/server/entity/area/DamagingAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>DamagingArea</code> objects.
 */
public class DamagingAreaFactory extends OccupantAreaFactory {
	//
	// DamagingAreaFactory
	//

	/**
	 * Extract the damage amount from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The damage amount.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected int getDamage(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("damage")) == null) {
			throw new IllegalArgumentException("Required attribute 'damage' missing");
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'damage' attribute: " + s);
		}
	}

	/**
	 * Extract the moving damage probability (as percent) from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The damage probability (0.0 - 1.0).
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is invalid.
	 */
	protected double getProbability(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("probability")) == null) {
			return 0.0;
		}

		try {
			return Integer.parseInt(s) / 100.0;
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'probability' attribute: " + s);
		}
	}

	//
	// OccupantAreaFactory
	//

	/**
	 * Create an occupant area.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	An OccupantArea.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 */
	@Override
	protected OccupantArea createArea(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new DamagingArea(getName(ctx), getWidth(ctx), getHeight(ctx), getDamage(ctx), getInterval(ctx), getProbability(ctx));
	}
}
