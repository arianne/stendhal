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
package games.stendhal.server.entity.mapstuff.useable;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * Factory for ViewChangeEntities
 */
public class ViewChangeEntityFactory implements ConfigurableFactory {
	@Override
	public Object create(ConfigurableFactoryContext ctx) {
		int x = ctx.getRequiredInt("x");
		int y = ctx.getRequiredInt("y");
		ViewChangeEntity entity = new ViewChangeEntity(x, y);

		return entity;
	}
}
