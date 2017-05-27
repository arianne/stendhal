/***************************************************************************
 *                   (C) Copyright 2014 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.slot;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import marauroa.common.game.RPSlot;

/**
 * a predicate which tests that the name of a slot is in list
 *
 * @author hendrik
 */
public class SlotNameInList implements Predicate<RPSlot> {
	private final ImmutableList<String> slotNames;

	/**
	 * a predicate which tests that the name of a slot is in list
	 *
	 * @param slotNames
	 */
	public SlotNameInList(List<String> slotNames) {
		this.slotNames = ImmutableList.copyOf(slotNames);
	}

	@Override
	public boolean apply(RPSlot slot) {
		return slotNames.contains(slot.getName());
	}

}
