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

import com.google.common.base.Objects;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Teleports the player to the specified location.
 */
@Dev(category=Category.LOCATION, label="Teleport")
public class TeleportAction implements ChatAction {

	private final String zonename;
	private final int x;
	private final int y;
	private final Direction direction;

	/**
	 * Creates a new TeleportAction.
	 *
	 * @param zonename
	 *            name of destination zone
	 * @param x
	 *            x-position
	 * @param y
	 *            y-position
	 * @param direction
	 *            facing into this direction
	 */
	public TeleportAction(final String zonename, final int x, final int y, final Direction direction) {
		this.zonename = checkNotNull(zonename);
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zonename);
		player.teleport(zone, x, y, direction, player);
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "Teleport<" + zonename + ", " + x + ", " + y + ", " + direction + ">";
	}

	@Override
	public int hashCode() {
		return 5657 * (zonename.hashCode() + 5659 * (x + 5669 * y));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TeleportAction)) {
			return false;
		}
		TeleportAction other = (TeleportAction) obj;
		return (x == other.x)
				&& (y == other.y)
				&& zonename.equals(other.zonename)
				&& Objects.equal(direction, other.direction);
	}
}
