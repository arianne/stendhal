/***************************************************************************
 *                    Copyright © 2007-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>DamagingArea</code> objects.
 */
public class DamagingAreaFactory extends OccupantAreaFactory {

	/**
	 * Extract the damage amount from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The damage amount.
	 * @throws IllegalArgumentException
	 *             If the attribute is missing.
	 */
	protected int getDamage(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredInt("damage");
	}

	/**
	 * Extract the moving damage probability (as percent) from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The damage probability (0.0 - 1.0).
	 *
	 * @throws IllegalArgumentException
	 *             If the attribute is invalid.
	 */
	protected double getProbability(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("probability", 0) / 100.0;
	}

	//
	// OccupantAreaFactory
	//

	/**
	 * Creates the OccupantArea.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return An OccupantArea.
	 * @throws IllegalArgumentException
	 *             in case of an invalid configuration
	 */
	@Override
	protected OccupantArea createArea(final ConfigurableFactoryContext ctx) {
		return new DamagingArea(getWidth(ctx), getHeight(ctx),
				getInterval(ctx), getDamage(ctx), getProbability(ctx));
	}
}
