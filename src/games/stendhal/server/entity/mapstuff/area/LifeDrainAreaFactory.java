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
 * A base factory for <code>LifeDrainArea</code> objects.
 */
public class LifeDrainAreaFactory extends OccupantAreaFactory {
	/**
	 * Extract the damage ratio from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The damage ratio (or 10% is unset).
	 */
	protected double getDamageRatio(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("damage-ratio", 10) / 100.0;
	}

	/**
	 * Extract the minimum damage amount from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The minimum damage amount (or 10 is unset).
	 */
	protected int getMimimumDamage(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("minimum-damage", 10);
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
		return new LifeDrainArea(getWidth(ctx), getHeight(ctx),
				getInterval(ctx), getDamageRatio(ctx), getMimimumDamage(ctx));
	}
}
