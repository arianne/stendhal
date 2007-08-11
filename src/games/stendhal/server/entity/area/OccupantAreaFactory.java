/*
 * @(#) src/games/stendhal/server/entity/area/OccupantAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>OccupantArea</code> objects.
 */
public abstract class OccupantAreaFactory implements ConfigurableFactory {

	//
	// OccupantAreaFactory
	//

	protected abstract OccupantArea createArea(ConfigurableFactoryContext ctx) throws IllegalArgumentException;


	/**
	 * Extract the name of the area entity.
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
	 *
	 * @return	The width, 1 if unspecified.
	 *
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
	 *
	 * @return	The height, 1 if unspecified.
	 *
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
	 * Extract the interval (in seconds) to perform actions while
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
	 * Extract the flag to only affect players.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The flag to only affect players.
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


	//
	// ConfigurableFactory
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
	 *
	 * @see		OccupantArea
	 */
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		OccupantArea area;

		area = createArea(ctx);
		area.setPlayersOnly(getPlayersOnly(ctx));

		return area;
	}
}
