/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.listener;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.core.events.LogoutListener;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.PlayerLoggedOutEvent;

/**
 * UpdatePlayerOnlineLogoutListener is responsible for sending PlayerLoggedOutEvents on Logout
 *
 * @author markus
 */
public class UpdatePlayerOnlineLogoutListener implements LogoutListener{

	@Override
	public void onLoggedOut(Player player) {
		SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
			@Override
			public void execute(final Player player) {
				player.addEvent(new PlayerLoggedOutEvent(player.getName()));
				player.notifyWorldAboutChanges();
			}
		});
	}



}
