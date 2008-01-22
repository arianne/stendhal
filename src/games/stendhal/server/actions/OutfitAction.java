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
package games.stendhal.server.actions;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class OutfitAction implements ActionListener {

	private static final String _VALUE = "value";
	private static final String _OUTFIT = "outfit";

	public static void register() {
		CommandCenter.register(_OUTFIT, new OutfitAction());
	}

	public void onAction(Player player, RPAction action) {
		if (action.has(_VALUE)) {
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					_OUTFIT, action.get(_VALUE));
			Outfit outfit = new Outfit(action.getInt(_VALUE));
			if (outfit.isChoosableByPlayers()) {
				player.setOutfit(outfit, false);
			}
		}
	}
}
