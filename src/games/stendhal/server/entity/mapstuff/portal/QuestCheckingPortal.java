/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

public class QuestCheckingPortal extends AccessCheckingPortal {
	private String questslot;

	public QuestCheckingPortal(String questslot) {
		this(questslot, "Why should i go down there?. It looks very dangerous.");
	}

	public QuestCheckingPortal(String questslot, String rejectMessage) {
		super(rejectMessage);

		this.questslot = questslot;
	}

	//
	// AccessCheckingPortal
	//

	/**
	 * Determine if this portal can be used.
	 * 
	 * @param user
	 *            The user to be checked.
	 * 
	 * @return <code>true<code> if the user can use the portal.
	 */
	@Override
	protected boolean isAllowed(RPEntity user) {
		if (user instanceof Player) {
			return ((Player) user).hasQuest(questslot);
		} else {
			return false;
		}
	}
}
