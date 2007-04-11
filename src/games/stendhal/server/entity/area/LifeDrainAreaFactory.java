/*
 * @(#) src/games/stendhal/server/entity/area/LifeDrainAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;
import games.stendhal.server.StendhalRPWorld;

/**
 * A base factory for <code>LifeDrainArea</code> objects.
 */
public class LifeDrainAreaFactory implements ConfigurableFactory {

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
			throw new IllegalArgumentException("Required attribute 'maximum-damage' missing");
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'damage' attribute: " + s);
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
	protected String getName(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("name")) == null) {
			throw new IllegalArgumentException("Required attribute 'name' missing");
		}

		return s;
	}

	/**
	 * Extract the width from context
	 *
	 * @param	ctx		The configuration context.
	 * @return	width (1 if unspecified)
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getWidth(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("width")) == null) {
			return 1;
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'width' attribute: " + s);
		}
	}

	/**
	 * Extract the height from context
	 *
	 * @param	ctx		The configuration context.
	 * @return	height (1 if unspecified)
	 * @throws	IllegalArgumentException If the attribute is invalid.
	 */
	protected int getHeight(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("height")) == null) {
			return 1;
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'height' attribute: " + s);
		}
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
	protected int getInterval(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s = ctx.getAttribute("interval");

		if (s == null) {
			throw new IllegalArgumentException("Required attribute 'interval' missing");
		}

		try {
			return StendhalRPWorld.get().getTurnsInSeconds(Integer.parseInt(s));
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'interval' attribute: " + s);
		}
	}

	/**
	 * Extract the flag to only damage players.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The flag to only damage players.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is invalid.
	 */
	protected boolean getPlayersOnly(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("players-only")) == null) {
			return false;
		}

		if (s.equals("true")) {
			return true;
		}

		if (s.equals("false")) {
			return false;
		}

		throw new IllegalArgumentException("Invalid 'players-only' attribute: " + s);
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
	// ConfigurableFactory
	//

	/**
	 * Create a damaging area.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A LifeDrainArea.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		LifeDrainArea
	 */
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		LifeDrainArea area;

		area = new LifeDrainArea(getName(ctx), getWidth(ctx), getHeight(ctx), getDamage(ctx), getInterval(ctx), getProbability(ctx));

		area.setPlayersOnly(getPlayersOnly(ctx));

		return area;
	}
}
