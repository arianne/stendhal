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
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.AMOUNT;
import static games.stendhal.common.constants.Actions.ITEM;
import static games.stendhal.common.constants.Actions.SLOT;
import static games.stendhal.common.constants.Actions.SUMMONAT;
import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.common.NotificationType;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
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
	public void perform(final Player admin, final RPAction action) {
		if (!action.has(TARGET) || !action.has(SLOT) || !action.has(ITEM)) {
			admin.sendPrivateText("Missing parameters");
			return;
		}
		final String name = action.get(TARGET);
		final Player changed = SingletonRepository.getRuleProcessor().getPlayer(name);

		if (changed == null) {
			logger.debug("Player \"" + name + "\" not found.");
			admin.sendPrivateText("Player \"" + name + "\" not found.");
			return;
		}

		final String slotName = action.get(SLOT);
		if (!changed.hasSlot(slotName)) {
			logger.debug("Player \"" + name
					+ "\" does not have an RPSlot named \"" + slotName
					+ "\".");
			admin.sendPrivateText("Player \"" + name
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
					admin.sendPrivateText(typeName + " is not an item.");
					type = null;
				}
			}
		}

		if (type == null) {
			return;
		}
		final Item item = manager.getItem(type);

		if (action.has(AMOUNT) && (item instanceof StackableItem)) {
			((StackableItem) item).setQuantity(action.getInt(AMOUNT));
		}

		if (!changed.equip(slotName, item)) {
			admin.sendPrivateText(NotificationType.ERROR, "The slot is full.");
			return;
		}

		// enable effects of slot activated items
		if (item instanceof SlotActivatedItem) {
			((SlotActivatedItem) item).onEquipped(changed, slotName);
		}

		// log events
		final String changedName = changed.getName();
		new GameEvent(admin.getName(), SUMMONAT, changedName, slotName, type).raise();
		new ItemLogger().summon(admin, item, changedName, slotName);
	}

}
