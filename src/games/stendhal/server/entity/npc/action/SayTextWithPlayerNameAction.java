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
import games.stendhal.server.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * States the name of the player amid a message
 * 
 */
public class SayTextWithPlayerNameAction implements ChatAction {

	private final String message;

	/**
	 * Creates a new SayTextWithPlayerNameAction
	 * 
	 * @param questname
	 *            name of quest-slot to check
	 * @param message
	 *            message with substitution [name] for the player name
	 *            
	 */
	public SayTextWithPlayerNameAction(final String message) {
		this.message = message;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		Map<String, String> substitutes = new HashMap<String, String>();
		substitutes.put("name", player.getTitle());
			
		raiser.say(StringUtils.substitute(message,substitutes));	
	}


	
	@Override
	public String toString() {
		return "SayTextWithPlayerNameAction <" + message + ">";
	}
	

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SayTextWithPlayerNameAction.class);
	}

	

}
