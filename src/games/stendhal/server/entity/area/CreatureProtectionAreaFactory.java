/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import java.util.StringTokenizer;

import games.stendhal.common.ConfigurableFactory;
import games.stendhal.common.ConfigurableFactoryContext;
import games.stendhal.server.StendhalRPWorld;

/**
 * A base factory for <code>CreatureProtectionArea</code> objects.
 */
public class CreatureProtectionAreaFactory implements ConfigurableFactory {
	//
	// CreatureProtectionAreaFactory
	//

	/**
	 * Configure an area's criteria rules.
	 *
	 * @param	area		The area to configure.
	 * @param	ctx		The configuration context.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is invalid.
	 */
	protected void defineCreatures(CreatureProtectionArea area,
	 ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String		s;
		String		clazz;
		String		subclazz;
		boolean		blocked;
		StringTokenizer	st;
		int		i;


		if((s = ctx.getAttribute("rules")) == null)
			return;

		blocked = true;
		st = new StringTokenizer(s, " \n\t,");

		while(st.hasMoreTokens()) {
			s = st.nextToken();

			/*
			 * Possible with "   "?
			 */
			if(s.length() == 0)
				continue;

			/*
			 * blocking modifier?
			 */
			if(s.charAt(0) == '-') {
				blocked = true;
				s = s.substring(1);
			} else if(s.charAt(0) == '+') {
				blocked = false;
				s = s.substring(1);
			}


			/*
			 * <class>
			 * <class>:<subclass>
			 */
			if((i = s.indexOf(':')) != -1) {
				clazz = s.substring(0, i);
				subclazz = s.substring(i + 1);

				if((subclazz.length() == 0)
				 || subclazz.equals("*")) {
					subclazz = null;
				}
			} else {
				clazz = s;
				subclazz = null;
			}

			if((clazz.length() == 0) || clazz.equals("*"))
				clazz = null;

			area.add(clazz, subclazz, blocked);
		}
	}


	/**
	 * Extract the default action from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The default action.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is invalid.
	 */
	protected boolean getBlockedDefault(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("default")) == null)
			return true;

		if(s.equals("block"))
			return true;

		if(s.equals("allow"))
			return false;

		throw new IllegalArgumentException(
			"Invalid 'default' attribute: " + s);
	}

	/**
	 * Extract the area height from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The height.
	 *
	 * @throws	IllegalArgumentException
	 *				If the attribute is invalid.
	 */
	protected int getHeight(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("height")) == null)
			return 1;

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
	 *				If the attribute is invalid.
	 */
	protected int getWidth(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		String	s;


		if((s = ctx.getAttribute("width")) == null)
			return 1;

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
	 * @return	A CreatureProtectionArea.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		CreatureProtectionArea
	 */
	public Object create(ConfigurableFactoryContext ctx)
	 throws IllegalArgumentException {
		CreatureProtectionArea	area;


		area =  new CreatureProtectionArea(
			getWidth(ctx), getHeight(ctx), getBlockedDefault(ctx));

		defineCreatures(area, ctx);

		return area;
	}
}
