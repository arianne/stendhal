/*
 * @(#) src/games/stendhal/server/entity/portal/KeyedPortalFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A factory for <code>KeyedPortal</code> objects.
 */
public class KeyedPortalFactory implements ConfigurableFactory {
	//
	// KeyedPortalFactory
	//

	/**
	 * Extract the portal key from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The key name.
	 *
	 * @throws	IllegalArgumentException
	 *				If the class attribute is missing.
	 */
	protected String getKey(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("key")) == null) {
			throw new IllegalArgumentException(
				"Required attribute 'key' missing");
		}

		return s;
	}


	/**
	 * Extract the portal key quantity from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The required key quantity.
	 *
	 * @throws	IllegalArgumentException
	 *				If the class attribute is missing.
	 */
	protected int getQuantity(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("quantity")) == null) {
			return 1;
		}

		try  {
			return Integer.parseInt(s);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(
				"Invalid 'quantity' attribute: " + s);
		}
	}


	/**
	 * Extract the rejected message from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The rejected message, or <code>null</code> if none.
	 *
	 * @throws	IllegalArgumentException
	 *				If the class attribute is missing.
	 */
	protected String getRejectedMessage(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		return ctx.getAttribute("rejected");
	}



	//
	// ConfigurableFactory
	//

	/**
	 * Create a keyed portal.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A KeyedPortal.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		KeyedPortal
	 */
	public Object create(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		return new KeyedPortal(
			getKey(ctx),
			getQuantity(ctx),
			getRejectedMessage(ctx));
	}
}
