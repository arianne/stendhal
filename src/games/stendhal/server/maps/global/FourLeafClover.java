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
package games.stendhal.server.maps.global;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.CachedActionManager;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.CloverSpawner;


public class FourLeafClover implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		// don't add to world until server maps are loaded
		CachedActionManager.get().register(new Runnable() {
			@Override
			public void run() {
				CloverSpawner.get().init();
			}
		});
	}
}
