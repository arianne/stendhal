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
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * plays the specified sound
 */
public class PlaySoundAction implements ChatAction {

	private final String sound;

	
	this.sound = sound;
	
	private final Player player;
	
	/**
	 * Creates a new PlaySoundAction.
	 * 
	 * @param sound sound to play
	 */
	public PlaySoundAction(String sound) {
		this.sound = sound;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		 
	}

	@Override
	public String toString() {
		return "PlaySound";
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

	
	}
	class SoundTurnListener implements TurnListener {
		
	@Override
	public void onTurnReached(int currentTurn) {
		player.addEvent(new SoundEvent(sound, SoundLayer.CREATURE_NOISE));
			
}
}