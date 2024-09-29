/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config.zone;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.DynamicCreatureSpawner;


/**
 * Parses info for dynamically spawned creatures.
 */
public class CreatureSpawnsXMLReader extends SetupXMLReader {

	private static Logger logger = Logger.getLogger(CreatureSpawnsXMLReader.class);


	@Override
	public SetupDescriptor read(final Element element) {
		final CreatureSpawnsDescriptor desc = new CreatureSpawnsDescriptor();
		readParameters(desc, element);
		return desc;
	}

	private static class CreatureSpawnsDescriptor extends SetupDescriptor {
		@Override
		public void setup(final StendhalRPZone zone) {
			final DynamicCreatureSpawner spawner = new DynamicCreatureSpawner(zone);
			final Map<String, String> params = getParameters();
			for (final String name: params.keySet()) {
				final int max = MathHelper.parseIntDefault(params.get(name), 0);
				if (max < 0) {
					logger.warn("Max must be a positive integer, not registering dynamic spawn for creature \"" + name + "\"");
					continue;
				}
				spawner.register(name, max);
			}
			// NOTE: does zone need to know about dynamic spawner?
			zone.setDynamicSpawner(spawner);
		}
	}
}
