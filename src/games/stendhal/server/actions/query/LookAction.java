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

import static games.stendhal.common.constants.Actions.LOOK;
import static games.stendhal.common.constants.Actions.NAME;
import static games.stendhal.common.constants.Actions.TYPE;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.Actions;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.validator.ActionData;
import games.stendhal.server.actions.validator.ActionValidation;
import games.stendhal.server.actions.validator.ExtractEntityValidator;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Processes a look menu action.
 */
public class LookAction implements ActionListener {

	private static final ActionValidation VALIDATION = new ActionValidation();;
	static {
		VALIDATION.add(new ExtractEntityValidator());
	}

	/**
	 * registers the look action
	 */
	public static void register() {
		CommandCenter.register(LOOK, new LookAction());
	}

	/**
	 * processes the requested action.
	 *
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		ActionData data = new ActionData();
		if (!VALIDATION.validateAndInformPlayer(player, action, data)) {
			return;
		}

		Entity entity = data.getEntity();

		if (entity != null) {

			if (entity instanceof Item) {
				Item item = (Item) entity;
				RPObject base = item.getBaseContainer();
				if (base instanceof Player) {
					Player owner = (Player) base;
					// Check if looking is allowed
					if ((player != owner) && isHiddenSlot(owner, item.getContainerSlot().getName())) {
						return;
					}
				}
			}

			String name = entity.get(TYPE);
			if (entity.has(NAME)) {
				name = entity.get(NAME);
			}
			new GameEvent(player.getName(), LOOK, name).raise();
			final String text = entity.describe();

			if (entity.has(Actions.ACTION) && entity.get(Actions.ACTION).equals(Actions.READ)) {
				player.sendPrivateText(NotificationType.RESPONSE, text);
			} else {
				player.sendPrivateText(text);
			}

			player.notifyWorldAboutChanges();
		}
	}

	/**
	 * Check if a slot is hidden to others than the player itself.
	 *
	 * @param player
	 * @param slotName
	 * @return <code>true</code> if other players should not be able to view
	 * 	the slot contents, <code>false</code> if it's allowed.
	 */
	private boolean isHiddenSlot(Player player, String slotName) {
		byte flags = player.getRPClass().getDefinition(DefinitionClass.RPSLOT, slotName).getFlags();
		return (((flags & Definition.PRIVATE) | (flags & Definition.HIDDEN)) != 0);
	}
}
