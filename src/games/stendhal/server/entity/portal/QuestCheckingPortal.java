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
package games.stendhal.server.entity.portal;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import marauroa.common.game.AttributeNotFoundException;

public class QuestCheckingPortal extends Portal implements UseListener {
	private String questslot = null;

	public QuestCheckingPortal(String questslot) throws AttributeNotFoundException {
		super();
		put("type", "portal");
		this.questslot = questslot;
	}

	public void onUsed(RPEntity user) {
		Player player = (Player) user;
		if (player.hasQuest(questslot)) {
			StendhalRPAction.usePortal(player, this);
		} else {
			player.sendPrivateText("Why should i go down there. It looks very dangerous.");
		}
		
	}
}
