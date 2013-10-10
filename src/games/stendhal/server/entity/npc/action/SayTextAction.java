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

import static com.google.common.base.Preconditions.checkNotNull;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * says the specified text, it works just like the normal parameter of add
 */
@Dev(category=Category.CHAT, label="\"...\"")
public class SayTextAction implements ChatAction {

	private final String text;

	/**
	 * Creates a new SayTextAction.
	 *
	 * @param text text to say
	 */
	public SayTextAction(String text) {
		this.text = checkNotNull(text);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		raiser.say(text);
	}

	@Override
	public String toString() {
		return "SayText";
	}

	@Override
	public int hashCode() {
		return 5417 * text.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayTextAction)) {
			return false;
		}
		SayTextAction other = (SayTextAction) obj;
		return text.equals(other.text);
	}
}
