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

	/** Determines if emote action will be directed at player. */
	private final boolean towardPlayer;

	/**
	 * Creates a new EmoteAction.
	 *
	 * @param npcAction text to say as emote
	 */
	public NPCEmoteAction(final String npcAction) {
		this.npcAction = npcAction.trim();
		this.towardPlayer = true;
	}

	/**
	 * Creates a new EmoteAction that can optionally be directed at the player.
	 *
	 * @param npcAction
	 * 			Text to say as emote.
	 * @param towardPlayer
	 * 			<code>boolean</code>: If <true>, will be directed at player.
	 */
	public NPCEmoteAction(final String npcAction, final boolean towardPlayer) {
		this.npcAction = npcAction.trim();
		this.towardPlayer = towardPlayer;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (towardPlayer) {
			raiser.say("!me " + npcAction + " " + player.getName());
		} else {
			raiser.say("!me " + npcAction);
		}
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
