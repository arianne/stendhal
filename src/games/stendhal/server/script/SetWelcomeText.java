/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * sets the welcome text players see on login.
 *
 * @author hendrik
 */
public class SetWelcomeText extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.isEmpty()) {
			admin.sendPrivateText(NotificationType.ERROR, "Argument missing.");
			return;
		}

		if (args.size() > 1) {
			admin.sendPrivateText(NotificationType.ERROR, "Too many arguments. Please use quotes.");
			return;
		}

		StendhalRPRuleProcessor.setWelcomeMessage(args.get(0));
		admin.sendPrivateText("Set welcome text to: " + args.get(0));
	}

}
