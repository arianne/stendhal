/**
 * 
 */
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import marauroa.common.game.RPObject;

/**
 * View of a lookable entity
 * 
 * @author madmetzger
 */
public class LookableEntity2DView<T extends Entity> extends Entity2DView<T> {

    @Override
    public int getZIndex() {
        // blocks should be at the same z index as players
        final RPObject obj = this.getEntity().getRPObject();
        if (obj.has("z-order")) {
            return obj.getInt("z-order");
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
