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
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.AMOUNT;
import static games.stendhal.common.constants.Actions.ITEM;
import static games.stendhal.common.constants.Actions.SLOT;
import static games.stendhal.common.constants.Actions.SUMMONAT;
import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.SlotActivatedItem;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SummonAtAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(SUMMONAT, new SummonAtAction(), 800);
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (action.has(TARGET) && action.has(SLOT) && action.has(ITEM)) {
			final String name = action.get(TARGET);
			final Player changed = SingletonRepository.getRuleProcessor().getPlayer(name);

			if (changed == null) {
				logger.debug("Player \"" + name + "\" not found.");
				player.sendPrivateText("Player \"" + name + "\" not found.");
				return;
			}

			final String slotName = action.get(SLOT);
			if (!changed.hasSlot(slotName)) {
				logger.debug("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				player.sendPrivateText("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				return;
			}

			final EntityManager manager = SingletonRepository.getEntityManager();
			final String typeName = action.get(ITEM);
			String type = typeName;

			// Is the entity an item
			if (!manager.isItem(type)) {
				// see it the name was in plural
				type = Grammar.singular(typeName);

				if (!manager.isItem(type)) {
					// see it the name was in singular but the registered type is in plural
					type = Grammar.plural(typeName);

					if (!manager.isItem(type)) {
						player.sendPrivateText(typeName + " is not an item.");
						type = null;
					}
				}
			}

			if (type != null) {
				new GameEvent(player.getName(), SUMMONAT, changed.getName(), slotName, type).raise();
				final Item item = manager.getItem(type);

				if (action.has(AMOUNT) && (item instanceof StackableItem)) {
					((StackableItem) item).setQuantity(action.getInt(AMOUNT));
				}

				if (!changed.equip(slotName, item)) {
					player.sendPrivateText("The slot is full.");
				} else if (item instanceof SlotActivatedItem) {
					// enable effects of slot activated items
					((SlotActivatedItem) item).onEquipped(player, slotName);
				}
			}
		}
	}

}
