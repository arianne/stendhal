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
package games.stendhal.server.entity.npc.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Chooses and equips the specified item from a list
 */
@Dev(category=Category.ITEMS_OWNED, label="Item+")
public class EquipRandomItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(EquipRandomItemAction.class);

	private Map<String, Integer> items = new HashMap<String, Integer>();
	private final boolean bind;

	/**
	 * Creates a new EquipRandomItemAction.
	 *
	 * @param items
	 *           items and quantities
	 */
	public EquipRandomItemAction(final Map<String, Integer> items) {
		this(items, false);
	}

	/**
	 * Creates a new EquipRandomItemAction.
	 *
	 * @param items
	 *            items and quantities
	 * @param bind
	 *            bind to player
	 */
	@Dev
	public EquipRandomItemAction(final Map<String, Integer> items, final boolean bind) {
		this.items = items;
		this.bind = bind;
	}

	/**
	 * Creates a new EquipRandomItemAction.
	 *
	 * @param itemlist
	 *            items and quantities as a String item=q1;item2=q2
	 */
	public EquipRandomItemAction(final String itemlist) {
		this(itemlist, false);
	}

	/**
	 * Creates a new EquipRandomItemAction.
	 *
	 * @param itemlist
	 *            items and quantities as a String item=q1;item2=q2
	 * @param bind
	 *            bind to player
	 */
	public EquipRandomItemAction(final String itemlist, final boolean bind) {
		String[] elements = itemlist.split(";");
		if (elements.length == 1) {
			logger.warn("Using random item function for one item? List: " + itemlist);
		}
		for (String element : elements) {
			String[] subelements = element.split("=");
			String itemname = subelements[0];
			Integer amount = 1;
			try {
				amount = Integer.parseInt(subelements[1]);
			} catch (ArrayIndexOutOfBoundsException ex) {
				logger.error("Bad format for list " + itemlist);
			} catch (NumberFormatException ex) {
				logger.error("Bad number " + subelements[1]  + " for entry " + subelements[0] + " in list " + itemlist);
			}
			items.put(itemname, amount);
		}
		this.bind = bind;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		final String itemName = Rand.rand(items.keySet());
		final Integer amount = items.get(itemName);
		new EquipItemAction(itemName, amount, bind).fire(player, null, null);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("equip ones of items <");
		sb.append(" ");
		sb.append(items.toString());
		if (bind) {
			sb.append(" (bind)");
		}
		sb.append(">");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + items.hashCode();
		if (bind) {
			result = PRIME * result;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EquipRandomItemAction other = (EquipRandomItemAction) obj;
		if (items == null) {
			if (other.items != null) {
				return false;
			}
		} else if (!items.equals(other.items)) {
			return false;
		}
		return bind == other.bind;
	}

}
