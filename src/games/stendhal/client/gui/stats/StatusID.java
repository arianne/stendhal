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
package games.stendhal.client.gui.stats;

import java.util.HashMap;
import java.util.Map;

/** Status IDs */
public enum StatusID {
    CONFUSE,
    POISON,
    SHOCK;
    
    /**
     * Find the status ID using the status name.
     * 
     * @param status
     *      Name of status
     * @return
     *      Status ID
     */
    public static StatusID getStatusID(String status) {
        Map<String, StatusID> IDMap = new HashMap<String, StatusID>();
        IDMap.put("status_confuse", CONFUSE);
        IDMap.put("poisoned", POISON);
        IDMap.put("status_shock", SHOCK);
        
        return IDMap.get(status);
    }
}
