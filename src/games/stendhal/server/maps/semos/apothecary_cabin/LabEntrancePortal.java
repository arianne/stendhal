/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.apothecary_cabin;

import java.util.Map;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;

public class LabEntrancePortal implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final Portal labEntrance = new Portal() {
			@Override
			public boolean onUsed(final RPEntity user) {
				if (isAllowed(user)) {
					return super.onUsed(user);
				}

				// show a hint if player cannot enter lab
				if (user instanceof Player) {
					((Player) user).sendPrivateText(NotificationType.WARNING, "You feel a cool breeze coming in from somewhere.");
				}

				return false;
			}

			private boolean isAllowed(final RPEntity user) {
				if (user instanceof Player) {
					final Player player = (Player) user;

					// can enter if Antivenom Ring quest has been started or player has note to apothecary item
					if (player.getQuest("antivenom_ring") != null || player.isEquippedWithInfostring("note", "note to apothecary")) {
						return true;
					}
				}

				return false;
			}
		};

		labEntrance.setPosition(3, 13);
		labEntrance.setResistance(0);
		labEntrance.setHidden(true);
		labEntrance.setIdentifier("lab_entrance");
		labEntrance.setDestination("int_apothecary_lab", "lab_exit");

		zone.add(labEntrance);
	}
}
