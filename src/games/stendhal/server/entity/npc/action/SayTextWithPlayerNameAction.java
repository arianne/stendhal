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
import games.stendhal.server.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * States the name of the player emitting a text
 *
 * @author kymara
 */
@Dev(category=Category.CHAT, label="\"...\"")
public class SayTextWithPlayerNameAction implements ChatAction {

	private final String text;

	/**
	 * Creates a new SayTextWithPlayerNameAction
	 *
	 * @param text
	 *            text with substitution [name] for the player name
	 *
	 */
	public SayTextWithPlayerNameAction(final String text) {
		this.text = checkNotNull(text);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		Map<String, String> substitutes = new HashMap<String, String>();
		substitutes.put("name", player.getTitle());

		raiser.say(StringUtils.substitute(text, substitutes));
	}

	@Override
	public String toString() {
		return "SayTextWithPlayerNameAction <" + text + ">";
	}

	@Override
	public int hashCode() {
		return 5417 * text.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayTextWithPlayerNameAction)) {
			return false;
		}
		SayTextWithPlayerNameAction other = (SayTextWithPlayerNameAction) obj;
		return text.equals(other.text);
	}

}
