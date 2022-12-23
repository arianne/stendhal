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
package games.stendhal.server.maps.quests.a_grandfathers_wish;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.portal.AccessCheckingPortal;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AGrandfathersWish;


public class MylingWellPortal extends AccessCheckingPortal {

	private static final String QUEST_SLOT = AGrandfathersWish.QUEST_SLOT;
	private static MylingSpawner spawner;


	public MylingWellPortal() {
		super();
	}

	@Override
	protected boolean isAllowed(final RPEntity user) {
		if (!(user instanceof Player)) {
			return false;
		}

		final Player player = (Player) user;
		final String find_myling = player.getQuest(QUEST_SLOT, 1);
		if (find_myling == null || find_myling.equals("")) {
			rejectedMessage = "There is no reason to enter this well right"
				+ " now.";
			return false;
		}
		if (find_myling.equals("find_myling:done")) {
			return true;
		}
		if (!player.isEquipped("rope")) {
			rejectedMessage = "You need a rope to descend down this well.";
			if (!find_myling.equals("find_myling:well_rope")) {
				player.setQuest(QUEST_SLOT, 1, "find_myling:well_rope");
			}
			return false;
		}

		return true;
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (isAllowed(user) && user instanceof Player) {
			return usePortal((Player) user);
		}

		rejected(user);
		return false;
	}

	@Override
	protected boolean usePortal(final Player player) {
		final boolean ret = super.usePortal(player);

		if (!player.getQuest(QUEST_SLOT, 3).equals("cure_myling:done")) {
			if (spawner == null) {
				spawner = AGrandfathersWish.getMylingSpawner();
			}
			// make sure there is a myling in the well for player to see
			spawner.respawn();
		}

		if (!player.getQuest(QUEST_SLOT, 1).equals("find_myling:done")) {
			// rope is hung so player can use anytime now
			player.drop("rope");
			player.setQuest(QUEST_SLOT, 1, "find_myling:done");
			player.sendPrivateText("Is that thing Niall!? Poor boy. I"
				+ " need to tell Elias right away.");
		} else if (player.getQuest(QUEST_SLOT, 2).equals("")) {
			player.sendPrivateText("I need to tell Elias about Niall.");
		} else if (player.getQuest(QUEST_SLOT, 3).equals("cure_myling:start")) {
			if (player.isEquipped("ashen holy water")) {
				player.sendPrivateText("I should be able to use the holy"
					+ " water here.");
			} else {
				player.sendPrivateText("Where did I put that holy water? I"
					+ " should return to the priest to get some more.");
			}
		}

		return ret;
	}
}
