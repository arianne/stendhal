/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import java.awt.geom.Rectangle2D;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

/**
 * Checks if there is room for a player within a restricted area.
 */
public class AreaIsFullCondition implements ChatCondition {

	/** area to check */
	private final Area area;
	/** maximum occupancy */
	private final int max;


	public AreaIsFullCondition(final StendhalRPZone zone, final Rectangle2D area, final int max) {
		this.area = new Area(zone, area);
		this.max = max;
	}

	public AreaIsFullCondition(final StendhalRPZone zone, final int max) {
		this(zone, null, max);
	}

	public AreaIsFullCondition(final String zoneid, final Rectangle2D area, final int max) {
		this(SingletonRepository.getRPWorld().getZone(zoneid), area, max);
	}

	public AreaIsFullCondition(final String zoneid, final int max) {
		this(zoneid, null, max);
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return area.getPlayers().size() >= max;
	}
}
