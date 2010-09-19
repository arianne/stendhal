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
package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

/**
 * Stores information about the place of the deathmatch.
 *
 * @author hendrik
 */
public class DeathmatchInfo {

	private final DeathmatchArea arena;

	private final Spot entranceSpot;

	private final StendhalRPZone zone;

	/**
	 * Creates a new DeathmatchInfo.
	 *
	 * @param arena
	 *            combat area
	 * @param zone
	 *            zone
	 * @param entrance the spot where the players stands before entering DM.
	 */
	public DeathmatchInfo(final Area arena, final StendhalRPZone zone,
			final Spot entrance) {
		super();
		this.arena = new DeathmatchArea(arena);
		this.zone = zone;
		this.entranceSpot = entrance;
	}

	
	/**
	 * Gets the arena.
	 *
	 * @return combat area
	 */
	public Area getArena() {
		return arena.getArea();
	}

	/**
	 * Gets the zone.
	 *
	 * @return zone
	 */
	public StendhalRPZone getZone() {
		return zone;
	}

	public boolean isInArena(final Player player) {
		return arena.contains(player);
	}

	Spot getEntranceSpot() {
		return entranceSpot;
	}

	void startSession(final Player player,final EventRaiser raiser ) {
		final DeathmatchState deathmatchState = DeathmatchState.createStartState(player.getLevel());
		player.setQuest("deathmatch", deathmatchState.toQuestString());
		final DeathmatchEngine dmEngine = new DeathmatchEngine(player, this, raiser);
		SingletonRepository.getTurnNotifier().notifyInTurns(0, dmEngine);
	}
	
	
}
