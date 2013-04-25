/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import static games.stendhal.common.constants.Actions.JAIL;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Action for an NPC to jail a player
 *
 * @author kymara
 */
@Dev(category=Category.OTHER, label="Jail-")
public class JailAction implements ChatAction {
	
	private final int minutes;
	private final String reason;

	/**
	 * Creates a new JailAction.
	 * 
	 * @param minutes sentence duration in minutes
	 * @param reason reason for jailing player
	 */
	public JailAction(final int minutes, final String reason) {
		this.minutes = minutes;
		this.reason = reason;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		// e.g this will be fine for SpeakerNPCs
		if (npc.getEntity() instanceof RPEntity) {
			SingletonRepository.getJail().imprison(player.getName(), (RPEntity) npc.getEntity(), minutes, reason);
			new GameEvent(npc.getName(), JAIL, player.getName(), Integer.toString(minutes), reason).raise();
		} 
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("jail <");
		sb.append(this.minutes);
		sb.append(", ");
		sb.append(this.reason);
		sb.append(">");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				JailAction.class);
	}

}
