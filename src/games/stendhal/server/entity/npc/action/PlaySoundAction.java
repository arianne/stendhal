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

import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * plays the specified sound
 */
@Dev(category=Category.ENVIRONMENT, label="Sound")
public class PlaySoundAction implements ChatAction {

	final String sound;
	private final boolean delay;

	/**
	 * Creates a new PlaySoundAction.
	 *
	 * @param sound sound to play
	 */
	public PlaySoundAction(String sound) {
		this.sound = sound;
		this.delay = false;
	}

	/**
	 * Creates a new PlaySoundAction.
	 *
	 * @param sound sound to play
	 * @param delay delay the sound for one turn because of zone change
	 */
	@Dev
	public PlaySoundAction(String sound, boolean delay) {
		this.sound = sound;
		this.delay = delay;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (!delay) {
			raiser.addEvent(new SoundEvent(sound, SoundLayer.CREATURE_NOISE));
		} else {
			TurnNotifier.get().notifyInTurns(0, new SoundTurnListener(player));
		}
	}

	@Override
	public String toString() {
		return "PlaySound<" + sound + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlaySoundAction.class);
	}


	/**
	 * Plays a sound in the specified turn.
	 */
	class SoundTurnListener implements TurnListener {
		private final Player player;
		SoundTurnListener(Player player) {
			this.player = player;
		}

		/**
		 * plays the sound
		 */
		@Override
		public void onTurnReached(int currentTurn) {
			player.addEvent(new SoundEvent(sound, SoundLayer.CREATURE_NOISE));
		}

	}
}
