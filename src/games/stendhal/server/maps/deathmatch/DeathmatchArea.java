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

import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

public class DeathmatchArea implements LoginListener {
	private final Area area;

	private Spot cowardSpot;

	DeathmatchArea(final Area area) {
		super();
		this.area = area;
		initialize();
	}

	protected void initialize() {

		SingletonRepository.getLoginNotifier().addListener(this);

	}

	@Override
	public void onLoggedIn(final Player player) {

		if (area.contains(player)) {
			teleportToCowardPlace(player);
		}

	}

	private void teleportToCowardPlace(final Player player) {

		if (cowardSpot == null) {
			cowardSpot = new Spot(SingletonRepository.getRPWorld().getZone(
			"0_semos_mountain_n2_w"), 104, 123);
		}
		player.teleport(cowardSpot.getZone(), cowardSpot.getX(), cowardSpot.getY(), Direction.DOWN, player);
		player.sendPrivateText("You wake up far away from the city in the mountains. But you don't know what happened.");
	}

	public Area getArea() {
		return area;
	}

	public boolean contains(final Player player) {

		return area.contains(player);
	}

	public List<Player> getPlayers() {
		return area.getPlayers();
	}

}
