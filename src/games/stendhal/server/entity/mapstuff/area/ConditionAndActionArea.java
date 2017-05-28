/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
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

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

public class ConditionAndActionArea extends AreaEntity {
    private ChatAction action;

    private ChatCondition condition;


    ConditionAndActionArea(final ChatCondition condition, final ChatAction action) {
        this.action = action;
        this.condition = condition;
    }

    ConditionAndActionArea(final ChatCondition condition, final ChatAction action, final int width, final int height) {
        super(width, height);
        this.action = action;
        this.condition = condition;
    }

    public boolean use(Player player) {
        if (action != null) {
            if ((condition == null) || (condition.fire(player, null, null))) {
                action.fire(player, null, null);
                return true;
            }
        }
        return false;
    }

    public ChatAction getAction() {
        return action;
    }

    public ChatCondition getCondition() {
        return condition;
    }

    public void setAction(ChatAction action) {
        this.action = action;
    }

    public void setCondition(ChatCondition condition) {
        this.condition = condition;
    }
}
