/* $Id$ */
/***************************************************************************
 *                 Copyright © 2007-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.Iterator;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.impl.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;

/**
 * Counts the number of creatures on the world.
 *
 * @author hendrik
 */
public class CountObjects extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		int count = 0;
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		for (final IRPZone irpZone : world) {
			final StendhalRPZone zone = (StendhalRPZone) irpZone;
			final Iterator<RPObject> itr2 = zone.iterator();
			while (itr2.hasNext()) {
				itr2.next();
				count++;
			}
		}
		admin.sendPrivateText("Number of objects " + count);
	}
}
