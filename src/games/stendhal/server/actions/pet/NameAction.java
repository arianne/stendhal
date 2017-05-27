/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.pet;

import java.util.List;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * @author Martin Fuchs
 */
public class NameAction implements ActionListener {

	/**
	 * Registers the "name" action handler.
	 */
	public static void register() {
		final NameAction name = new NameAction();
		CommandCenter.register("name", name);
	}

	/**
	 * Handle the /name action.
	 * @param player
	 * @param action
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		String curName = action.get("target");
		String newName = action.get("args");

		if ((newName == null) || (newName.length() == 0)) {
			player.sendPrivateText("Please issue the old and the new name.");
			return;
		}

		final List<DomesticAnimal> animals = player.getAnimals();

		if (animals.isEmpty()) {
			player.sendPrivateText("You don't own any " + curName);
		} else {
			DomesticAnimal animal = null;

			do {
				animal = player.searchAnimal(curName, false);
				if (animal != null) {
					// remove quotes, if present
					if ((newName.charAt(0) == '\'') && (newName.charAt(newName.length() - 1) == '\'')) {
						newName = newName.substring(1, newName.length() - 1);
					}

					newName = newName.trim();

					// check only if the pet is actually named newName, rather than just recognises it
					if (player.searchAnimal(newName, true) != null) {
						player.sendPrivateText("You own already a pet named '" + newName + "'");
					} else if (newName.length() > 0) {
						if (newName.length() > 20) {
							player.sendPrivateText("The new name of your pet must not be longer than 20 characters.");
						} else {
							final String oldName = animal.getTitle();

							animal.setTitle(newName);

							if (oldName != null) {
								player.sendPrivateText("You changed the name of '" + oldName + "' to '" + newName
										+ "'");

							} else {
								player.sendPrivateText("Congratulations, your " + curName + " is now called '"
										+ newName + "'.");
							}
							new GameEvent(player.getName(), "name", animal.getRPClass().getName(), newName).raise();
						}
					} else {
						player.sendPrivateText("Please don't use empty names.");
					}
				} else {
					// see if we can move the word separator one space to the
					// right to search for a pet name
					final int idxSpace = newName.indexOf(' ');

					if (idxSpace != -1) {
						final int idxLastSpace = newName.lastIndexOf(' ', idxSpace);
						curName += " " + newName.substring(0, idxSpace);
						newName = newName.substring(idxLastSpace + 1);
					} else {
						// There is no more other command interpretation.
						break;
					}
				}
			} while (animal == null);

			if (animal == null) {
				player.sendPrivateText("You don't own a pet called '" + curName + "'");
			}
		}
	}

}
