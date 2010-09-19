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
 * Loads a list of permissions/ranks for a guild from the guilds zone.
 * @author timothyb89
 */
public class GuildPermissionList {
    
    public static final String GUILD_ZONE = GuildList.GUILD_ZONE;
    
    private static GuildPermissionList instance;
    
    private StendhalRPZone zone;
    private List<GuildPermission> perms;
    
    private GuildPermissionList() {
        loadZone();
        loadPermissions();
    }
    
    public static GuildPermissionList get() {
        if (instance == null) {
            instance = new GuildPermissionList();
        }
        return instance;
    }
    
    private void loadZone() {
        zone = SingletonRepository.getRPWorld().getZone(GUILD_ZONE);
    }
    
    private void loadPermissions() {
        perms = new LinkedList<GuildPermission>();
        for (final RPObject o : zone) {
            if (o instanceof GuildPermission) {
                perms.add((GuildPermission) o);
            }
        }
    }
    
    public void addPermission(final GuildPermission g) {
        zone.add(g);
        zone.storeToDatabase();
    }
    
    public void removePermission(final GuildPermission p) {
        zone.remove(p);
        zone.storeToDatabase();
    }
    
    public void removePermission(final String identifier) {
        for (final GuildPermission g : perms) {
            if (g.getIdentifier().equals(identifier)) {
                zone.remove(g);
                break;
            }
        }
        zone.storeToDatabase();
    }
    
    public List<GuildPermission> getPermissionsForGuild(final Guild guild) {
        final List<GuildPermission> gps = new LinkedList<GuildPermission>();
        for (final GuildPermission g : perms) {
            if (g.getGuild().equals(guild.getIdentifier())) {
                gps.add(g);
            }
        }
        return gps;
    }
    
}
