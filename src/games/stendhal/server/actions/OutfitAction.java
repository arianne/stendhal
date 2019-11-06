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

import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Changes the outfit for the player
 */
public class OutfitAction implements ActionListener {
	private static String COLOR_MAP = "outfit_colors";

	public static void register() {
		CommandCenter.register(OUTFIT, new OutfitAction());
		// backward compatibility
		CommandCenter.register("outfit", new OutfitAction());
	}

	/**
	 * Changes Player's outfit to the value provided in action.
	 * @param player whose outfit is to be changed. Must not be <code>null</code>.
	 * @param action the action containing the outfit info in the attribute 'value'. Must not be <code>null</code>.
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(VALUE)) {
			final Outfit outfit = new Outfit(action.get(VALUE));

			if (outfit.isChoosableByPlayers()) {
				new GameEvent(player.getName(), OUTFIT,
						action.get(VALUE)).raise();
				// hack
				player.setOutfitWithDetail(outfit, false);
				//player.setOutfit(outfit, false);

				// Players may change hair color
				String color = action.get("hair");
				if (color != null) {
					player.put(COLOR_MAP, "hair", color);
				} else {
					player.remove(COLOR_MAP, "hair");
				}

				// Players may change eyes color
				color = action.get("eyes");
				if (color != null) {
					player.put(COLOR_MAP, "eyes", color);
				} else {
					player.remove(COLOR_MAP, "eyes");
				}

				// Players may change dress color
				color = action.get("dress");
				if (color != null) {
					player.put(COLOR_MAP, "dress", color);
				} else {
					player.remove(COLOR_MAP, "dress");
				}

				// Players may change skin color
				color = action.get("skin");
				// Only allow certain skin colors.
				if (color != null && SkinColor.isValidColor(MathHelper.parseInt(color))) {
					player.put(COLOR_MAP, "skin", color);
				} else {
					player.remove(COLOR_MAP, "skin");
				}
			}
		}
	}
}
