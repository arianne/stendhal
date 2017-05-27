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
package games.stendhal.server.script;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.player.Player;

/**
 * Base class for creating small raids of creatures from other lists
 * Picks randomly from the list and summons raid creatures within a radius of the admin.
 */
public abstract class CreateRaid extends ScriptImpl {
	private static Logger logger = Logger.getLogger(CreateRaid.class);

	private final int RADIUS = 5;

	protected abstract Map<String, Integer> createArmy();

	@Override
	public void execute(final Player admin, final List<String> args) {

		if (args.size() > 0) {
			admin.sendPrivateText(getInfo());
			return;
		}

		// extract position of admin
		final StendhalRPZone myZone = sandbox.getZone(admin);
		final int x = admin.getX();
		final int y = admin.getY();
		sandbox.setZone(myZone);

		for (final Map.Entry<String, Integer> entry : createArmy().entrySet()) {
			Creature template = sandbox.getCreature(entry.getKey());
			if (template != null) {
				final RaidCreature creature = new RaidCreature(template);

				for (int i = 0; i < entry.getValue(); i++) {
					if(Rand.roll1D6()==1) {
						sandbox.add(creature, x + games.stendhal.common.Rand.randUniform(-RADIUS, RADIUS),
								              y + games.stendhal.common.Rand.randUniform(-RADIUS, RADIUS));
					}
				}
			} else {
				logger.error("Unknown creature name " + entry.getKey());
			}
		}
	}

	/**
	 * contains info to help raid makers decide if this special raid script is applicable for the given users or other usefull info.
	 *
	 * @return the info to simplify life of raid maker
	 */
	protected String getInfo() {
		return "no special Info";
	}
}
