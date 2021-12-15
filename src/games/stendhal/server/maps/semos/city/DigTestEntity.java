/***************************************************************************
 *                     (C) Copyright 2021 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.city;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.area.DigArea;
import games.stendhal.server.entity.player.Player;


/**
 * An example of how to implement ToolUseArea class
 */
public class DigTestEntity implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final DigArea dig_area = new DigArea() {
			@Override
			protected boolean onUsed(final Player user) {
				user.equipOrPutOnGround(SingletonRepository.getEntityManager().getItem("vomit"));
				user.sendPrivateText("Ewwww! You found some vomit.");

				return true;
			}
		};

		dig_area.setPosition(Integer.parseInt(attributes.get("x")), Integer.parseInt(attributes.get("y")));
		zone.add(dig_area);
	}
}
