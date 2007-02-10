/*
 * @(#) src/games/stendhal/server/entity/SignFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A base factory for <code>Sign</code> objects.
 */
public class SignFactory implements ConfigurableFactory {
	//
	// SignFactory
	//

	/**
	 * Extract the message text from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The message text.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected String getText(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("text")) == null) {
			throw new IllegalArgumentException(
				"Required attribute 'text' missing");
		}

		return s;
	}


	//
	// ConfigurableFactory
	//

	/**
	 * Create a damaging area.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A Sign.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		Sign
	 */
	public Object create(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		Sign	sign;


		sign = new Sign();
		sign.setText(getText(ctx));

		return sign;
	}
}
