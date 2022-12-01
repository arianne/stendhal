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
package games.stendhal.server.maps.quests.grandfatherswish;

import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.maps.quests.GrandfathersWish;
import games.stendhal.server.maps.quests.grandfatherswish.MylingSpawner;


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
			final Player player = (Player) user;

			if (checkZone(user)) {
				final MylingSpawner spawner = GrandfathersWish.getMylingSpawner();

				if (checkMylingInWorld(spawner)) {
					player.sendPrivateText("You sprinkle the holy water over the"
						+ " myling's head.");

					removeOne();
					spawner.onMylingCured(player);
				} else {
					player.sendPrivateText("There is no myling here. Maybe if I"
						+ " wait one will show up.");
				}
			} else {
				player.sendPrivateText("There is nothing here that this can be"
					+ " used on.");
			}
		}

		return true;
	}

	/**
	 * Checks if the player is currently in the burrow.
	 */
	private boolean checkZone(final RPEntity user) {
		return user.getZone().equals(SingletonRepository.getRPWorld().getZone("-1_myling_well"));
	}

	private boolean checkMylingInWorld(final MylingSpawner spawner) {
		return spawner != null && spawner.mylingIsActive();
	}
}
