/***************************************************************************
 *                    Copyright © 2011-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.ExpirationTracker;
import marauroa.common.game.RPObject;

public class ExpirationTrackerTransformer implements Transformer {
	@Override
	public RPObject transform(RPObject object) {
		ExpirationTracker entity = new ExpirationTracker();

		entity.setPosition(object.getInt("x"), object.getInt("y"));
		entity.setIdentifier(object.get("identifier"));
		entity.setPlayerName(object.get("player_name"));
		entity.setExpirationTime(Long.parseLong(object.get("expires")));

		return entity;
	}
}
