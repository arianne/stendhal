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
package games.stendhal.server.entity.npc.action;

import java.util.List;

import com.google.common.collect.ImmutableList;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.PlayerSlot;

/**
 * creates a slot
 *
 * @author hendrik
 */
public class CreateSlotAction implements ChatAction {
	private final ImmutableList<String> slotNames;

	/**
	 * creates a slot
	 *
	 * @param slotNames list of slots to create
	 */
	public CreateSlotAction(List<String> slotNames) {
		this.slotNames = ImmutableList.copyOf(slotNames);
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		for (String name : slotNames) {
			if (!player.hasSlot(name)) {
				player.addSlot(new PlayerSlot(name));
			}
		}
	}

}
