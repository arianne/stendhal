/***************************************************************************
 *                 Copyright © 2011-2024 - Faiumoni e. V.                  *
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
import games.stendhal.server.core.scripting.impl.ScriptImpl;
import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.player.Player;

/**
 * Script to enable a feature like keyring for a player
 *
 * @author madmetzger
 */
public class EnableFeature extends ScriptImpl {
	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args == null || args.size() < 2 || args.size() > 3) {
			admin.sendPrivateText("Usage of EnableFeature: <player> <feature> [<value>]");
			return;
		}
		final String feature = args.get(1);
		final String name = args.get(0);
		String value = "";
		if (args.size() > 2) {
			value = args.get(2);
		}
		final Player player = SingletonRepository.getRuleProcessor().getPlayer(name);
		new EnableFeatureAction(feature, value).fire(player, null, null);
	}
}
