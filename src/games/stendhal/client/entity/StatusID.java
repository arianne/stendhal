/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import java.util.HashMap;
import java.util.Map;

/** Status IDs */
public enum StatusID {
    CONFUSE,
    POISON,
    SHOCK;
    
    private static final Map<String, StatusID> map = new HashMap<String, StatusID>();
    static {
    	map.put("status_confuse", CONFUSE);
        map.put("poisoned", POISON);
        map.put("status_shock", SHOCK);
    }
    
    /**
     * Find the status ID using the status name.
     * 
     * @param status
     *      Name of status
     * @return
     *      Status ID
     */
    public static StatusID getStatusID(String status) {
    	return map.get(status);
    }
}
