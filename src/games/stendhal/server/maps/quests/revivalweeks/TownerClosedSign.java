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
package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.sign.Sign;

/**
 * Add a sign saying the tower is closed
 *
 * @author hendrik
 */
public class TownerClosedSign {

	// TODO: move this sign to the normal, non-quest map
	// This is actually a lie, the real reason is that we cannot tell apart
	// if someone is behind the top of the tower or in front of it.
	public void addToWorld() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		final Sign sign = new Sign();
		sign.setPosition(105, 114);
		sign.setText("Because of the missing guard rail it is too dangerous to enter the tower.");
		zone.add(sign);
	}
}
