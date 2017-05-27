/***************************************************************************
 *                   (C) Copyright 2003-2013 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.query;

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.WHERE;

import games.stendhal.common.ItemTools;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Tells the client where the queried player is on the world.
 */
public class WhereAction implements ActionListener {

	/**
	 * registers where actions
	 */
	public static void register() {
		CommandCenter.register(WHERE, new WhereAction());
	}

	/**
	 * processes the requested action.
	 *
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String whoName = action.get(TARGET);

			final StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();

			new GameEvent(player.getName(), WHERE, whoName).raise();

			final Player who = rules.getPlayer(whoName);
			final DomesticAnimal animal = player.searchAnimal(whoName, false);

			if (who != null) {
				if (who.isGhost() && !who.equals(player)) {
					player.sendPrivateText("No player or pet named \"" + whoName + "\" is currently logged in.");
				} else {
					final StendhalRPZone zone = who.getZone();

					if (zone != null) {
						if (who.equals(player)) {
							player.sendPrivateText("You are in " + zone.getName()
									+ " at (" + who.getX() + "," + who.getY() + ")");
						} else {
							player.sendPrivateText(who.getTitle() + " is in " + zone.getName()
									+ " at (" + who.getX() + "," + who.getY() + ")");
						}
					}
				}
			}

			if (animal != null) {
				player.sendPrivateText("Your " + ItemTools.itemNameToDisplayName(animal.get("type"))
						+ " is at (" + animal.getX() + "," + animal.getY() + ")");
			}

			if ((who == null) && (animal == null)) {
				player.sendPrivateText("No player or pet named \"" + whoName + "\" is currently logged in.");
			}

			player.notifyWorldAboutChanges();
		}
	}
}
