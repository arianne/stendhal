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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * npc emoting to player
 */
@Dev(category=Category.CHAT, label="NPC")
public class NPCEmoteAction implements ChatAction {

	private final String npcAction;

	/**
	 * Creates a new EmoteAction.
	 *
	 * @param npcAction text to say as emote
	 */
	public NPCEmoteAction(String npcAction) {
		this.npcAction = npcAction.trim();
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		raiser.say("!me " + npcAction + " " + player.getName());
	}

	@Override
	public String toString() {
		return "NPCEmoteAction";
	}


	@Override
	public int hashCode() {
		return 5333 * npcAction.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof NPCEmoteAction)) {
			return false;
		}
		final NPCEmoteAction other = (NPCEmoteAction) obj;
		return npcAction.equals(other.npcAction);
	}
}
