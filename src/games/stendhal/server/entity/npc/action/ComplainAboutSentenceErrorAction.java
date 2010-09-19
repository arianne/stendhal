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

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * tells the player that the npc did not understand the sentence;
 * use it in combination with SentenceHasErrorCondtion.
 */
public class ComplainAboutSentenceErrorAction implements ChatAction {

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
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				ComplainAboutSentenceErrorAction.class);
	}
}
