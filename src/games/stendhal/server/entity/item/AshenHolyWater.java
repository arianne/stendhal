/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.maps.quests.AnOldMansWish;


/**
 * A special item for An Old Man's Wish quest.
 */
public class AshenHolyWater extends Item {
	public AshenHolyWater(final String name, final String clazz,
			final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public AshenHolyWater(final AshenHolyWater hw) {
		super(hw);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user instanceof Player) {
			if (checkZone(user)) {
				if (checkMylingInWorld()) {
					// DEBUG:
					((Player) user).sendPrivateText("Used holy water.");

					removeOne();
				} else {
					// DEBUG:
					((Player) user).sendPrivateText("There is no myling here");
				}
			} else {
				// DEBUG:
				((Player) user).sendPrivateText("You cannot use this here.");
			}
		}

		return true;
	}

	/**
	 * Checks if the player is currently in the burrow.
	 */
	private boolean checkZone(final RPEntity user) {
		return user.getZone().equals(SingletonRepository.getRPWorld().getZone("-1_cemetery_burrow"));
	}

	private boolean checkMylingInWorld() {
		final AnOldMansWish.MylingSpawner spawner = AnOldMansWish.getMylingSpawner();
		return spawner != null && spawner.mylingIsActive();
	}
}
