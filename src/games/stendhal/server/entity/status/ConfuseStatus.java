/***************************************************************************
 *                      (C) Copyright 2013 - Stendhal                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.status;

import games.stendhal.server.entity.RPEntity;

/**
 * A status effect that causes the entity to stop moving after a set amount of steps
 * 
 * @author Jordan
 *
 */
public class ConfuseStatus extends Status {
    
    /**
     * Create the status
     */
    public ConfuseStatus() {
        // Give the status a name
        super("confuse");
        super.setTimeout(200);
        super.setMaxOccurrences(1);
    }
    
    /**
     * Called on each turn
     * 
     * @param entity
     */
    @Override
    public void affect(final RPEntity entity) {
        super.affect(entity);
        
        // Do nothing. The effects of this status are handled in RPEntity sub-classes.
    }
}
