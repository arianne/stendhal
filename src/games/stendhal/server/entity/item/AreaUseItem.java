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
package games.stendhal.server.entity.item;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.area.ConditionAndActionArea;
import games.stendhal.server.entity.player.Player;

import java.util.List;
import java.util.Map;

public class AreaUseItem extends Item {
    public AreaUseItem(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
        super(name, clazz, subclass, attributes);
    }
    
    public AreaUseItem(AreaUseItem item) {
        super(item);
    }
    
    @Override
    public boolean onUsed(RPEntity player) {
        boolean success = false;
        
        StendhalRPZone zone = player.getZone();
        Direction facing = player.getDirection();
        int posX = player.getX();
        int posY = player.getY();
        
        // Tarrget one step in front of player
        if (facing == Direction.RIGHT) {
            posX += 1;
        } else if (facing == Direction.LEFT) {
            posX -= 1;
        } else if (facing == Direction.DOWN) {
            posY += 1;
        } else if (facing == Direction.UP) {
            posY += 1;
        }
        
        List<Entity> entityList = zone.getEntitiesAt(posX, posY);
        for (Entity entity : entityList) {
            if (entity instanceof ConditionAndActionArea) {
                success = ((ConditionAndActionArea) entity).use((Player) player);
            }
        }
        
        return success;
    }
}
