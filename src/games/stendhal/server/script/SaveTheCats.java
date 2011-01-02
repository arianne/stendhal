/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * saves cats (workaround for https://sourceforge.net/support/tracker.php?aid=3149889 )
 *
 * @author hendrik
 */
public class SaveTheCats extends ScriptImpl implements TurnListener {

	@Override
	public void execute(Player admin, List<String> args) {
		TurnNotifier.get().notifyInTurns(1, this);
	}

	public void onTurnReached(int currentTurn) {
		TurnNotifier.get().notifyInTurns(1, this);
		StendhalRPWorld world = StendhalRPWorld.get();
		for (IRPZone zone : world) {
			List<RPEntity> friends = ((StendhalRPZone) zone).getPlayerAndFriends();
			Iterator<RPEntity> itr = friends.iterator();
			while (itr.hasNext()) {
				RPEntity entity = itr.next();
				if (entity instanceof Cat) {
					itr.remove();
				}
			}
		}
	}

	@Override
	public void unload(Player admin, List<String> args) {
		super.unload(admin, args);
		TurnNotifier.get().dontNotify(this);
	}
	
	
}
