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
package games.stendhal.server.entity.item;

import java.util.Map;

import games.stendhal.common.grammar.Grammar;


/**
 * Represents a stackable item for which we do not want 'Stats' to show in description.
 *
 * @author kymara
 */
public class NoStatsStackableItem extends StackableItem {

	public NoStatsStackableItem(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		update();
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public NoStatsStackableItem(final NoStatsStackableItem item) {
		super(item);
		this.setQuantity(item.getQuantity());
		update();
	}

	@Override
	public String describe() {
		String text = "You see " + Grammar.a_noun(getTitle()) + ".";
		if (hasDescription()) {
			text = getDescription();
		}

		final String boundTo = getBoundTo();

		if (boundTo != null) {
			text = text + " It is a special reward for " + boundTo
					+ ", and cannot be used by others.";
		}
		return (text);
	}

}
