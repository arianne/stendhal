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

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * A status effect that causes the entity to stop moving after a set amount of steps
 * 
 * @author Jordan
 *
 */
public class ShockStatus extends Status {
    
    /** Entity is "shocked" after taking 5 steps */
    private final int stepsDelay = 5;
    
    /**
     * Create the status
     */
    public ShockStatus() {
        // Give the status a name
        super("shock");
        super.setTimeout(20);
    }
    
    /**
     * Called on each turn
     * 
     * @param entity
     */
    @Override
    public void affect(final RPEntity entity) {
        super.affect(entity);
        
        int stepsTaken = entity.getStepsTaken();
        if (stepsTaken == stepsDelay) {
            
            // Stop the entity's movement after 5 steps
            entity.stop();
            entity.clearPath();
            
            if (entity instanceof Player) {
                ((Player) entity).sendPrivateText(NotificationType.SCENE_SETTING, "You are affected by \"shock\"");
            }
        }
    }
}
