/*
 * @(#) src/games/stendhal/server/entity/OnePlayerAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A base factory for <code>OnePlayerArea</code> objects.
 */
public class OnePlayerAreaFactory implements ConfigurableFactory {
	//
	// OnePlayerAreaFactory
	//

	/**
	 * Extract the area height from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The height.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected int getHeight(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("height")) == null) {
			throw new IllegalArgumentException(
				"Required attribute 'height' missing");
		}

		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(
				"Invalid 'height' attribute: " + s);
		}
	}


	/**
	 * Extract the area width from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The width.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected int getWidth(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("width")) == null) {
			throw new IllegalArgumentException(
				"Required attribute 'width' missing");
		}

		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(
				"Invalid 'width' attribute: " + s);
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
	 * @return	A OnePlayerArea.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		OnePlayerArea
	 */
	public Object create(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		return new OnePlayerArea(getWidth(ctx), getHeight(ctx));
	}
}
