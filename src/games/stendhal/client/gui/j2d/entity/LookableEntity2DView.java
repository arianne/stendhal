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
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.StatefulEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import marauroa.common.game.RPObject;

/**
 * View of a lookable entity
 *
 * @author madmetzger
 * @param <T> entity type
 */
class LookableEntity2DView<T extends StatefulEntity> extends VariableSpriteEntity2DView<T> {

    @Override
    public int getZIndex() {
        // blocks should be at the same z index as players
        final RPObject obj = this.getEntity().getRPObject();
        if (obj.has("z")) {
            return obj.getInt("z");
        }
        return super.getZIndex();
    }

    @Override
    public void onAction() {
        onAction(ActionType.LOOK);
    }

    @Override
    public StendhalCursor getCursor() {
        return StendhalCursor.LOOK;
    }

}
