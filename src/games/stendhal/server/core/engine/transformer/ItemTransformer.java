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
package games.stendhal.server.core.engine.transformer;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.item.scroll.MarkedScroll;
import games.stendhal.server.entity.player.UpdateConverter;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class ItemTransformer {
	private static Logger logger = Logger.getLogger(ItemTransformer.class);

	/**
	 * Transform an <code>RPObject</code> to an item
	 *
	 * @param rpobject	the object to be transformed
	 * @return	Item corresponding to the <code>RPObject</code>
	 */
	public Item transform(RPObject rpobject) {
		// We simply ignore corpses...
		if (rpobject.get("type").equals("item")) {

			final String name = UpdateConverter.updateItemName(rpobject.get("name"));
			final Item item = UpdateConverter.updateItem(name);

			if (item == null) {
				// no such item in the game anymore
				return null;
			}

			item.setID(rpobject.getID());

			boolean autobind = item.has("autobind");
			if (rpobject.has("persistent")
					&& (rpobject.getInt("persistent") == 1)) {
				/*
				 * Keep [new] rpclass
				 */
				final RPClass rpclass = item.getRPClass();
				item.fill(rpobject);
				item.setRPClass(rpclass);

				// If we've updated the item name we don't want persistent reverting it
				item.put("name", name);
				// Also autobinding must work for persistent items
				if (autobind) {
					item.put("autobind", "");
				}
			}

			if (item instanceof StackableItem) {
				int quantity = 1;
				if (rpobject.has("quantity")) {
					quantity = rpobject.getInt("quantity");
				} else {
					logger.warn("Adding quantity=1 to "
							+ rpobject
							+ ". Most likely cause is that this item was not stackable in the past");
				}
				((StackableItem) item).setQuantity(quantity);

				if (quantity <= 0) {
					logger.warn("Ignoring item "
							+ name
							+ " because this item has an invalid quantity: "
							+ quantity);
					return null;
				}
			}

			// make sure saved individual information is
			// restored
			final String[] individualAttributes = { "infostring",
					"description", "bound", "undroppableondeath",
					"uses", "logid"};
			for (final String attribute : individualAttributes) {
				if (rpobject.has(attribute)) {
					item.put(attribute, rpobject.get(attribute));
				}
			}
			UpdateConverter.updateItemAttributes(item);

			// update visible destination info on marked scrolls
			if (item instanceof MarkedScroll) {
				((MarkedScroll) item).applyDestInfo();
			}

			// Contents, if the item has slot(s)
			for (RPSlot slot : rpobject.slots()) {
				RPSlot itemSlot = item.getSlot(slot.getName());
				for (RPObject obj : slot) {
					// Transform the contents too
					itemSlot.add(transform(obj));
				}
			}

			return item;
		} else {
			logger.warn("Non-item object found: " + rpobject);
			return null;
		}
	}
}
