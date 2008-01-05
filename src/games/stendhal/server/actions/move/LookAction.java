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
package games.stendhal.server.actions.move;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

public class LookAction implements ActionListener {

	private static final String ATTR_NAME = "name";
	private static final String ATTR_TYPE = "type";
	private static final String _TARGET = "target";
	private static final String _LOOK = "look";

	public static void register() {
		CommandCenter.register(_LOOK, new LookAction());
	}

	public void onAction(Player player, RPAction action) {
		Entity entity = EntityHelper.entityFromSlot(player, action);
		if (entity == null) {
			entity = EntityHelper.entityFromTargetName(action.get(_TARGET), player.getZone());
		}

		if (entity != null) {
			String name = entity.get(ATTR_TYPE);
			if (entity.has(ATTR_NAME)) {
				name = entity.get(ATTR_NAME);
			}
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					_LOOK, name);
			String text = entity.describe();
			if (entity instanceof Sign) {
				player.sendPrivateText(NotificationType.RESPONSE, text);
			} else {
				player.sendPrivateText(text);
			}
			player.notifyWorldAboutChanges();
		}
	}
}
