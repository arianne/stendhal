/**
 * 
 */
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Block;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.util.List;

/**
 * View of a pushable block
 * @author madmetzger
 */
public class Block2DView extends Entity2DView<Block> {

    @Override
    public int getZIndex() {
        // blocks should be at the same z index as players
        return 8000;
    }

    @Override
    protected void buildActions(List<String> list) {
        list.add(ActionType.LOOK.getRepresentation());
        super.buildActions(list);
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
