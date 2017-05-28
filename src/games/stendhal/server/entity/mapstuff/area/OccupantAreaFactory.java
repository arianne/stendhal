/*
 * @(#) src/games/stendhal/server/entity/area/OccupantAreaFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>OccupantArea</code> objects.
 */
public abstract class OccupantAreaFactory implements ConfigurableFactory {
	/**
	 * Creates the OccupantArea.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return An OccupantArea.
	 * @throws IllegalArgumentException
	 *             in case of an invalid configuration
	 */
	protected abstract OccupantArea createArea(ConfigurableFactoryContext ctx);

	/**
	 * Extract the width from context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The width, 1 if unspecified.
	 * @throws IllegalArgumentException
	 *             If the attribute is invalid.
	 */
	protected int getWidth(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("width", 1);
	}

	/**
	 * Extract the height from context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The height, 1 if unspecified.
	 * @throws IllegalArgumentException
	 *             If the attribute is invalid.
	 */
	protected int getHeight(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("height", 1);
	}

	/**
	 * Extract the interval (in seconds) to perform actions while stationary.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The stationary interval (in turns).
	 * @throws IllegalArgumentException
	 *             if the attribute is missing.
	 */
	protected int getInterval(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredInt("interval");
	}

	/**
	 * Extract the flag to only affect players.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The flag to only affect players.
	 * @throws IllegalArgumentException
	 *             If the attribute is invalid.
	 */
	protected boolean getPlayersOnly(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredBoolean("players-only");
	}

	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		OccupantArea area;

		area = createArea(ctx);
		area.setPlayersOnly(getPlayersOnly(ctx));

		return area;
	}
}
