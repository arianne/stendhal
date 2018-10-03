/***************************************************************************
 *                      (C) Copyright 2018 - Stendhal                      *
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

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * removes the specified zone from the world
 */
public class RemoveZone extends ScriptImpl {
    private static Logger logger = Logger.getLogger(RemoveZone.class);

    @Override
    public void execute(Player admin, List<String> args) {
        StendhalRPWorld world = StendhalRPWorld.get();
        StendhalRPZone zone = world.getZone(args.get(0));
        if (zone == null) {
            admin.sendPrivateText("No such zone");
            return;
        }

        try {
            world.removeRPZone(zone.getID());
        } catch (Exception e) {
            logger.error(e, e);
            admin.sendPrivateText("Removing zone failed");
        }
    }
}
