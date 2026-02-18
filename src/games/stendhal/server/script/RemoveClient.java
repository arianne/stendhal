/***************************************************************************
 *                 Copyright © 2015-2024 - Faiumoni e. V.                  *
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

import games.stendhal.server.core.scripting.impl.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * removes an entry from the PlayerContainer with the specified clientid
 *
 * @author hendrik
 */
public class RemoveClient extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("Usage: /script RemoveClient.class <clientid>");
		}

		PlayerEntry playerEntry = PlayerEntryContainer.getContainer().get(Integer.parseInt(args.get(0)));
		admin.sendPrivateText("playerEntry: " + playerEntry);

		if (playerEntry != null) {
			PlayerEntryContainer.getContainer().remove(Integer.parseInt(args.get(0)));
		}
	}

}
