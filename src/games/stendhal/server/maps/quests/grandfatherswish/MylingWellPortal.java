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

import static games.stendhal.server.maps.quests.GrandfathersWish.QUEST_SLOT;

import games.stendhal.server.entity.mapstuff.portal.AccessCheckingPortal;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;


public class MylingWellPortal extends AccessCheckingPortal {

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
		if (find_myling != null && find_myling.equals("find_myling:done")) {
			return true;
		}
		if (find_myling == null || !find_myling.equals("find_myling:start")) {
			rejectedMessage = "There is something strange about this well.";
			return false;
		}
		if (!player.isEquipped("rope")) {
			rejectedMessage = "You need a rope to descend down this well.";
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

		if (player.getQuest(QUEST_SLOT, 1).equals("find_myling:start")) {
			// rope is hung so player can use anytime now
			player.drop("rope");
			player.setQuest(QUEST_SLOT, 1, "find_myling:done");
			player.sendPrivateText("Is that thing Niall!? Poor boy. I"
				+ " need to tell Elias right away.");
		} else if (player.getQuest(QUEST_SLOT, 3).equals("cure_myling:start")
				&& player.isEquipped("ashen holy water")) {
			player.sendPrivateText("I should be able to use the holy"
				+ " water here.");
		}

		return ret;
	}
}
