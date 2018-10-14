/***************************************************************************
 *                     (C) Copyright 2012 - Stendhal                       *
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

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * sets the direction of an NPC
 *
 * @author hendrik
 */
@Dev(category=Category.LOCATION, label="NPC")
public final class NPCSetDirection implements ChatAction {

	private final Direction direction;

	/**
	 * sets the direction of an NPC
	 *
	 * @param direction Direction into which the NPC should look
	 */
	public NPCSetDirection(Direction direction) {
		this.direction = checkNotNull(direction);
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (npc.getEntity() instanceof ActiveEntity) {
			((ActiveEntity) npc.getEntity()).setDirection(direction);
		}
	}

	@Override
	public String toString() {
		return "NPC set direction<" + direction + ">";
	}

	@Override
	public int hashCode() {
		return 5347 * direction.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof NPCSetDirection)) {
			return false;
		}
		final NPCSetDirection other = (NPCSetDirection) obj;
		return direction.equals(other.direction);
	}
}
