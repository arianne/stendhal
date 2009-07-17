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

import static games.stendhal.common.constants.Actions.OUTFIT;
import static games.stendhal.common.constants.Actions.VALUE;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;


public class OutfitAction implements ActionListener {


	public static void register() {
		CommandCenter.register(OUTFIT, new OutfitAction());
	}

	/**
	 * Changes Player's outfit to the value provided in action. 
	 * @param player whose outfit is to be changed. Must not be <code>null</code>.
	 * @param action the action containing the outfit info in the attribute 'value'. Must not be <code>null</code>.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (action.has(VALUE)) {
			final Outfit outfit = new Outfit(action.getInt(VALUE));
			if (outfit.isChoosableByPlayers()) {
				new GameEvent(player.getName(), OUTFIT, action.get(VALUE)).raise();
				player.setOutfit(outfit, false);
			}
		}
	}
}
