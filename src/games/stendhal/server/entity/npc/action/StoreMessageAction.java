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

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.command.DBCommandQueue;

/**
 * Stores a message for delivery with postman
 *
 * @author kymara
 */
@Dev(category=Category.CHAT, label="Message")
public class StoreMessageAction implements ChatAction {

	private final String npcName;
	private final String message;

	/**
	 * creates a new StoreMessageAction
	 *
	 * @param npcName who left the message
	 * @param message what the message is
	 */
	public StoreMessageAction(String npcName, String message) {
		this.npcName = checkNotNull(npcName);
		this.message = checkNotNull(message);
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		DBCommandQueue.get().enqueue(new StoreMessageCommand(npcName, player.getName(), message, "N"));
	}

	@Override
	public int hashCode() {
		return 5651 * (npcName.hashCode() + 5653 * message.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof StoreMessageAction)) {
			return false;
		}
		StoreMessageAction other = (StoreMessageAction) obj;
		return npcName.equals(other.npcName)
			&& message.equals(other.message);
	}
}
