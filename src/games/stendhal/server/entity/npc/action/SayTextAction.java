/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
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

import java.util.List;

import com.google.common.collect.ImmutableList;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.PlayerMapAdapter;
import games.stendhal.server.util.StringUtils;

/**
 * says the specified text, it works just like the normal parameter of add.
 *
 * But in addition it add support for [variables]. Most notable [name] will
 * be replaced by the players name. And [quest.slotname:1] will be replaced
 * by the value stored in the questslot "slotname" at index 1.
 */
@Dev(category=Category.CHAT, label="\"...\"")
public class SayTextAction implements ChatAction {

	private final List<String> texts;

	/**
	 * Creates a new SayTextAction.
	 *
	 * @param text text to say
	 */
	public SayTextAction(String text) {
		this.texts = ImmutableList.of(checkNotNull(text));
	}

	/**
	 * Creates a new SayTextAction.
	 *
	 * @param texts list of texts from which a random one is said
	 */
	public SayTextAction(Iterable<String> texts) {
		this.texts = ImmutableList.copyOf(checkNotNull(texts));
	}

	/**
	 * Creates a new SayTextAction.
	 *
	 * @param texts array of texts from which a random one is said
	 */
	public SayTextAction(String[] texts) {
		this.texts = ImmutableList.copyOf(checkNotNull(texts));
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		PlayerMapAdapter map = new PlayerMapAdapter(player);
		raiser.say(StringUtils.substitute(Rand.rand(texts), map));
	}

	@Override
	public String toString() {
		return "SayText";
	}

	@Override
	public int hashCode() {
		return 5417 * texts.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayTextAction)) {
			return false;
		}
		SayTextAction other = (SayTextAction) obj;
		return texts.equals(other.texts);
	}
}
