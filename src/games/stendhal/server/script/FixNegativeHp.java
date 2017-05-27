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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LoginNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * fixed negative hp
 *
 * @author hendrik
 */
public class FixNegativeHp extends ScriptImpl implements LoginListener {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);

		LoginNotifier.get().addListener(this);
	}

	@Override
	public void onLoggedIn(Player player) {
		if (player.getHP() <= 0) {
			SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper", "set hp of player " + player.getName() + " from " + player.getHP() + " to 1.");
			player.setHP(1);
		}
	}


}
