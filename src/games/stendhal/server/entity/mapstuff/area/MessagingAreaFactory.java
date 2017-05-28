/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

public class MessagingAreaFactory implements ConfigurableFactory {
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		final MessagingArea area;

		area = new MessagingArea(coversZone(ctx), getWidth(ctx), getHeight(ctx),
				getEnterMessage(ctx), getLeaveMessage(ctx), warnPlayer(ctx));

		return area;
	}

	private int getWidth(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("width", 1);
	}

	private int getHeight(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("height", 1);
	}

	private String getEnterMessage(final ConfigurableFactoryContext ctx) {
		return ctx.getString("enterMessage", null);
	}

	private String getLeaveMessage(final ConfigurableFactoryContext ctx) {
		return ctx.getString("leaveMessage", null);
	}

	private boolean coversZone(final ConfigurableFactoryContext ctx) {
		return ctx.getBoolean("coversZone", false);
	}

	private boolean warnPlayer(final ConfigurableFactoryContext ctx) {
		return ctx.getBoolean("warnPlayer", false);
	}
}
