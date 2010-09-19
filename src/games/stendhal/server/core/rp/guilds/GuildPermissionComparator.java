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

import java.util.Comparator;

/**
 * Compares guild ranks. Useful for sorting permissions lists.
 * @author timothyb89
 */
public class GuildPermissionComparator implements Comparator<GuildPermission> {

    /**
     * Compares two GuildPermissions to see if one has greater permissions than 
     * another.
     * @param p1 the first permission
     * @param p2 the second permission
     * @return -1 if p1 > p2, 0 if equal, or 1 if p1 < p2
     */
    public int compare(final GuildPermission p1, final GuildPermission p2) {
        if (p1.getRank() > p2.getRank()) {
            return 1;
        } else if (p1.getRank() == p2.getRank()) {
            return 0;
        } else {
            return -1;
        }
    }
}
