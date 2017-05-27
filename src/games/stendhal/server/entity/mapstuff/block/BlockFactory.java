/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.block;

import java.util.Arrays;
import java.util.List;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * a factory for movable blocks
 *
 * @author hendrik
 */
public class BlockFactory implements ConfigurableFactory{

	@Override
	public Object create(ConfigurableFactoryContext ctx) {
		boolean multiPush = ctx.getBoolean("multi", true);
		final String description = ctx.getString("description", "");
		final String sounds = ctx.getString("sounds", null);
		final String shape = ctx.getString("shape", null);

		List<String> soundList = null;
		if(sounds!= null) {
			soundList = Arrays.asList(sounds.split(","));
		}

		String style = ctx.getString("style", null);
		Block block = new Block(multiPush, style, shape, soundList);

		if (description != null) {
			block.setDescription(description);
		}

		return block;
	}

}
