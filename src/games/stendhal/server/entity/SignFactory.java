/*
 * @(#) src/games/stendhal/server/entity/SignFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.server.config.factory.ConfigurableFactory;
import games.stendhal.server.config.factory.ConfigurableFactoryContext;

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
	 * @param ctx The configuration context. Must provide 'text'
	 * @return The message text.
	 */
	protected String getText(ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("text");
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a damaging area.
	 *
	 * @param ctx
	 *            Configuration context. must provide 'text'
	 *
	 * @return A Sign.
	 *
	 * @see Sign
	 */
	public Object create(ConfigurableFactoryContext ctx) {
		Sign sign = new Sign();

		sign.setText(getText(ctx));
		String clazz = ctx.getString("class", null);
		if (clazz != null) {
			sign.put("class", clazz);
		}

		return sign;
	}
}
