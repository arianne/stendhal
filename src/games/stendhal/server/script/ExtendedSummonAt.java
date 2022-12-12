/***************************************************************************
 *                    (C) Copyright 2003-2022 - Stendhal                   *
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

import static games.stendhal.common.constants.Actions.AMOUNT;
import static games.stendhal.common.constants.Actions.ITEM;
import static games.stendhal.common.constants.Actions.SLOT;
import static games.stendhal.common.constants.Actions.SUMMONAT;
import static games.stendhal.common.constants.Actions.TARGET;

import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.SlotActivatedItem;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Summons an items and sets the item logid
 *
 * @author hendrik
 */
public class ExtendedSummonAt extends ScriptImpl {

	public RPAction parseParameters(Player admin, List<String> params) {

		if (params.size() < 5) {
			admin.sendPrivateText(NotificationType.ERROR, "Syntax: target slot quantity item logid");
			return null;
		}

		final RPAction summon = new RPAction();
		summon.put("target", params.get(0));
		summon.put("slot", params.get(1));
		String logid = params.get(params.size() - 1);
		try {
			summon.put("logid", Integer.parseInt(logid));
		} catch (final NumberFormatException ex) {
			admin.sendPrivateText(NotificationType.ERROR, "Invalid logid. Syntax: target slot quantity item logid");
			return null;
		}
		try {
			summon.put(AMOUNT, Integer.parseInt(params.get(2)));
		} catch (final NumberFormatException ex) {
			admin.sendPrivateText(NotificationType.ERROR, "Invalid quantity. Syntax: target slot quantity item logid");
			return null;
		}

		StringBuilder itemName = new StringBuilder();
		for (int i = 3; i < params.size() - 1; i++) {
			itemName.append(params.get(i) + " ");
		}

		final String singularName = Grammar.singular(itemName.toString().trim());
		summon.put("item", singularName);
		return summon;
	}

	@Override
	public void execute(Player admin, List<String> args) {
		if (admin.getAdminLevel() < 5000) {
			admin.sendPrivateText(NotificationType.ERROR, "You do not have the admin level required");
			return;
		}

		RPAction action = parseParameters(admin, args);
		if (action == null) {
			return;
		}

		final String name = action.get(TARGET);
		final Player changed = SingletonRepository.getRuleProcessor().getPlayer(name);

		if (changed == null) {
			admin.sendPrivateText("Player \"" + name + "\" not found.");
			return;
		}

		final String slotName = action.get(SLOT);
		if (!changed.hasSlot(slotName)) {
			admin.sendPrivateText("Player \"" + name + "\" does not have an RPSlot named \"" + slotName + "\".");
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
					return;
				}
			}
		}

		final Item item = manager.getItem(type);

		if (action.has(AMOUNT) && (item instanceof StackableItem)) {
			((StackableItem) item).setQuantity(action.getInt(AMOUNT));
		}

		if (!changed.equip(slotName, item)) {
			admin.sendPrivateText(NotificationType.ERROR, "The slot is full.");
			return;
		}
		
		new GameEvent(admin.getName(), SUMMONAT, changed.getName(), slotName, type, "logid: "+ action.get("logid")).raise();
		item.put("logid", action.get("logid"));
		SingletonRepository.getRuleProcessor().sendMessageToSupporters(admin.getName() + " summoned "
				+ action.getInt(AMOUNT) + " " + action.get("item") + " at " + changed.getName() + " slot " + slotName
				+ " with logid " + action.get("logid"));

		// enable effects of slot activated items
		if (item instanceof SlotActivatedItem) {
			((SlotActivatedItem) item).onEquipped(changed, slotName);
		}
	}

}
