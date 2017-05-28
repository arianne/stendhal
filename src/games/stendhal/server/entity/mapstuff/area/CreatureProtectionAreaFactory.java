/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import java.util.StringTokenizer;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

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
	 * @param area
	 *            The area to configure.
	 * @param ctx
	 *            The configuration context.
	 *
	 * @throws IllegalArgumentException
	 *             If the attribute is invalid.
	 */
	protected void defineCreatures(final CreatureProtectionArea area,
			final ConfigurableFactoryContext ctx) {
		String s = ctx.getString("rules", null);
		String clazz;
		String subclazz;
		boolean blocked;
		StringTokenizer st;
		int i;

		if (s == null) {
			return;
		}

		blocked = true;
		st = new StringTokenizer(s, " \n\t,");

		while (st.hasMoreTokens()) {
			s = st.nextToken();

			/*
			 * Possible with " "?
			 */
			if (s.length() == 0) {
				continue;
			}

			/*
			 * blocking modifier?
			 */
			if (s.charAt(0) == '-') {
				blocked = true;
				s = s.substring(1);
			} else if (s.charAt(0) == '+') {
				blocked = false;
				s = s.substring(1);
			}

			/*
			 * <class> <class>:<subclass>
			 */
			i = s.indexOf(':');
			if (i != -1) {
				clazz = s.substring(0, i);
				subclazz = s.substring(i + 1);

				if ((subclazz.length() == 0) || "*".equals(subclazz)) {
					subclazz = null;
				}
			} else {
				clazz = s;
				subclazz = null;
			}

			if ((clazz.length() == 0) || "*".equals(clazz)) {
				clazz = null;
			}

			area.add(clazz, subclazz, blocked);
		}
	}

	/**
	 * Extract the default action from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The default action.
	 *
	 * @throws IllegalArgumentException
	 *             If the attribute is other than "block" or "allow".
	 */
	protected boolean getBlockedDefault(final ConfigurableFactoryContext ctx) {
		final String s = ctx.getString("default", null);

		if (s == null) {
			return true;
		}

		if ("block".equals(s)) {
			return true;
		}

		if ("allow".equals(s)) {
			return false;
		}

		throw new IllegalArgumentException("Invalid 'default' attribute: " + s);
	}

	/**
	 * Extract the area height from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The height.
	 * @throws IllegalArgumentException
	 *             If the attribute is invalid.
	 */
	protected int getHeight(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("height", 1);
	}

	/**
	 * Extract the area width from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The width.
	 * @throws IllegalArgumentException
	 *             If the attribute is invalid.
	 */
	protected int getWidth(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("width", 1);
	}

	/**
	 * Create a damaging area.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return A CreatureProtectionArea.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 *
	 * @see CreatureProtectionArea
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		CreatureProtectionArea area;

		area = new CreatureProtectionArea(getWidth(ctx), getHeight(ctx),
				getBlockedDefault(ctx));

		defineCreatures(area, ctx);

		return area;
	}
}
