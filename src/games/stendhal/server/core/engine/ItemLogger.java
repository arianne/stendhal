/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.db.StendhalItemDAO;
import games.stendhal.server.core.engine.dbcommand.AbstractLogItemEventCommand;
import games.stendhal.server.core.engine.dbcommand.LogMergeItemEventCommand;
import games.stendhal.server.core.engine.dbcommand.LogSimpleItemEventCommand;
import games.stendhal.server.core.engine.dbcommand.LogSplitItemEventCommand;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.db.command.DBCommandPriority;
import marauroa.server.db.command.DBCommandQueue;

/**
 * Item Logger.
 *
 * @author hendrik
 */
public class ItemLogger {


	public void addLogItemEventCommand(final AbstractLogItemEventCommand command) {
		DBCommandQueue.get().enqueue(command, DBCommandPriority.LOW);
	}


	private String getQuantity(final RPObject item) {
		int quantity = 1;
		if (item.has("quantity")) {
			quantity = item.getInt("quantity");
		}
		return Integer.toString(quantity);
	}

	public void loadOnLogin(final Player player, final RPSlot slot, final Item item) {
		if (item.has(StendhalItemDAO.ATTR_ITEM_LOGID)) {
			return;
		}
		addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "create", item.get("name"), getQuantity(item), "olditem",
				slot.getName()));
	}

	public void destroyOnLogin(final Player player, final RPSlot slot, final RPObject item) {
		addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "destroy", item.get("name"), getQuantity(item), "on login",
				slot.getName()));
	}

	public void destroy(final RPEntity entity, final RPSlot slot, final RPObject item) {
		destroy(entity, slot, item, "quest");
	}

	public void destroy(final RPEntity entity, final RPSlot slot, final RPObject item, String reason) {
		String slotName = "";
		if (slot != null) {
			slotName = slot.getName();
		}
		addLogItemEventCommand(new LogSimpleItemEventCommand(item, entity, "destroy", item.get("name"), getQuantity(item), reason,
				slotName));
	}

	public void dropQuest(final Player player, final Item item) {
		addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "destroy", item.get("name"), getQuantity(item), "quest", null));
	}

	/**
	 * Call when the item or its container times out, or the containing zone is
	 * destroyed
	 *
	 * @param item Item to log timeout for
	 */
	public void timeout(final Item item) {
		LogSimpleItemEventCommand command;
		if (!item.isContained()) {
			command = new LogSimpleItemEventCommand(item, null, "destroy", item.get("name"), getQuantity(item), "timeout", item.getZone().getID().getID() + " " + item.getX() + " " + item.getY());
		} else {
			RPObject base = item.getBaseContainer();
			if (base instanceof Entity) {
				Entity baseEntity = (Entity) base;
				command = new LogSimpleItemEventCommand(item, null, "destroy",
						item.get("name"), getQuantity(item),
						"timeout", baseEntity.getZone().getID().getID()
						+ " " + baseEntity.getX() + " " + baseEntity.getY()
						+ " (" + baseEntity.getRPClass().getName() + ")");
			} else {
				return;
			}
		}
		addLogItemEventCommand(command);
	}

	public void displace(final Player player, final PassiveEntity item, final StendhalRPZone zone, final int oldX, final int oldY, final int x, final int y) {
		addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "ground-to-ground", zone.getID().getID(), oldX + " " + oldY,
				zone.getID().getID(), x + " " + y));
	}

	public void equipAction(final Player player, final Entity entity, final String[] sourceInfo, final String[] destInfo) {
		addLogItemEventCommand(new LogSimpleItemEventCommand(entity, player, sourceInfo[0] + "-to-" + destInfo[0], sourceInfo[1],
				sourceInfo[2], destInfo[1], destInfo[2]));
	}

	public void merge(final RPEntity entity, final Item oldItem, final Item outlivingItem) {
		if (!(entity instanceof Player)) {
			return;
		}
		final Player player = (Player) entity;

		addLogItemEventCommand(new LogMergeItemEventCommand(player, oldItem, outlivingItem));
	}

	public void splitOff(final RPEntity player, final Item item, final int quantity) {
		final String oldQuantity = getQuantity(item);
		final String outlivingQuantity = Integer.toString(Integer.parseInt(oldQuantity) - quantity);
		addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "split out", "-1", oldQuantity, outlivingQuantity, Integer.toString(quantity)));
	}


	public void splitOff(final RPEntity player, final Item item, final Item newItem, final int quantity) {
		if (!(player instanceof Player)) {
			return;
		}
		addLogItemEventCommand(new LogSplitItemEventCommand(player, item, newItem));
	}

	/*
	create             name         quantity          quest-name / killed creature / summon zone x y / summonat target target-slot quantity / olditem
	slot-to-slot       source       source-slot       target    target-slot
	ground-to-slot     zone         x         y       target    target-slot
	slot-to-ground     source       source-slot       zone         x         y
	ground-to-ground   zone         x         y       zone         x         y
	use                old-quantity new-quantity
	destroy            name         quantity          by admin / by quest / on login / timeout on ground
	merge in           outliving_id      destroyed-quantity   outliving-quantity       merged-quantity
	merged in          destroyed_id      outliving-quantity   destroyed-quantity       merged-quantity
	split out          new_id            old-quantity         outliving-quantity       new-quantity
	splitted out       outliving_id      old-quantity         new-quantity             outliving-quantity

	the last two are redundant pairs to simplify queries
	 */


}
