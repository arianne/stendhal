/***************************************************************************
 *                (C) Copyright 2003-2012 - Faiumoni e. V.                 *
 ***************************************************************************
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
 * A base factory for <code>NoLoginArea</code> objects.
 */
public class NoLoginAreaFactory implements ConfigurableFactory {

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
	 * Get the message to send to the player.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The message to send to the player.
	 */
	protected String getMessage(final ConfigurableFactoryContext ctx) {
		return ctx.getString("message", null);
	}

	/**
	 * Get the new player X coordinate.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The new player X coordinate.
	 *
	 * @throws IllegalArgumentException
	 *             If the attribute is missing/invalid.
	 */
	protected int getNewX(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredInt("new-x");
	}

	/**
	 * Get the new player Y coordinate.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The new player Y coordinate.
	 *
	 * @throws IllegalArgumentException
	 *             If the attribute is missing/invalid.
	 */
	protected int getNewY(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredInt("new-y");
	}

	/**
	 * Create an object.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return A new object, or <code>null</code> if allowed by the factory
	 *         type.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 */
	@Override
	public AreaEntity create(final ConfigurableFactoryContext ctx) {
		AreaEntity area = new AreaEntity(getWidth(ctx), getHeight(ctx));
		NoLoginAreaBehaviour noLoginAreaBehaviour = new NoLoginAreaBehaviour(getNewX(ctx), getNewY(ctx), getMessage(ctx));
		area.addBehaviour(noLoginAreaBehaviour);
		area.addBehaviour(new HideAreaBehaviour());
		return area;
	}
}
