/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
 * Tells the player that the NPC did not understand the sentence;
 * use it in combination with SentenceHasErrorCondition.
 */
@Dev(category=Category.CHAT, label="Error")
public class ComplainAboutSentenceErrorAction implements ChatAction {

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (sentence.hasError()) {
			raiser.say("Sorry, I did not understand you. "
				+ sentence.getErrorString());
		}
	}

	@Override
	public String toString() {
		return "complainSentenceError";
	}

	@Override
	public int hashCode() {
		return 5101;
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof ComplainAboutSentenceErrorAction);
	}
}
