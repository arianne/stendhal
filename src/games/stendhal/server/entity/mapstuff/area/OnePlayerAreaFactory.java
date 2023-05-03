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

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>OnePlayerArea</code> objects.
 */
public class OnePlayerAreaFactory implements ConfigurableFactory {

	/**
	 * Extracts the height from context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The height, 1 if unspecified.
	 */
	protected int getHeight(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("height", 1);
	}

	/**
	 * Extracts the width from context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The width, 1 if unspecified.
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
	 * @return A OnePlayerArea.
	 *
	 * @see OnePlayerArea
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		return new OnePlayerArea(getWidth(ctx), getHeight(ctx));
	}
}
