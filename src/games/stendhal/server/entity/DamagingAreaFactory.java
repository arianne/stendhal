/*
 * @(#) src/games/stendhal/server/entity/DamagingAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;
import games.stendhal.server.StendhalRPWorld;

/**
 * A base factory for <code>DamagingArea</code> objects.
 */
public class DamagingAreaFactory implements ConfigurableFactory {
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
	protected int getDamage(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("damage")) == null) {
			throw new IllegalArgumentException(
				"Required attribute 'damage' missing");
		}

		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(
				"Invalid 'damage' attribute: " + s);
		}
	}


	/**
	 * Extract the name of the damaging entity.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The entity's name.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected String getName(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("name")) == null) {
			throw new IllegalArgumentException(
				"Required attribute 'name' missing");
		}

		return s;
	}


	/**
	 * Extract the interval (in seconds) to inflict damage while
	 * stationary.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The stationary interval (in turns).
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected int getInterval(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("interval")) == null) {
			throw new IllegalArgumentException(
				"Required attribute 'interval' missing");
		}

		try {
			return StendhalRPWorld.get().getTurnsInSeconds(
				Integer.parseInt(s));
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(
				"Invalid 'interval' attribute: " + s);
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
	protected double getProbability(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("probability")) == null)
			return 0.0;

		try {
			return Integer.parseInt(s) / 100.0;
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(
				"Invalid 'probability' attribute: " + s);
		}
	}


	//
	// ConfigurableFactory
	//

	/**
	 * Create a damaging area.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A DamagingArea.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		DamagingArea
	 */
	public Object create(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		return new DamagingArea(
			getName(ctx),
			getDamage(ctx),
			getInterval(ctx),
			getProbability(ctx));
	}
}
