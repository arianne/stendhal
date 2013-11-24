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
package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

public class DigArea extends ConditionAndActionArea {
    DigArea(ChatCondition condition, ChatAction action) {
        super(condition, action);
    }

    DigArea(ChatCondition condition, ChatAction action, int width,
            int height) {
        super(condition, action, width, height);
    }
    
    /**
     * Override so area cannot be used without a shovel
     * 
     * @param player
     *      Player taking the action
     * @return
     *      Always false
     */
    @Override
    public boolean use(Player player) {
        // Player did not use a shovel
        return false;
    }
    
    /**
     * Action to take when player uses a shovel
     * 
     * @param player
     *      Player taking action
     * @param tool
     *      The item that was used
     * @return
     *      Player can dig
     */
    public boolean use(Player player, Item tool) {
        // Make sure player used a shovel
        if (tool.getName() == "shovel") {
            return super.use(player);
        }
        return false;
    }
}
