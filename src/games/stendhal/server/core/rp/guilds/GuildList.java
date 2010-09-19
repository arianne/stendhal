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
package games.stendhal.server.core.rp.guilds;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

/**
 * Stores all of the guilds in a list of entities.
 * @author timothyb89
 */
public class GuildList {
    
    public static final String GUILD_ZONE = "int_guilds";
    
    private static GuildList instance;
    
    private List<Guild> guilds;
    private StendhalRPZone guildsZone;
    
    private GuildList() {
        loadZone();
        loadGuilds();
    }

    public static GuildList get() {
        if (instance == null) {
            instance = new GuildList();
        }
        return instance;
    }
    
    private void loadZone() {
        guildsZone = SingletonRepository.getRPWorld().getZone(GUILD_ZONE);
    }
    
    private void loadGuilds() {
        guilds = new LinkedList<Guild>();
        for (final RPObject o : guildsZone) {
            if (o instanceof Guild) {
                guilds.add((Guild) o);
            }
        }
    }
    
    public void addGuild(final Guild g) {
        guilds.add(g);
        guildsZone.add(g);
        guildsZone.storeToDatabase();
    }
    
    public void removeGuild(final Guild g) {
        guildsZone.remove(g);
        guildsZone.storeToDatabase();
    }
    
    public void removeGuild(final String identifier) {
        for (final Guild g : guilds) {
            if (g.getIdentifier().equals(identifier)) {
                guildsZone.remove(g);
                break;
            }
        }
        guildsZone.storeToDatabase();
    }
    
    public Guild getGuild(final String name) {
        for (final Guild g : guilds) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }
    
}
