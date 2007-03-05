/*
 * @(#) src/games/stendhal/server/entity/BlackboardFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A base factory for <code>Blackboard</code> objects.
 */
public class BlackboardFactory extends SignFactory {
	//
	// BlackboardFactory
	//

	/**
	 * Extract the writable state from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The writable state.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is missing.
	 */
	protected boolean getWritable(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("writable")) == null) {
			return false;
		}

		if(s.equals("true")) {
			return true;
		}

		if(s.equals("false")) {
			return false;
		}

		throw new IllegalArgumentException(
			"Invalid attribute 'writable': " + s);
	}


	//
	// ConfigurableFactory
	//

	/**
	 * Create a damaging area.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A Blackboard.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		Blackboard
	 */
	public Object create(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		Blackboard	blackboard;


		blackboard = new Blackboard(getWritable(ctx));
		blackboard.setText(getText(ctx));

		return blackboard;
	}
}
